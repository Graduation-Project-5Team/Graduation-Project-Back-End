package comso.Team5.GP.comments.service;

import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.comments.dto.request.CommentCreateRequest;
import comso.Team5.GP.comments.dto.response.CommentCreateResponse;
import comso.Team5.GP.comments.entity.Comments;
import comso.Team5.GP.comments.repository.CommentRepository;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;


    @Transactional
    public CommentCreateResponse create(CommentCreateRequest request){

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Artworks artwork = artworkRepository.findById(request.getArtworkId())
                .orElseThrow(() -> new IllegalArgumentException("해당 작품이 존재하지 않습니다. 존재하지 않는 작품 ID : " + request.getArtworkId()));

        Comments comment = Comments.builder()
                .artwork(artwork)
                .user(user)
                .content(request.getContent())
                .build();

        Comments save = commentRepository.save(comment);

        CommentCreateResponse response = new CommentCreateResponse(save.getCommentId());

        return response;
    }
}
