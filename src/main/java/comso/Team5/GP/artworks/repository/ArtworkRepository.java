package comso.Team5.GP.artworks.repository;

import comso.Team5.GP.artworks.entity.Artworks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artworks, Long> {
}
