package comso.Team5.GP.artworks.repository;

import comso.Team5.GP.artworks.entity.ArtworkLike;
import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.users.entity.Users;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtworkLikeRepository extends JpaRepository<ArtworkLike, Long> {

    Optional<ArtworkLike> getReferenceByUserAndArtwork(Users user, Artworks artwork);
    // User, Artworks의 객체 확인
    boolean existsByUserAndArtwork(Users user, Artworks artwork);

    // 특정 유저가 좋아요한 ArtworkLike 목록 (artwork와 이미지를 fetch join으로 한 번에 조회)
    @Query("SELECT al FROM ArtworkLike al JOIN FETCH al.artwork a LEFT JOIN FETCH a.imageUrl WHERE al.user.userId = :userId")
    List<ArtworkLike> findByUserIdWithArtwork(@Param("userId") Long userId);
}
