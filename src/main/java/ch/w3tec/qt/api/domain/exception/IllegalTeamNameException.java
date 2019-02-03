package ch.w3tec.qt.api.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalTeamNameException extends RuntimeException {

    public IllegalTeamNameException() {
        super(String.format("Team name must be unique"));
    }

}
