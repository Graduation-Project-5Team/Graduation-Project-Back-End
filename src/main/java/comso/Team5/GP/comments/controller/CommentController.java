package comso.Team5.GP.comments.controller;

import comso.Team5.GP.comments.dto.request.CommentCreateRequest;
import comso.Team5.GP.comments.dto.response.CommentCreateResponse;
import comso.Team5.GP.comments.dto.response.CommentResponse;
import comso.Team5.GP.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping("/create")
    public ResponseEntity<CommentCreateResponse> create(@RequestBody CommentCreateRequest request) {
        return ResponseEntity.ok(commentService.create(request));
    }

    // 작품 ID로 댓글 목록 조회
    @GetMapping("/artwork/{artworkId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByArtwork(@PathVariable Long artworkId) {
        return ResponseEntity.ok(commentService.getCommentsByArtwork(artworkId));
    }
}
