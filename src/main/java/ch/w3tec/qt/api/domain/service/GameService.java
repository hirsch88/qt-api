package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.CreateTournamentRequest;
import ch.w3tec.qt.api.application.request.UpdateGameRequest;
import ch.w3tec.qt.api.domain.exception.IllegalGameUpdateException;
import ch.w3tec.qt.api.domain.exception.IllegalTeamUpdateException;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.GameRepository;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GameService {

//    private static final Logger LOG = LoggerFactory.getLogger(TournamentService.class);

    private final GameRepository gameRepository;

    @Autowired
    public GameService(
            GameRepository gameRepository
    ) {
        this.gameRepository = gameRepository;
    }

    public Game findById(UUID id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", id.toString()));
    }

    protected Page<Game> findByTournament(Tournament tournament, Pageable pageRequest) {
        return gameRepository.findByTournament(tournament, pageRequest);
    }

    public Game update(UUID id, UpdateGameRequest updateGameRequest) {
        Game game = findById(id);
        if (!game.getTournament().getState().equals(TournamentState.READY_TO_PLAY)) {
            throw new IllegalGameUpdateException();
        }

        return gameRepository.save(game.toBuilder()
                .hostScore(updateGameRequest.getHostScore())
                .guestScore(updateGameRequest.getGuestScore())
                .build());
    }
}
