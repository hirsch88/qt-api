package ch.w3tec.qt.api.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalGameUpdateException extends RuntimeException {

    public IllegalGameUpdateException() {
        super(String.format("It is only allowed to update games during the PLAY state"));
    }

}
