package comso.Team5.GP.artworks.repository;

import comso.Team5.GP.artworks.entity.ArtworkLike;
import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.users.entity.Users;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtworkLikeRepository extends JpaRepository<ArtworkLike, Long> {

    Optional<ArtworkLike> getReferenceByUserAndArtwork(Users user, Artworks artwork);
    // User, Artworks의 객체 확인
    boolean existsByUserAndArtwork(Users user, Artworks artwork);
}
