package comso.Team5.GP.exhibitions.entity;

import comso.Team5.GP.departments.entity.Departments;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exhibitions {

    @Id
    @Column(name = "exhi_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exhiId;

// 학과 테이블과 연결 (N : 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", referencedColumnName = "dept_id")
    private Departments departments;

    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "thumbnail_image")
    private String thumbnailImage;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

}
