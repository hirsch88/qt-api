package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.UpdateTournamentRequest;
import ch.w3tec.qt.api.application.response.PageResponse;
import ch.w3tec.qt.api.domain.exception.IllegalSearchFilterException;
import ch.w3tec.qt.api.domain.service.GameService;
import ch.w3tec.qt.api.domain.service.TeamService;
import ch.w3tec.qt.api.domain.service.TournamentService;
import ch.w3tec.qt.api.application.request.CreateTournamentRequest;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.repository.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PageResponse<Tournament>> findAll(@RequestParam(value = "search", required = false) String search, Pageable pageRequest) {
        Specification<Tournament> spec = Specification.where(null);

        if (Strings.isNotEmpty(search)) {
            try {
            Node rootNode = new RSQLParser().parse(search);
            spec = rootNode.accept(new CustomRsqlVisitor<>());
            } catch (RSQLParserException e){
                throw new IllegalSearchFilterException();
            }
        }

        Page<Tournament> page = tournamentService.findAll(spec, pageRequest);
        PageResponse<Tournament> pageResponse = PageResponse.build(page);
        return ResponseEntity.ok().body(pageResponse);
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
    public ResponseEntity<PageResponse<Game>> findGamesByTournamentId(@PathVariable("id") UUID id, Pageable pageRequest) {
        Page<Game> page = gameService.findGamesByTournamentId(id, pageRequest);
        PageResponse<Game> pageResponse = PageResponse.build(page);
        return ResponseEntity.ok().body(pageResponse);
    }

    @GetMapping("/{id}/teams")
    public ResponseEntity<PageResponse<Team>> findTeamsByTournamentId(@PathVariable("id") UUID id, Pageable pageRequest) {
        Page<Team> page = teamService.findTeamsByTournamentId(id, pageRequest);
        PageResponse<Team> pageResponse = PageResponse.build(page);
        return ResponseEntity.ok().body(pageResponse);
    }

    @PostMapping("/{id}/teams")
    public ResponseEntity<Team> createTeamForTournament(@PathVariable("id") UUID id, @RequestBody CreateTeamRequest createTeamRequest) {
        Team team = teamService.addTeamToTournament(id, createTeamRequest);
        return ResponseEntity.ok().body(team);
    }

}
