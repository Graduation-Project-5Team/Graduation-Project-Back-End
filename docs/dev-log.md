# 개발 작업 로그

## 1. PR #6 (Feat#5) 코드 분석

닫힌 PR `feature/artwork-read-api` 브랜치의 코드를 분석하여 문제점과 개선 방향을 파악했다.

### PR 주요 내용
- 전체 도메인 조회 API 구현 (Artworks, Comments, Departments, Exhibitions)
- Artworks 엔티티 필드 변경 (`description → content`, `startDate/endDate` 추가, `LocalDate → LocalDateTime`)
- JWT에서 userId 추출하여 작품 등록 시 보안 향상

### 발견된 문제점
| 항목 | 문제 |
|---|---|
| `ArtworkUpdateRequest`의 `id` 필드 | PathVariable로 받아야 할 id가 body에 포함 |
| `ArtworkUpdateRequest` Jackson 역직렬화 | `@NoArgsConstructor` + setter 없음 → 필드 전부 null |
| 수정/삭제 권한 오류 코드 | `INVALID_TOKEN` 사용 (의미 불일치) |
| `@Transactional` 중복 선언 | 클래스/메서드 레벨 중복 |

---

## 2. 테이블 스펙 기준 엔티티/DTO 정합성 수정

### 수정 전 문제 목록 (9항목)

| # | 파일 | 문제 |
|---|---|---|
| 1 | `Users` | `role` 필드가 `String` → ENUM 타입 필요 |
| 2 | `Users` | `created_at` 컬럼명 오타 (`create_at`) |
| 3 | `Users` | `email` 길이 제약 없음 (VARCHAR 30 필요) |
| 4 | `Users` | `nickname` 길이 제약 없음 (VARCHAR 50 필요) |
| 5 | `Exhibitions` | `name` 길이 제약 없음 (VARCHAR 100 필요) |
| 6 | `Artworks` | `createdAt`, `updatedAt` 타입 `LocalDate` → `LocalDateTime` |
| 7 | `Artworks` | 잘못된 `TODO @ManyToMany` 주석 |
| 8 | `ArtworkImages` | 엔티티 자체 없음 |
| 9 | `ArtworkResponse`, `ArtworkUpdateRequest` | `content` → `description`, `startDate`/`endDate` 제거 |

### 수정 내용

**`Role.java` 신규 생성**
```java
public enum Role {
    USER, STUDENT, ADMIN
}
```

**`Users.java`**
```java
// 1. role 타입 변경
@Enumerated(EnumType.STRING)
@Column
private Role role;

// 2. 컬럼명 오타 수정
@Column(name = "created_at", ...)

// 3. email 길이 제약
@Column(nullable = false, unique = true, length = 30)
private String email;

// 4. nickname 길이 제약
@Column(length = 50)
private String nickname;
```

**`Exhibitions.java`**
```java
@Column(length = 100)
private String name;
```

**`Artworks.java`**
```java
// LocalDate → LocalDateTime
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
// TODO @ManyToMany 주석 제거
```

**`ArtworkImages.java` 신규 생성**
```java
@Entity
@Table(name = "artwork_images")
public class ArtworkImages {
    private Long artworkImageId;
    private Artworks artwork;
    private String imageUrl;
    private int sortOrder;
}
```

**`UserService.java`**
```java
// String → Role enum 적용
Role role = dto.getEmail().endsWith(STUDENT_EMAIL_DOMAIN)
        ? Role.STUDENT
        : Role.USER;

// getRole().name()으로 String 변환
return new UserMeResponse(..., user.getRole().name(), ...);
```

**`ArtworkCreateRequest.java`**
```java
// likeCount 제거 (DB 스펙 DEFAULT 0, 서버에서 고정)
// @AllArgsConstructor → @NoArgsConstructor + @Setter
```

**`ArtworkService.java`**
```java
Artworks artwork = Artworks.builder()
        .likeCount(0)                      // 0 고정
        .createdAt(LocalDateTime.now())    // 서버 자동 설정
        .updatedAt(LocalDateTime.now())
        .build();
```

**`ArtworkResponse.java`, `ArtworkUpdateRequest.java`**
```java
// content → description
// startDate, endDate 제거
```

---

## 3. 조회 API 구현 (`feature/artwork-read-api`)

### 구현된 엔드포인트

| 메서드 | 엔드포인트 | 설명 |
|---|---|---|
| GET | `/api/artworks` | 작품 목록 조회 (페이지네이션, 기본 20개 최신순) |
| GET | `/api/artworks/{artworkId}` | 작품 단건 조회 |
| GET | `/api/comments/artwork/{artworkId}` | 작품별 댓글 목록 조회 |
| GET | `/api/departments` | 학과 전체 목록 조회 |
| GET | `/api/departments/{deptId}` | 학과 단건 조회 |
| GET | `/api/exhibitions` | 전시 전체 목록 조회 |
| GET | `/api/exhibitions/{exhiId}` | 전시 단건 조회 |

### `ArtworkController` — JWT 인증 흐름

```java
@PostMapping("/create")
public ResponseEntity<ArtworkCreateResponse> create(
        @RequestBody ArtworkCreateRequest request,
        HttpServletRequest httpRequest) {

    JwtPrincipal principal = extractPrincipal(httpRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(artworkService.create(request, principal.userId()));
}
```

- `request` : 클라이언트가 body에 담은 작품 정보 (JSON → DTO 역직렬화)
- `principal.userId()` : JWT 토큰에서 추출한 사용자 PK
- `userId`를 body 대신 JWT에서 추출함으로써 클라이언트의 임의 조작 방지

