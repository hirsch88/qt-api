package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.UpdateGameRequest;
import ch.w3tec.qt.api.domain.exception.IllegalGameUpdateException;
import ch.w3tec.qt.api.domain.exception.IllegalPaginationRequest;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

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
        try {
            return gameRepository.findByTournament(tournament, pageRequest);
        } catch (PropertyReferenceException exception) {
            LOGGER.warn("FAILED findByTournament() => {}", exception.getMessage());
            throw new IllegalPaginationRequest(pageRequest);
        }
    }

    public Game create(Game game) {
        LOGGER.info("STARTED update(game={})", game);
        Game savedGame = gameRepository.save(game);
        LOGGER.info("FINISHED update(game={}) => {}", game, savedGame);
        return savedGame;
    }

    public Game update(UUID id, UpdateGameRequest updateGameRequest) {
        LOGGER.info("STARTED update(gameId={}, updateGameRequest={})", id, updateGameRequest);
        Game game = findById(id);
        if (!game.getTournament().getState().equals(TournamentState.PLAYABLE)) {
            LOGGER.warn("FAILED update(gameId={}) => IllegalGameUpdateException");
            throw new IllegalGameUpdateException();
        }

        Game savedGame = gameRepository.save(game.toBuilder()
                .hostScore(updateGameRequest.getHostScore())
                .guestScore(updateGameRequest.getGuestScore())
                .build());
        LOGGER.info("FINISHED update(gameId={}) => ", savedGame.getId(), savedGame);
        return savedGame;
    }

    public void deleteByTournament(Tournament tournament) {
        LOGGER.info("STARTED deleteByTournament(tournament={})", tournament);
        gameRepository.deleteByTournament(tournament);
        LOGGER.info("FINISHED deleteByTournament(tournament={})", tournament);
    }
}
