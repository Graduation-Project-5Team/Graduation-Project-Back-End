package comso.Team5.GP.departments.repository;

import comso.Team5.GP.departments.entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Departments, Long> {

    Optional<Departments> findByDeptId(Long  deptId);
}