### `ArtworkService` — toResponse 변환

```java
private ArtworkResponse toResponse(Artworks artwork) {
    return new ArtworkResponse(
            artwork.getArtworkId(),
            artwork.getUsers().getUserId(),
            artwork.getExhibitions() != null ? artwork.getExhibitions().getExhiId() : null,
            artwork.getTitle(),
            artwork.getDescription(),
            artwork.getLikeCount(),
            artwork.getCreatedAt(),
            artwork.getUpdatedAt()
    );
}
```

---

## 4. 발견 및 수정된 버그

### 4-1. `ArtworkCreateRequest` Jackson 역직렬화 실패

**증상**
```
The given id must not be null
at ArtworkService.create(ArtworkService.java:52)
```

**원인**

`@NoArgsConstructor`만 있고 `@Setter`가 없어 Jackson이 빈 객체를 생성한 뒤 필드에 값을 주입하지 못함. 모든 필드가 null이 되어 `findById(null)` 호출.

```
Jackson 동작:
1. @NoArgsConstructor로 빈 객체 생성
2. setExhiId() 호출 시도 → setter 없음 → 무시
3. 결과: exhiId = null → findById(null) → 에러
```

**수정**
```java
@Getter
@Setter        // 추가
@NoArgsConstructor
public class ArtworkCreateRequest { ... }
```

### 4-2. `ArtworkController` import 경로 오류

**원인**
```java
// 잘못된 경로
import comso.Team5.GP.users.exception.UserException;

// 올바른 경로
import comso.Team5.GP.global.exception.users.UserException;
```

---

## 5. 미해결 이슈

### ~~5-1. JWT `userId` null 문제~~ ✅ 팀원 PR (#10)에서 해결

**기존 문제**
```java
// 토큰 생성: 문자열 로그인 id를 "id" 클레임에 저장
generateToken(String id)
payloadJson = ... + ",\"id\":\"" + id + "\""  // → "id": "admin" (문자열)

// 토큰 파싱: "id" 클레임을 Long으로 읽음 → null
idClaim.asLong()  // "admin" → null
```

**해결 (PR #10)**
```java
// generateToken → generateAccess(Long userId, String id)로 변경
// "id" 클레임에 숫자 userId(PK) 저장
payloadJson = ... + ",\"id\":" + userId + ...  // → "id": 1 (숫자)
```

### 5-2. `ArtworkCreateRequest`의 미사용 `userId` 필드

서비스에서 JWT 추출 userId를 사용하므로 `request.getUserId()`는 호출되지 않음. 추후 제거 예정.

### 5-3. 작품 수정/삭제 API 미구현

`update`, `delete` 엔드포인트는 이후 PR에서 구현 예정.

---

## 6. 팀원 PR #10 (Feat#6 user status) 변경사항

### 6-1. JWT 구조 개선

| 항목 | 변경 전 | 변경 후 |
|---|---|---|
| Access 토큰 생성 | `generateToken(String id)` | `generateAccess(Long userId, String id)` |
| Refresh 토큰 생성 | `generateRefresh(Long userId)` | `generateRefresh(Long userId, String id)` |
| `"id"` 클레임 값 | 문자열 로그인 id | 숫자 userId (PK) |
| `JwtPrincipal` 위치 | `JwtUtil` 내부 record | 별도 클래스 `JwtPrincipal.java` 로 분리 |

추가된 메서드:
- `validate(String token)` — 토큰 유효성 검사
- `getUserIdFromRefreshToken(String)` — 리프레시 토큰에서 userId 추출
- `getRefreshExpirationsSeconds()` — 리프레시 토큰 만료 시간 반환

### 6-2. Refresh Token 기능 추가

**`RefreshTokenService.java` 신규 생성**

토큰 재발급 시 토큰 로테이션 전략 적용:
1. 리프레시 토큰 유효성 검사
2. DB 저장 토큰과 일치 여부 확인 (탈취 감지)
3. 불일치 시 DB 토큰 무효화 후 예외
4. 일치 시 새 Access/Refresh 토큰 발급 후 DB 갱신

**추가된 엔드포인트**

| 메서드 | 엔드포인트 | 설명 |
|---|---|---|
| POST | `/api/users/reissue` | Access/Refresh 토큰 재발급 |

**추가된 예외 코드**
```java
REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 리프레시 토큰입니다.")
```

### 6-3. UserService / UserController 변경

- `getUserMe(Long userId, String id)` → `getUserMe(Long userId)` : `findByCheckId` → `findById`로 변경
- `logout` : `RefreshTokenService.deleteByUser(user)` 위임
- `UserController.getUserMe`, `logout` : JWT에서 추출한 `userId`만 전달하도록 단순화

### 6-4. schema.sql 수정

```sql
-- 수정 전 (created_at 컬럼명 명시로 인한 오타 에러 발생)
INSERT INTO Users (id, email, nickname, password, role, dept_id, is_verified, created_at) VALUES (...)

-- 수정 후 (컬럼명 생략, 순서 기반 삽입)
INSERT INTO Users VALUES (...)
```

---

## 7. 브랜치 전략

```
dev
 └── refactor/entity-dto-sync     → 엔티티/DTO 스펙 정합성 수정
       ↓ PR #8 merge
dev
 └── feature/artwork-read-api     → 전체 도메인 조회 API 구현
       ↓ PR #9 merge
dev
 └── feature/user-status (팀원)   → JWT 개선 및 Refresh Token 기능
       ↓ PR #10 merge
dev
```
