package ch.w3tec.qt.api.application.request;

import ch.w3tec.qt.api.persistence.entity.TournamentState;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateTournamentRequest {

    @NotNull
    private String name;

    @NotNull
    private TournamentState state;

}
