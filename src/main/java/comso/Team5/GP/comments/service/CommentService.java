package comso.Team5.GP.comments.service;

import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.comments.dto.request.CommentCreateRequest;
import comso.Team5.GP.comments.dto.request.CommentUpdateRequest;
import comso.Team5.GP.comments.dto.response.*;
import comso.Team5.GP.comments.entity.CommentDeletedBy;
import comso.Team5.GP.global.exception.comments.CommentException;
import comso.Team5.GP.global.exception.comments.CommentExceptionCode;
import comso.Team5.GP.comments.entity.Comments;
import comso.Team5.GP.comments.repository.CommentRepository;
import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.artworks.ArtworkExceptionCode;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import comso.Team5.GP.util.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;

    // 댓글 등록 (JWT에서 userId 추출)
    public CommentCreateResponse create(CommentCreateRequest request, Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        Artworks artwork = artworkRepository.findById(request.getArtworkId())
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        Comments comment = Comments.builder()
                .artwork(artwork)
                .user(user)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        Comments save = commentRepository.save(comment);

        return new CommentCreateResponse(save.getCommentId());
    }

    // 작품 ID로 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByArtwork(Long artworkId) {

        List<Comments> comments = commentRepository.findByArtwork_ArtworkId(artworkId);

        return comments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 댓글 수정 (본인만 가능)
    public CommentResponse update(Long commentId, CommentUpdateRequest request, Long userId) {

        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionCode.NOT_FOUND_COMMENT));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new CommentException(CommentExceptionCode.FORBIDDEN_COMMENT);
        }

        if (request.getContent() != null) comment.setContent(request.getContent());

        return toResponse(comment);
    }

    // 댓글 삭제 (숨김 처리. -> 관리자, 댓글 원작자, 작품자가 삭제할 경우 리턴하는 메세지가 다르다.)
    public CommentDeletedResponse delete(Long commentId, JwtPrincipal principal) {

        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionCode.NOT_FOUND_COMMENT));

        Users user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        Artworks artwork = artworkRepository.findById(comment.getArtwork().getArtworkId())
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        // 댓글을 삭제할 사용자가 댓글의 원작자인지, 작품자인지 확인하기 위한 변수 선언
        Long currentUserId = user.getUserId();
        Long commentOwnerId = comment.getUser().getUserId();
        Long artworkOwnerId = artwork.getUsers().getUserId();

        // 관계자가 지웠을 시
        if (user.getRole() == Role.ADMIN) {
            comment.setDeleted(true);
            comment.setDeletedBy(CommentDeletedBy.ADMIN);
            comment.setDeletedAt(LocalDateTime.now());
            return new CommentDeletedResponse(
                    "관계자에 의해 삭제된 댓글입니다.", true,
                    new CommentDeletedByResponse(CommentDeletedBy.ADMIN));
        }

        // 댓글 작성자가 댓글을 지웠을 시
        if (currentUserId.equals(commentOwnerId)) {
            comment.setDeleted(true);
            comment.setDeletedBy(CommentDeletedBy.USER);
            comment.setDeletedAt(LocalDateTime.now());
            return new CommentDeletedResponse(
                    "댓글이 삭제되었습니다.", true,
                    new CommentDeletedByResponse(CommentDeletedBy.USER));
        }

        // 작품의 사용자가 댓글을 지울 시
        if (currentUserId.equals(artworkOwnerId)) {
            comment.setDeleted(true);
            comment.setDeletedBy(CommentDeletedBy.ARTWORK_OWNER);
            comment.setDeletedAt(LocalDateTime.now());
            return new CommentDeletedResponse(
                    "작품의 사용자가 댓글을 삭제했습니다.", true,
                    new CommentDeletedByResponse(CommentDeletedBy.ARTWORK_OWNER));
        }

        throw new CommentException(CommentExceptionCode.FORBIDDEN_COMMENT);
    }

    // Comments 엔티티를 CommentResponse DTO로 변환
    private CommentResponse toResponse(Comments comment) {
        return new CommentResponse(
                comment.getCommentId(),
                new CommentUserResponseDto(
                        comment.getUser().getUserId(),
                        comment.getUser().getNickname(),
                        comment.getUser().getProfileImage()
                ),
                comment.getArtwork().getArtworkId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.isDeleted(),
                comment.getDeletedBy(),
                comment.getDeletedAt()
        );
    }
}
