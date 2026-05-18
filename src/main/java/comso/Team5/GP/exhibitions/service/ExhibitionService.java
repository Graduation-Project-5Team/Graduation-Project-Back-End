package comso.Team5.GP.exhibitions.service;

import comso.Team5.GP.departments.entity.Departments;
import comso.Team5.GP.departments.repository.DepartmentRepository;
import comso.Team5.GP.exhibitions.dto.reponse.ExhibitionCreateResponse;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionCreateRequest;
import comso.Team5.GP.exhibitions.dto.request.ExhibitionUpdateRequest;
import comso.Team5.GP.exhibitions.entity.Exhibitions;
import comso.Team5.GP.exhibitions.repository.ExhibitionRepository;
import comso.Team5.GP.global.exception.departments.DepartmentException;
import comso.Team5.GP.global.exception.departments.DepartmentExceptionCode;
import comso.Team5.GP.global.exception.exhibitions.ExhibitionException;
import comso.Team5.GP.global.exception.exhibitions.ExhibitionExceptionCode;
import comso.Team5.GP.global.exception.users.UserException;
import comso.Team5.GP.global.exception.users.UserExceptionCode;
import comso.Team5.GP.users.entity.Role;
import comso.Team5.GP.users.entity.Users;
import comso.Team5.GP.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ExhibitionService {

    private final ExhibitionRepository exhibitionsRepository;
    private final DepartmentRepository departmentsRepository;
    private final UserRepository userRepository;

    // applciation.yaml 파일에 경로를 변수로 지정
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 전시 등록 (관리자만 가능)
    public ExhibitionCreateResponse create(ExhibitionCreateRequest request, Long userId, MultipartFile image) {

        checkAdmin(userId);

        Departments department = departmentsRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));

        // 폴더가 존재하지 않을 시 생성
        File dir = new File(uploadDir);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String filePath = imageSave(image, dir);

        Exhibitions exhibition = Exhibitions.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .thumbnailImage(filePath)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .departments(department)
                .build();

        return toResponse(exhibitionsRepository.save(exhibition));
    }

    // 전시 단건 조회
    @Transactional(readOnly = true)
    public ExhibitionCreateResponse getExhibition(Long exhiId) {

        Exhibitions exhibition = exhibitionsRepository.findById(exhiId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        return toResponse(exhibition);
    }

    // 전체 전시 목록 조회
    @Transactional(readOnly = true)
    public List<ExhibitionCreateResponse> getAllExhibitions() {

        return exhibitionsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 전시 수정 (관리자만 가능)
    @Transactional
    public ExhibitionCreateResponse update(Long exhiId,
                                           ExhibitionUpdateRequest request,
                                           Long userId,
                                           MultipartFile image) {

        checkAdmin(userId);

        Exhibitions exhibition = exhibitionsRepository.findById(exhiId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        // request 없이 사진만 바꼈을 경우
        if (request != null) {
            // 요청에 포함된 필드만 수정 (null인 필드는 기존 값 유지)
            if (request.getName() != null) exhibition.setName(request.getName());
            if (request.getDescription() != null) exhibition.setDescription(request.getDescription());
            if (request.getLocation() != null) exhibition.setLocation(request.getLocation());
            if (request.getStartDate() != null) exhibition.setStartDate(request.getStartDate());
            if (request.getEndDate() != null) exhibition.setEndDate(request.getEndDate());
            if (request.getDepartmentId() != null) {
                // 학과 ID 변경 시 존재 여부 검증 후 교체
                Departments department = departmentsRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new DepartmentException(DepartmentExceptionCode.NOT_FOUND_NAME));
                exhibition.setDepartments(department);
            }
        }

        if (image != null && !image.isEmpty()) {
            // 기존 썸네일 이미지 물리적인 경로 파일 삭제
            deletePhysicalFile(exhibition.getThumbnailImage());

            // 새 이미지 저장 및 정보 업데이트
            File dir = new File(uploadDir);
            exhibition.setThumbnailImage(imageSave(image, dir));
        }

        return toResponse(exhibition);
    }

    // 전시 삭제 (관리자만 가능)
    public void delete(Long exhiId, Long userId) {

        checkAdmin(userId);

        Exhibitions exhibition = exhibitionsRepository.findById(exhiId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionExceptionCode.NOT_FOUND_EXHIBITION));

        // 썸네일 이미지 파일 삭제
        deletePhysicalFile(exhibition.getThumbnailImage());

        exhibitionsRepository.delete(exhibition);
    }

    private void checkAdmin(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionCode.USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new ExhibitionException(ExhibitionExceptionCode.NOT_ADMIN);
        }
    }

    private ExhibitionCreateResponse toResponse(Exhibitions exhibition) {
        return new ExhibitionCreateResponse(
                exhibition.getExhiId(),
                exhibition.getName(),
                exhibition.getDescription(),
                exhibition.getLocation(),
                exhibition.getThumbnailImage(),
                exhibition.getStartDate(),
                exhibition.getEndDate(),
                exhibition.getDepartments().getDeptId()
        );
    }

    private void deletePhysicalFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        try {
            // /uploads/ 제거한 실제 파일명
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            File fileToDelete = new File(uploadDir, fileName);
            // 변수로 지정한 파일 디렉터리에서 삭제
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        } catch (Exception e) {
            System.err.println("Failed to delete image file: " + imageUrl + " - " + e.getMessage());
        }
    }

    private String imageSave(MultipartFile image, File dir) {
        String dbFilePath = "";
        if(image != null || !image.isEmpty()) {
            try{
                // 이미지로 받아온 객체를 변수명에 지정
                String originFileName = image.getOriginalFilename();

                // UUID 랜덤한 문자열과 실제 이미지 이름을 합침
                String savedFileName = UUID.randomUUID().toString() + "_" + originFileName;

                // 프로제트 루트 디렉터리/images/exhibitions/UUID_이미지파일명
                File serverFile = new File(dir + File.separator +savedFileName);
                image.transferTo(serverFile);

                // db에 최종적으로 저장할 문자열 변수
                dbFilePath = savedFileName;

            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
            }
        }
        return dbFilePath;
    }
}
