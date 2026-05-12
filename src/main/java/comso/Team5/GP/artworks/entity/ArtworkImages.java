package comso.Team5.GP.artworks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "artwork_images")
public class ArtworkImages {

    @Id
    @Column(name = "artwork_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artworkImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", referencedColumnName = "artwork_id")
    private Artworks artwork;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "sort_order")
    private int sortOrder;

    public void setArtwork(Artworks artwork) {
        this.artwork = artwork;
    }
}
