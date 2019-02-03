package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.application.request.UpdateTeamRequest;
import ch.w3tec.qt.api.domain.service.TeamService;
import ch.w3tec.qt.api.persistence.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> findById(@PathVariable("id") UUID id) {
        Team team = teamService.findById(id);
        return ResponseEntity.ok().body(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") UUID id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable("id") UUID id, @RequestBody UpdateTeamRequest updateTeamRequest) {
        Team team = teamService.update(id, updateTeamRequest);
        return ResponseEntity.ok().body(team);
    }

}
