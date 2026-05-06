package comso.Team5.GP.departments.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Departments {

    @Id
    @Column(name = "dept_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @Column
    private String name;

    public Departments (String name) {
        this.name = name;
    }

}
