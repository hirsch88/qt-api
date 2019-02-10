package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.UpdateTeamRequest;
import ch.w3tec.qt.api.domain.exception.IllegalTeamNameException;
import ch.w3tec.qt.api.domain.exception.IllegalTeamUpdateException;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class TeamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

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
        LOGGER.info("STARTED addTeamToTournament(tournamentId={}, createTeamRequest={})", tournament.getId(), createTeamRequest);

        if (teamRepository.countByName(createTeamRequest.getName()) > 0) {
            LOGGER.warn("FAILED addTeamToTournament(tournamentId={}, createTeamRequest={}) => IllegalTeamNameException", tournament.getId(), createTeamRequest);
            throw new IllegalTeamNameException();
        }

        Team team = Team.builder()
                .name(createTeamRequest.getName())
                .tournament(tournament)
                .build();

        Team savedTeam = teamRepository.save(team);
        LOGGER.info("FINISHED addTeamToTournament(tournamentId={}, createTeamRequest={}) => {}", tournament.getId(), createTeamRequest, savedTeam);
        return savedTeam;
    }

    public void removeTeamFromTournament(UUID id) {
        LOGGER.info("STARTED removeTeamFromTournament(teamId={})", id);
        Team team = findById(id);
        teamRepository.delete(team);
        LOGGER.info("FINISHED removeTeamFromTournament(teamId={})", id);
    }

    public Team update(UUID id, UpdateTeamRequest updateTeamRequest) {
        LOGGER.info("STARTED removeTeamFromTournament(teamId={})", id);

        Team team = findById(id);
        if (!team.getTournament().getState().equals(TournamentState.OPEN)) {
            LOGGER.warn("FAILED removeTeamFromTournament(teamId={}) => IllegalTeamUpdateException", id);
            throw new IllegalTeamUpdateException();
        }

        Team savedTeam = teamRepository.save(team.toBuilder()
                .name(updateTeamRequest.getName())
                .build());
        LOGGER.info("FINISHED removeTeamFromTournament(teamId={}) => ", id, savedTeam);
        return savedTeam;
    }

}
