package comso.Team5.GP.artworks.service;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.entity.Artworks;
import comso.Team5.GP.artworks.repository.ArtworkRepository;
import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.exhibitions.repository.ExhibitionRepository;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ArtworkCreateResponse create(ArtworkCreateRequest request) {

        Exhibitions exhibitions = exhibitionRepository.findById(request.getExhiId())
                .orElseThrow(() -> new IllegalArgumentException("해당 전시회가 존재하지 않습니다. 전시회 ID : " + request.getExhiId()));

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Artworks artwork = Artworks.builder().title(request.getTitle())
                .exhibitions(exhibitions)
                .users(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .likeCount(request.getLikeCount())
                .build();

        Artworks save = artworkRepository.save(artwork);

        ArtworkCreateResponse response = new ArtworkCreateResponse(save.getArtworkId());

        return response;
    }
}
