package comso.Team5.GP.users.controller;

import comso.Team5.GP.users.dto.UserLoginRequest;
import comso.Team5.GP.users.dto.UserLoginResponse;
import comso.Team5.GP.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/users/login")
    public ResponseEntity<UserLoginResponse> UserLogin(@RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

}
