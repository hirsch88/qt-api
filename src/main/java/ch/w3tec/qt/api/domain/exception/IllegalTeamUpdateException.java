package ch.w3tec.qt.api.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalTeamUpdateException extends RuntimeException {

    public IllegalTeamUpdateException() {
        super(String.format("A team can only be updated during the OPEN state"));
    }

}
