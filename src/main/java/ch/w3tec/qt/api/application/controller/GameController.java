package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.application.request.UpdateGameRequest;
import ch.w3tec.qt.api.domain.service.GameService;
import ch.w3tec.qt.api.persistence.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> findById(@PathVariable("id") UUID id) {
        Game game = gameService.findById(id);
        return ResponseEntity.ok().body(game);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> update(@PathVariable("id") UUID id, @Valid @RequestBody UpdateGameRequest updateGameRequest) {
        Game game = gameService.update(id, updateGameRequest);
        return ResponseEntity.ok().body(game);
    }

}
