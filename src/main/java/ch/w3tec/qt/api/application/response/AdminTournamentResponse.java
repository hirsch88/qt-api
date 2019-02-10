package ch.w3tec.qt.api.application.response;

import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdminTournamentResponse {
    private UUID id;
    private UUID adminId;
    private UUID visitorId;
    private String name;
    private TournamentState state;

    public AdminTournamentResponse(Tournament tournament) {
        this.id = tournament.getId();
        this.adminId = tournament.getAdminId();
        this.visitorId = tournament.getVisitorId();
        this.name = tournament.getName();
        this.state = tournament.getState();
    }
}
