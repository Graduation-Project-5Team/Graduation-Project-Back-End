package comso.Team5.GP.exhibitions.controller;

import comso.Team5.GP.exhibitions.dto.reponse.ExhibitionCreateResponse;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionCreateRequest;
import comso.Team5.GP.exhibitions.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/exhibitions")
@RestController
public class ExhibitionController {

    private final ExhibitionService exhibitionsService;

    @PostMapping("/create")
    public ResponseEntity<ExhibitionCreateResponse> create(@RequestBody ExhibitionCreateRequest request) {
        return ResponseEntity.ok(exhibitionsService.create(request));
    }
}
