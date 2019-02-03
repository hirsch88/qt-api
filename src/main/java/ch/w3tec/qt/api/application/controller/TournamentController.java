package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.UpdateTournamentRequest;
import ch.w3tec.qt.api.domain.service.GameService;
import ch.w3tec.qt.api.domain.service.TeamService;
import ch.w3tec.qt.api.domain.service.TournamentService;
import ch.w3tec.qt.api.application.request.CreateTournamentRequest;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final GameService gameService;
    private final TeamService teamService;

    @Autowired
    public TournamentController(
            TournamentService tournamentService,
            GameService gameService,
            TeamService teamService
    ) {
        this.tournamentService = tournamentService;
        this.gameService = gameService;
        this.teamService = teamService;
    }

    @GetMapping()
    public ResponseEntity<List<Tournament>> findAll() {
        List<Tournament> tournaments = tournamentService.findAll();
        return ResponseEntity.ok().body(tournaments);
    }

    @PostMapping()
    public ResponseEntity<Tournament> create(@RequestBody CreateTournamentRequest createTournamentRequest) {
        Tournament tournament = tournamentService.create(createTournamentRequest);
        return ResponseEntity.ok().body(tournament);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> findById(@PathVariable("id") UUID id) {
        Tournament tournament = tournamentService.findById(id);
        return ResponseEntity.ok().body(tournament);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable("id") UUID id, @RequestBody UpdateTournamentRequest updateTournamentRequest) {
        Tournament tournament = tournamentService.update(id, updateTournamentRequest);
        return ResponseEntity.ok().body(tournament);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") UUID id) {
        tournamentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/games")
    public ResponseEntity<List<Game>> findGamesByTournamentId(@PathVariable("id") UUID id) {
        List<Game> games = gameService.findGamesByTournamentId(id);
        return ResponseEntity.ok().body(games);
    }

    @GetMapping("/{id}/teams")
    public ResponseEntity<List<Team>> findTeamsByTournamentId(@PathVariable("id") UUID id) {
        List<Team> teams = teamService.findTeamsByTournamentId(id);
        return ResponseEntity.ok().body(teams);
    }

    @PostMapping("/{id}/teams")
    public ResponseEntity<Team> createTeamForTournament(@PathVariable("id") UUID id, @RequestBody CreateTeamRequest createTeamRequest) {
        Team team = teamService.addTeamToTournament(id, createTeamRequest);
        return ResponseEntity.ok().body(team);
    }

}
