package comso.Team5.GP.global.exception.users;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

  private final UserExceptionCode errorCode;

  public UserException(UserExceptionCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
