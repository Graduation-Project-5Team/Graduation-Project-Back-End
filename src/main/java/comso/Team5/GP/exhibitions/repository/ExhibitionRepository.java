package comso.Team5.GP.exhibitions.repository;

import comso.Team5.GP.exhibitions.entity.Exhibitions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibitions, Long> {
}
