package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.UpdateTeamRequest;
import ch.w3tec.qt.api.domain.exception.*;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.GameRepository;
import ch.w3tec.qt.api.persistence.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TeamService {

//    private static final Logger LOG = LoggerFactory.getLogger(TournamentService.class);

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(
            TeamRepository teamRepository
    ) {
        this.teamRepository = teamRepository;
    }

    public Team findById(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id.toString()));
    }

    protected Page<Team> findByTournament(Tournament tournament, Pageable pageRequest) {
        return teamRepository.findByTournament(tournament, pageRequest);
    }

    protected Team addTeamToTournament(Tournament tournament, CreateTeamRequest createTeamRequest) {
        if (teamRepository.countByName(createTeamRequest.getName()) > 0) {
            throw new IllegalTeamNameException();
        }

        Team team = Team.builder()
                .name(createTeamRequest.getName())
                .tournament(tournament)
                .build();
        return teamRepository.save(team);
    }

    public void removeTeamFromTournament(UUID id) {
        Team team = findById(id);
        teamRepository.delete(team);
    }

    public Team update(UUID id, UpdateTeamRequest updateTeamRequest) {
        Team team = findById(id);
        if (!team.getTournament().getState().equals(TournamentState.OPEN)) {
            throw new IllegalTeamUpdateException();
        }

        return teamRepository.save(team.toBuilder()
                .name(updateTeamRequest.getName())
                .build());
    }

}
