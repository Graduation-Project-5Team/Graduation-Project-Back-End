package comso.Team5.GP.global.exception.artworks;

import lombok.Getter;

@Getter
public class ArtworkException extends RuntimeException {

    private final ArtworkExceptionCode errorCode;

    public ArtworkException(ArtworkExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
