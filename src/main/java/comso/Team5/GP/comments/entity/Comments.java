package comso.Team5.GP.comments.entity;

import comso.Team5.GP.artworks.entity.Artworks;
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
public class Comments {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    // 작품 테이블과 연결 (N : 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", referencedColumnName = "artwork_id")
    private Artworks artwork;

    // 사용자 테이블과 연결 (N : 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
