package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.application.request.UpdateGameRequest;
import ch.w3tec.qt.api.application.response.PageResponse;
import ch.w3tec.qt.api.domain.service.TournamentService;
import ch.w3tec.qt.api.persistence.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentGameController {

    private final TournamentService tournamentService;

    @Autowired
    public TournamentGameController(
            TournamentService tournamentService
    ) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{visitorOrAdminId}/games")
    public ResponseEntity<PageResponse<Game>> findGamesByTournamentId(@PathVariable("visitorOrAdminId") UUID visitorOrAdminId,
                                                                      Pageable pageRequest) {
        Page<Game> page = tournamentService.findGamesByVisitorIdOrAdminId(visitorOrAdminId, pageRequest);
        PageResponse<Game> pageResponse = PageResponse.build(page);
        return ResponseEntity.ok().body(pageResponse);
    }

    @PutMapping("/{adminId}/games/{gameId}")
    public ResponseEntity<Game> updateGamesOfTournamentId(@PathVariable("adminId") UUID adminId,
                                                          @PathVariable("gameId") UUID gameId,
                                                          @Valid @RequestBody UpdateGameRequest updateGameRequest) {
        Game game = tournamentService.updateGamesByAdminId(adminId, gameId, updateGameRequest);
        return ResponseEntity.ok().body(game);
    }

}
