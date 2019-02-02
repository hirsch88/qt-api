package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.domain.service.TournamentService;
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

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping()
    public ResponseEntity<List<Tournament>> findAll() {
        List<Tournament> tournaments = tournamentService.findAll();
        return ResponseEntity.ok().body(tournaments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> findById(@PathVariable("id") UUID id) {
        Tournament tournament = tournamentService.findById(id);
        return ResponseEntity.ok().body(tournament);
    }

}
