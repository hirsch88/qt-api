package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.UpdateGameRequest;
import ch.w3tec.qt.api.domain.exception.IllegalGameUpdateException;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository mockGameRepository;

    private UUID id;
    private UpdateGameRequest updateGameRequest;

    @Before
    public void setUp() {
        id = UUID.randomUUID();
        Game savedGame = Game.builder()
                .id(id)
                .tournament(Tournament.builder()
                        .state(TournamentState.PLAYABLE)
                        .build())
                .build();

        updateGameRequest = UpdateGameRequest.builder()
                .hostScore(2)
                .guestScore(1)
                .build();

        when(mockGameRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(savedGame));

        when(mockGameRepository.save(ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void update_validGame_setsScores() {
        Game game = gameService.update(id, updateGameRequest);

        assertThat(game.getId()).isEqualByComparingTo(id);
        assertThat(game.getHostScore()).isEqualTo(updateGameRequest.getHostScore());
        assertThat(game.getGuestScore()).isEqualTo(updateGameRequest.getGuestScore());
        verify(mockGameRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void update_nonexistentTournament_throwsIllegalGameUpdateException() {
        when(mockGameRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        gameService.update(id, updateGameRequest);
        verify(mockGameRepository, times(0)).save(ArgumentMatchers.any());
    }

    @Test(expected = IllegalGameUpdateException.class)
    public void update_aOpenTournament_throwsIllegalGameUpdateException() {
        when(mockGameRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(buildWithState(TournamentState.OPEN)));

        gameService.update(id, updateGameRequest);
        verify(mockGameRepository, times(0)).save(ArgumentMatchers.any());
    }

    @Test(expected = IllegalGameUpdateException.class)
    public void update_aProjectableTournament_throwsIllegalGameUpdateException() {
        when(mockGameRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(buildWithState(TournamentState.PROJECTABLE)));

        gameService.update(id, updateGameRequest);
        verify(mockGameRepository, times(0)).save(ArgumentMatchers.any());
    }

    @Test(expected = IllegalGameUpdateException.class)
    public void update_aClosedTournament_throwsIllegalGameUpdateException() {
        when(mockGameRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(buildWithState(TournamentState.CLOSED)));

        gameService.update(id, updateGameRequest);
        verify(mockGameRepository, times(0)).save(ArgumentMatchers.any());
    }

    private Game buildWithState(TournamentState state) {
        return Game.builder()
                .tournament(Tournament.builder()
                        .state(state)
                        .build())
                .build();
    }

}