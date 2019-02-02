package ch.w3tec.qt.api.domain.exception;

import ch.w3tec.qt.api.persistence.entity.TournamentState;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class IllegalTournamentUpdateException extends RuntimeException {

    public IllegalTournamentUpdateException(TournamentState fromState, TournamentState toState) {
        super(String.format("From state '%s' to '%s' is not allowed", fromState, toState));
    }

}
