package comso.Team5.GP.artworks.entity;

import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtworkImages> imageUrl = new ArrayList<>();

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "like_count")
    private int likeCount = 0;

    @Column(name = "views")
    private int views;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계 편의 메서드
    public void setImages(List<ArtworkImages> newImages) {
        this.imageUrl.clear();

        if (newImages == null) {
            return;
        }

        for (ArtworkImages image : newImages) {
            image.setArtwork(this);
            this.imageUrl.add(image);
        }
    }

    public void addLike() {
        this.likeCount = likeCount + 1;
    }

    public void removeLike() {
        this.likeCount = likeCount - 1;
    }
}