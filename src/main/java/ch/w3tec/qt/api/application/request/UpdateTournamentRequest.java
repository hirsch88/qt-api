package ch.w3tec.qt.api.application.request;

import ch.w3tec.qt.api.persistence.entity.TournamentState;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UpdateTournamentRequest {

    private String name;
    private TournamentState tournamentState;

}
