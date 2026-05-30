package comso.Team5.GP.comments.repository;

import comso.Team5.GP.comments.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {

    // 작품 ID로 해당 작품의 댓글 목록 조회
    List<Comments> findByArtwork_ArtworkId(Long artworkId);

    // 관리자 페이지 댓글 목록 조회
    @Query("SELECT c FROM Comments c JOIN c.user u WHERE u.nickname LIKE %:keyword%")
    Page<Comments> findByNicknameContaing(String keyword, Pageable pageable);
}
