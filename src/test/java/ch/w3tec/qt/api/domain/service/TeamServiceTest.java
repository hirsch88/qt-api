package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.UpdateTeamRequest;
import ch.w3tec.qt.api.domain.exception.IllegalTeamNameException;
import ch.w3tec.qt.api.domain.exception.IllegalTeamUpdateException;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.TeamRepository;
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
public class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamRepository mockTeamRepository;

    private UUID id;
    private CreateTeamRequest createTeamRequest;
    private Tournament tournament;

    @Before
    public void setUp() {
        id = UUID.randomUUID();
        tournament = Tournament.builder()
                .id(id)
                .state(TournamentState.OPEN)
                .build();

        createTeamRequest = CreateTeamRequest.builder()
                .name("A-Team")
                .build();

        when(mockTeamRepository.save(ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void addTeamToTournament_validRequest_savesTeam() {
        when(mockTeamRepository.countByName(ArgumentMatchers.any()))
                .thenReturn(0);

        Team team = teamService.addTeamToTournament(tournament, createTeamRequest);

        assertThat(team.getTournament().getId()).isEqualByComparingTo(id);
        assertThat(team.getName()).isEqualTo(createTeamRequest.getName());
        verify(mockTeamRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test(expected = IllegalTeamNameException.class)
    public void addTeamToTournament_duplicatedTeamName_throwsException() {
        when(mockTeamRepository.countByName(ArgumentMatchers.any()))
                .thenReturn(1);

        teamService.addTeamToTournament(tournament, createTeamRequest);

        verify(mockTeamRepository, times(0)).save(ArgumentMatchers.any());
    }

    @Test
    public void update_validRequest_savesTeam() {
        when(mockTeamRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(Team.builder()
                        .id(id).name("A-Team")
                        .tournament(Tournament.builder().state(TournamentState.OPEN).build())
                        .build()));

        UpdateTeamRequest updateTeamRequest = UpdateTeamRequest.builder()
                .name("B-Team")
                .build();

        Team team = teamService.update(id, updateTeamRequest);

        assertThat(team.getId()).isEqualTo(id);
        assertThat(team.getName()).isEqualTo(updateTeamRequest.getName());
        verify(mockTeamRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test(expected = IllegalTeamUpdateException.class)
    public void update_projectableTournament_throwsException() {
        mockFindByIdWithTournamentState(TournamentState.PROJECTABLE);

        teamService.update(id, UpdateTeamRequest.builder().build());

        verify(mockTeamRepository, times(0)).save(ArgumentMatchers.any());
    }

    @Test(expected = IllegalTeamUpdateException.class)
    public void update_playableTournament_throwsException() {
        mockFindByIdWithTournamentState(TournamentState.PLAYABLE);

        teamService.update(id, UpdateTeamRequest.builder().build());

        verify(mockTeamRepository, times(0)).save(ArgumentMatchers.any());
    }

    @Test(expected = IllegalTeamUpdateException.class)
    public void update_closedTournament_throwsException() {
        mockFindByIdWithTournamentState(TournamentState.CLOSED);

        teamService.update(id, UpdateTeamRequest.builder().build());

        verify(mockTeamRepository, times(0)).save(ArgumentMatchers.any());
    }

    private void mockFindByIdWithTournamentState(TournamentState state) {
        when(mockTeamRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(Team.builder()
                        .id(id).name("A-Team")
                        .tournament(Tournament.builder().state(state).build())
                        .build()));
    }
}