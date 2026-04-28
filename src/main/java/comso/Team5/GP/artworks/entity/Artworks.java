package comso.Team5.GP.artworks.entity;

import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

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
    private String description;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
