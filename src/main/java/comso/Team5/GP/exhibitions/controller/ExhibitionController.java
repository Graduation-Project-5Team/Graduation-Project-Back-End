package comso.Team5.GP.exhibitions.controller;

import comso.Team5.GP.exhibitions.dto.reponse.ExhibitionCreateResponse;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionCreateRequest;
import comso.Team5.GP.exhibitions.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/exhibitions")
@RestController
public class ExhibitionController {

    private final ExhibitionService exhibitionsService;

    // 전시 등록
    @PostMapping("/create")
    public ResponseEntity<ExhibitionCreateResponse> create(@RequestBody ExhibitionCreateRequest request) {
        return ResponseEntity.ok(exhibitionsService.create(request));
    }

    // 전시 단건 조회
    @GetMapping("/{exhiId}")
    public ResponseEntity<ExhibitionCreateResponse> getExhibition(@PathVariable Long exhiId) {
        return ResponseEntity.ok(exhibitionsService.getExhibition(exhiId));
    }

    // 전체 전시 목록 조회
    @GetMapping
    public ResponseEntity<List<ExhibitionCreateResponse>> getAllExhibitions() {
        return ResponseEntity.ok(exhibitionsService.getAllExhibitions());
    }
}
