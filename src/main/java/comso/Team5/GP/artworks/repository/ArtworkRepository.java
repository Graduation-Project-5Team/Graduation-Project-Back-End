package comso.Team5.GP.artworks.repository;

import comso.Team5.GP.artworks.entity.Artworks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artworks, Long> {

    // 전시 ID로 해당 전시의 작품 목록 조회
    List<Artworks> findByExhibitions_ExhiId(Long exhiId);
}
