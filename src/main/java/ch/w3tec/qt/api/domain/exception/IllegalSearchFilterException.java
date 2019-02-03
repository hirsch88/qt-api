package ch.w3tec.qt.api.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalSearchFilterException extends RuntimeException {

    private String bubu = "askdjf";

    public IllegalSearchFilterException() {
        super(String.format("Lexical error in your search filter"));
    }

}
