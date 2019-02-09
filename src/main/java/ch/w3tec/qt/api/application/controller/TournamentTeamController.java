package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.UpdateTeamRequest;
import ch.w3tec.qt.api.application.response.PageResponse;
import ch.w3tec.qt.api.domain.service.TournamentService;
import ch.w3tec.qt.api.persistence.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentTeamController {

    private final TournamentService tournamentService;

    @Autowired
    public TournamentTeamController(
            TournamentService tournamentService
    ) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{visitorOrAdminId}/teams")
    public ResponseEntity<PageResponse<Team>> findTeamsByTournamentId(@PathVariable("visitorOrAdminId") UUID visitorOrAdminId,
                                                                      Pageable pageRequest) {
        Page<Team> page = tournamentService.findTeamsByVisitorIdOrAdminId(visitorOrAdminId, pageRequest);
        PageResponse<Team> pageResponse = PageResponse.build(page);
        return ResponseEntity.ok().body(pageResponse);
    }

    @PostMapping("/{visitorOrAdminId}/teams")
    public ResponseEntity<Team> addTeamToTournament(@PathVariable("visitorOrAdminId") UUID visitorOrAdminId,
                                                    @Valid @RequestBody CreateTeamRequest createTeamRequest) {
        Team team = tournamentService.addTeamToTournament(visitorOrAdminId, createTeamRequest);
        return ResponseEntity.ok().body(team);
    }

    @PutMapping("/{visitorOrAdminId}/teams/{teamId}")
    public ResponseEntity<Team> updateTeamOfTournament(@PathVariable("visitorOrAdminId") UUID visitorOrAdminId,
                                                       @PathVariable("teamId") UUID teamId,
                                                       @Valid @RequestBody UpdateTeamRequest updateTeamRequest) {
        Team team = tournamentService.updateTeamOfTournament(visitorOrAdminId, teamId, updateTeamRequest);
        return ResponseEntity.ok().body(team);
    }

    @DeleteMapping("/{visitorOrAdminId}/teams/{teamId}")
    public ResponseEntity removeTeamFromTournament(@PathVariable("visitorOrAdminId") UUID visitorOrAdminId,
                                                   @PathVariable("teamId") UUID teamId) {
        tournamentService.removeTeamFromTournament(visitorOrAdminId, teamId);
        return ResponseEntity.noContent().build();
    }

}
