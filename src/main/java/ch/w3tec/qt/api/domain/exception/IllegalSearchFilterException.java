package ch.w3tec.qt.api.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalSearchFilterException extends RuntimeException {

    public IllegalSearchFilterException() {
        super("Lexical error in your search filter");
    }

}
