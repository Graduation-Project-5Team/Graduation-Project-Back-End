package comso.Team5.GP.artworks.entity;

import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Artworks {

    @Id
    @Column(name = "artwork_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long artworkId;

    // 유저 엔티티와 연결 (N : 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users users;

    // 전시 엔티티와 연결 (N : 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhi_id", referencedColumnName = "exhi_id")
    private Exhibitions exhibitions;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "like_count")
    private int likeCount;

    // 사용자가 입력하는 작품 시작일
    @Column(name = "start_date")
    private LocalDate startDate;

    // 사용자가 입력하는 작품 종료일
    @Column(name = "end_date")
    private LocalDate endDate;

    // 서버에서 자동 기록하는 등록 시각
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 서버에서 자동 기록하는 최종 수정 시각
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
