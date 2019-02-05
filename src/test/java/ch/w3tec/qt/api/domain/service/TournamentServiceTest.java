package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.CreateTournamentRequest;
import ch.w3tec.qt.api.application.request.UpdateTournamentRequest;
import ch.w3tec.qt.api.domain.exception.IllegalTeamCreationException;
import ch.w3tec.qt.api.domain.exception.IllegalTeamDeletionException;
import ch.w3tec.qt.api.domain.exception.IllegalTournamentUpdateException;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
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
public class TournamentServiceTest {

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private TournamentRepository mockTournamentRepository;

    @Mock
    private GamePlanService mockGamePlanService;

    @Mock
    private GameService mockGameService;

    @Mock
    private TeamService mockTeamService;

    private UUID id;

    @Before
    public void setUp() {
        id = UUID.randomUUID();

        when(mockTournamentRepository.save(ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void create_validRequest_savesAOpenTournament() {
        CreateTournamentRequest createTournamentRequest = CreateTournamentRequest.builder().name("Tournament 1").build();

        Tournament tournament = tournamentService.create(createTournamentRequest);

        assertThat(tournament.getName()).isEqualTo(createTournamentRequest.getName());
        assertThat(tournament.getState()).isEqualByComparingTo(TournamentState.OPEN);
        verify(mockTournamentRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test()
    public void addTeamToTournament_openTournament_addTeamToTournament() {
        Tournament tournament = Tournament.builder().state(TournamentState.OPEN).build();
        when(mockTournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        CreateTeamRequest createTeamRequest = CreateTeamRequest.builder().build();
        tournamentService.addTeamToTournament(id, createTeamRequest);

        verify(mockTeamService, times(1)).addTeamToTournament(tournament, createTeamRequest);
    }

    @Test(expected = IllegalTeamCreationException.class)
    public void addTeamToTournament_projectableTournament_throwsException() {
        testAddTeamToTournamentWithTournamentState(TournamentState.PROJECTABLE);
    }

    @Test(expected = IllegalTeamCreationException.class)
    public void addTeamToTournament_playableTournament_throwsException() {
        testAddTeamToTournamentWithTournamentState(TournamentState.PLAYABLE);
    }

    @Test(expected = IllegalTeamCreationException.class)
    public void addTeamToTournament_closedTournament_throwsException() {
        testAddTeamToTournamentWithTournamentState(TournamentState.CLOSED);
    }

    @Test()
    public void removeTeamFromTournament_openTournament_removeTeamToTournament() {
        UUID teamId = UUID.randomUUID();
        Tournament tournament = Tournament.builder().state(TournamentState.OPEN).build();
        when(mockTournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        tournamentService.removeTeamFromTournament(id, teamId);

        verify(mockTeamService, times(1)).removeTeamFromTournament(teamId);
    }

    @Test(expected = IllegalTeamDeletionException.class)
    public void removeTeamFromTournament_projectableTournament_throwsException() {
        testRemoveTeamToTournamentWithTournamentState(TournamentState.PROJECTABLE);
    }

    @Test(expected = IllegalTeamDeletionException.class)
    public void removeTeamFromTournament_playableTournament_throwsException() {
        testRemoveTeamToTournamentWithTournamentState(TournamentState.PLAYABLE);
    }

    @Test(expected = IllegalTeamDeletionException.class)
    public void removeTeamFromTournament_closedTournament_throwsException() {
        testRemoveTeamToTournamentWithTournamentState(TournamentState.CLOSED);
    }

    @Test
    public void update_tournamentChangeName_returnsTournamentWithChangedName() {
        testUpdateTournamentWithValidStateChange(TournamentState.OPEN, TournamentState.OPEN);
        testUpdateTournamentWithValidStateChange(TournamentState.OPEN, TournamentState.PROJECTABLE);
        testUpdateTournamentWithValidStateChange(TournamentState.PROJECTABLE, TournamentState.OPEN);
        testUpdateTournamentWithValidStateChange(TournamentState.PROJECTABLE, TournamentState.PROJECTABLE);
        testUpdateTournamentWithValidStateChange(TournamentState.PROJECTABLE, TournamentState.PLAYABLE);
        testUpdateTournamentWithValidStateChange(TournamentState.PLAYABLE, TournamentState.PLAYABLE);
        testUpdateTournamentWithValidStateChange(TournamentState.PLAYABLE, TournamentState.CLOSED);

        verify(mockTournamentRepository, times(7)).save(ArgumentMatchers.any());
    }

    @Test()
    public void update_tournamentInvalidState_throwsException() {
        testUpdateTournamentWithInvalidStateChange(TournamentState.OPEN, TournamentState.PLAYABLE);
        testUpdateTournamentWithInvalidStateChange(TournamentState.OPEN, TournamentState.CLOSED);
        testUpdateTournamentWithInvalidStateChange(TournamentState.PROJECTABLE, TournamentState.CLOSED);
        testUpdateTournamentWithInvalidStateChange(TournamentState.PLAYABLE, TournamentState.OPEN);
        testUpdateTournamentWithInvalidStateChange(TournamentState.PLAYABLE, TournamentState.PROJECTABLE);
        testUpdateTournamentWithInvalidStateChange(TournamentState.CLOSED, TournamentState.OPEN);
        testUpdateTournamentWithInvalidStateChange(TournamentState.CLOSED, TournamentState.PROJECTABLE);
        testUpdateTournamentWithInvalidStateChange(TournamentState.CLOSED, TournamentState.PLAYABLE);
        testUpdateTournamentWithInvalidStateChange(TournamentState.CLOSED, TournamentState.CLOSED);

        verify(mockTournamentRepository, times(0)).save(ArgumentMatchers.any());
    }

    private void testUpdateTournamentWithValidStateChange(TournamentState fromState, TournamentState toState) {
        Tournament existingTournament = Tournament.builder().state(fromState).build();
        when(mockTournamentRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(existingTournament));

        UpdateTournamentRequest updateTournamentRequest = buildUpdateTournamentRequest(toState);

        Tournament tournament = tournamentService.update(id, updateTournamentRequest);

        assertThat(tournament.getName()).isEqualTo(updateTournamentRequest.getName());
        assertThat(tournament.getState()).isEqualByComparingTo(toState);
        assertThat(tournament.getState()).isEqualTo(updateTournamentRequest.getState());
    }

    private void testUpdateTournamentWithInvalidStateChange(TournamentState fromState, TournamentState toState) {
        Tournament existingTournament = Tournament.builder().state(fromState).build();
        when(mockTournamentRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(existingTournament));

        UpdateTournamentRequest updateTournamentRequest = buildUpdateTournamentRequest(toState);

        try {
            tournamentService.update(id, updateTournamentRequest);
        } catch (Exception e) {
            assertThat(e instanceof IllegalTournamentUpdateException).isTrue();
        }
    }

    private void testAddTeamToTournamentWithTournamentState(TournamentState state) {
        Tournament tournament = Tournament.builder().state(state).build();
        when(mockTournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        CreateTeamRequest createTeamRequest = CreateTeamRequest.builder().build();
        tournamentService.addTeamToTournament(id, createTeamRequest);

        verify(mockTeamService, times(0)).addTeamToTournament(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    private void testRemoveTeamToTournamentWithTournamentState(TournamentState state) {
        UUID teamId = UUID.randomUUID();
        Tournament tournament = Tournament.builder().state(state).build();
        when(mockTournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        tournamentService.removeTeamFromTournament(id, teamId);

        verify(mockTeamService, times(0)).removeTeamFromTournament(ArgumentMatchers.any());
    }

    private UpdateTournamentRequest buildUpdateTournamentRequest(TournamentState state) {
        return UpdateTournamentRequest.builder().name("New Name").state(state).build();
    }

}