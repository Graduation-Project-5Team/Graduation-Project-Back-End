package comso.Team5.GP.comments.service;

import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.comments.dto.request.CommentCreateRequest;
import comso.Team5.GP.comments.dto.response.CommentCreateResponse;
import comso.Team5.GP.comments.dto.response.CommentResponse;
import comso.Team5.GP.comments.entity.Comments;
import comso.Team5.GP.comments.repository.CommentRepository;
import comso.Team5.GP.global.exception.artworks.ArtworkException;
import comso.Team5.GP.global.exception.artworks.ArtworkExceptionCode;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;

    // 댓글 등록
    @Transactional
    public CommentCreateResponse create(CommentCreateRequest request){

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));

        Artworks artwork = artworkRepository.findById(request.getArtworkId())
                .orElseThrow(() -> new ArtworkException(ArtworkExceptionCode.NOT_FOUND_ARTWORK));

        Comments comment = Comments.builder()
                .artwork(artwork)
                .user(user)
                .content(request.getContent())
                .build();

        Comments save = commentRepository.save(comment);

        CommentCreateResponse response = new CommentCreateResponse(save.getCommentId());

        return response;
    }

    // 작품 ID로 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByArtwork(Long artworkId) {

        List<Comments> comments = commentRepository.findByArtwork_ArtworkId(artworkId);

        return comments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Comments 엔티티를 CommentResponse DTO로 변환
    private CommentResponse toResponse(Comments comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getUser().getUserId(),
                comment.getArtwork().getArtworkId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
