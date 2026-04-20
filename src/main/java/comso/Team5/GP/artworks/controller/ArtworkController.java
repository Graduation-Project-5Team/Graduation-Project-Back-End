package comso.Team5.GP.artworks.controller;

import comso.Team5.GP.artworks.dto.request.ArtworkCreateRequest;
import comso.Team5.GP.artworks.dto.response.ArtworkCreateResponse;
import comso.Team5.GP.artworks.service.ArtworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/artworks")
@RestController
public class ArtworkController {

    private final ArtworkService artworkService;

    @PostMapping("/create")
    public ResponseEntity<ArtworkCreateResponse> create(@RequestBody ArtworkCreateRequest request) {
        return ResponseEntity.ok(artworkService.create(request));
    }

}
