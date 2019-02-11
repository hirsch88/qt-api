package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.*;
import ch.w3tec.qt.api.domain.exception.*;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
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
public class TournamentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TournamentService.class);

    private final TournamentRepository tournamentRepository;
    private final GamePlanService gamePlanService;
    private final GameService gameService;
    private final TeamService teamService;

    @Autowired
    public TournamentService(
            TournamentRepository tournamentRepository,
            GamePlanService gamePlanService,
            GameService gameService,
            TeamService teamService
    ) {
        this.tournamentRepository = tournamentRepository;
        this.gamePlanService = gamePlanService;
        this.gameService = gameService;
        this.teamService = teamService;
    }

    public Tournament findByVisitorIdOrAdminId(UUID visitorOrAdminId) {
        return tournamentRepository.findByVisitorIdOrAdminId(visitorOrAdminId, visitorOrAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "visitorOrAdminId", visitorOrAdminId.toString()));
    }

    public Tournament create(CreateTournamentRequest createTournamentRequest) {
        LOGGER.info("STARTING create(createTournamentRequest={})", createTournamentRequest);
        Tournament tournament = Tournament.builder()
                .name(createTournamentRequest.getName())
                .adminId(UUID.randomUUID())
                .visitorId(UUID.randomUUID())
                .state(TournamentState.OPEN)
                .build();

        Tournament savedTournament = tournamentRepository.save(tournament);
        LOGGER.info("FINISHED create(createTournamentRequest={}) => {}", createTournamentRequest, savedTournament);
        return savedTournament;
    }

    public Page<Game> findGamesByVisitorIdOrAdminId(UUID visitorOrAdminId, Pageable pageRequest) {
        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        return gameService.findByTournament(tournament, pageRequest);
    }

    public Page<Team> findTeamsByVisitorIdOrAdminId(UUID visitorOrAdminId, Pageable pageRequest) {
        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        return teamService.findByTournament(tournament, pageRequest);
    }

    public Team addTeamToTournament(UUID visitorOrAdminId, CreateTeamRequest createTeamRequest) {
        LOGGER.info("STARTING addTeamToTournament(visitorOrAdminId={}, createTeamRequest={})", visitorOrAdminId, createTeamRequest);

        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            LOGGER.warn("FAILED addTeamToTournament(visitorOrAdminId={}, createTeamRequest={}) => IllegalTeamCreationException", visitorOrAdminId, createTeamRequest);
            throw new IllegalTeamCreationException();
        }

        Team savedTeam = teamService.addTeamToTournament(tournament, createTeamRequest);
        LOGGER.info("FINISHED addTeamToTournament(visitorOrAdminId={}, createTeamRequest={}) => {}", visitorOrAdminId, createTeamRequest, savedTeam);
        return savedTeam;
    }

    public Team updateTeamOfTournament(UUID visitorOrAdminId, UUID teamId, UpdateTeamRequest updateTeamRequest) {
        LOGGER.info("STARTING updateTeamOfTournament(visitorOrAdminId={}, teamId={}, updateTeamRequest={})", visitorOrAdminId, teamId, updateTeamRequest);
        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            LOGGER.warn("FAILED updateTeamOfTournament(visitorOrAdminId={}, teamId={}, updateTeamRequest={}) => IllegalTeamCreationException", visitorOrAdminId, teamId, updateTeamRequest);
            throw new IllegalTeamCreationException();
        }

        Team savedTeam = teamService.update(teamId, updateTeamRequest);
        LOGGER.info("FINISHED updateTeamOfTournament(visitorOrAdminId={}, teamId={}, updateTeamRequest={}) => {}", visitorOrAdminId, teamId, updateTeamRequest, savedTeam);
        return savedTeam;
    }

    public void removeTeamFromTournament(UUID visitorOrAdminId, UUID teamId) {
        LOGGER.info("STARTING removeTeamFromTournament(visitorOrAdminId={}, teamId={})", visitorOrAdminId, teamId);
        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            LOGGER.warn("FAILED removeTeamFromTournament(visitorOrAdminId={}, teamId={}) => IllegalTeamDeletionException", visitorOrAdminId, teamId);
            throw new IllegalTeamDeletionException();
        }

        teamService.removeTeamFromTournament(teamId);
        LOGGER.info("FINISHED removeTeamFromTournament(visitorOrAdminId={}, teamId={})", visitorOrAdminId, teamId);
    }

    public Game updateGamesByAdminId(UUID adminId, UUID gameId, UpdateGameRequest updateGameRequest) {
        LOGGER.info("STARTING updateGamesByAdminId(adminId={}, gameId={}, updateGameRequest={})", adminId, gameId, updateGameRequest);

        Tournament tournament = findByAdminId(adminId);
        if (!tournament.getState().equals(TournamentState.PLAYABLE)) {
            LOGGER.warn("FAILED updateGamesByAdminId(adminId={}, gameId={}, updateGameRequest={}) => IllegalGameUpdateException", adminId, gameId, updateGameRequest);
            throw new IllegalGameUpdateException();
        }

        Game savedGame = gameService.update(gameId, updateGameRequest);
        LOGGER.info("FINISHED updateGamesByAdminId(adminId={}, gameId={}, updateGameRequest={}) => {}", adminId, gameId, updateGameRequest, savedGame);
        return savedGame;
    }

    public Tournament updateByAdminId(UUID adminId, UpdateTournamentRequest updateTournamentRequest) {
        LOGGER.info("STARTING updateByAdminId(adminId={}, updateTournamentRequest={})", adminId, updateTournamentRequest);
        Tournament tournament = findByAdminId(adminId);
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();
        LOGGER.info("Update tournament from state {} to the state {}", fromState, toState);

        switch (fromState) {
            case OPEN:
                Tournament savedOpenTournament = updateOpenTournament(tournament, updateTournamentRequest);
                LOGGER.info("FINISHED updateByAdminId(adminId={}, updateTournamentRequest={})", adminId, updateTournamentRequest, savedOpenTournament);
                return savedOpenTournament;
            case PROJECTABLE:
                Tournament savedProjectableTournament = updateProjectableTournament(tournament, updateTournamentRequest);
                LOGGER.info("FINISHED updateByAdminId(adminId={}, updateTournamentRequest={})", adminId, updateTournamentRequest, savedProjectableTournament);
                return savedProjectableTournament;
            case PLAYABLE:
                Tournament savedPlayableTournament = updatePlayableTournament(tournament, updateTournamentRequest);
                LOGGER.info("FINISHED updateByAdminId(adminId={}, updateTournamentRequest={})", adminId, updateTournamentRequest, savedPlayableTournament);
                return savedPlayableTournament;
            default:
                throw new IllegalTournamentUpdateException(fromState, toState);
        }
    }

    private Tournament updateOpenTournament(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        LOGGER.info("STARTING updateOpenTournament(tournament={}, updateTournamentRequest={})", tournament, updateTournamentRequest);
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();

        if (!(toState.equals(TournamentState.PROJECTABLE) || toState.equals(fromState))) {
            LOGGER.info("FAILED updateOpenTournament(tournament={}, updateTournamentRequest={}) => IllegalTournamentUpdateException", tournament, updateTournamentRequest);
            throw new IllegalTournamentUpdateException(fromState, toState);
        }

        if (toState.equals(TournamentState.PROJECTABLE)) {
            Tournament savedProjectableTournament = updateProjectableTournamentAndGenerateGames(tournament, updateTournamentRequest);
            LOGGER.info("FINISHED updateOpenTournament(tournament={}, updateTournamentRequest={}) => {}", tournament, updateTournamentRequest, savedProjectableTournament);
            return savedProjectableTournament;
        }

        Tournament savedOpenTournament = updateBeforePlaying(tournament, updateTournamentRequest);
        LOGGER.info("FINISHED updateOpenTournament(tournament={}, updateTournamentRequest={}) => {}", tournament, updateTournamentRequest, savedOpenTournament);
        return savedOpenTournament;
    }

    private Tournament updateProjectableTournament(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        LOGGER.info("STARTING updateProjectableTournament(tournament={}, updateTournamentRequest={})", tournament, updateTournamentRequest);
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();

        if (!(toState.equals(TournamentState.OPEN) || toState.equals(TournamentState.PLAYABLE) || toState.equals(fromState))) {
            LOGGER.warn("FAILED updateProjectableTournament(tournament={}, updateTournamentRequest={}) => IllegalTournamentUpdateException", tournament, updateTournamentRequest);
            throw new IllegalTournamentUpdateException(fromState, toState);
        }

        if (toState.equals(TournamentState.OPEN)) {
            Tournament savedOpenTournament = updateBeforePlaying(tournament, updateTournamentRequest);
            LOGGER.warn("FINISHED updateProjectableTournament(tournament={}, updateTournamentRequest={}) => {}", tournament, updateTournamentRequest, savedOpenTournament);
            return savedOpenTournament;
        }

        Tournament savedProjectableTournament = updateProjectableTournamentAndGenerateGames(tournament, updateTournamentRequest);
        LOGGER.warn("FINISHED updateProjectableTournament(tournament={}, updateTournamentRequest={}) => {}", tournament, updateTournamentRequest, savedProjectableTournament);
        return savedProjectableTournament;
    }

    private Tournament updatePlayableTournament(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        LOGGER.info("STARTING updatePlayableTournament(tournament={}, updateTournamentRequest={})", tournament, updateTournamentRequest);
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();

        if (!(toState.equals(TournamentState.CLOSED) || toState.equals(fromState))) {
            LOGGER.info("FAILED updatePlayableTournament(tournament={}, updateTournamentRequest={}) => IllegalTournamentUpdateException", tournament, updateTournamentRequest);
            throw new IllegalTournamentUpdateException(fromState, toState);
        }

        Tournament savedTournament = updateAfterPlaning(tournament, updateTournamentRequest);
        LOGGER.info("FINISHED updatePlayableTournament(tournament={}, updateTournamentRequest={}) => {}", tournament, updateTournamentRequest, savedTournament);
        return savedTournament;
    }

    private Tournament updateProjectableTournamentAndGenerateGames(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        Tournament newTournament = updateBeforePlaying(tournament, updateTournamentRequest);
        gameService.deleteByTournament(tournament);
        gamePlanService.generate(newTournament);
        return newTournament;
    }

    private Tournament updateBeforePlaying(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        return tournamentRepository.save(tournament.toBuilder()
                .name(updateTournamentRequest.getName())
                .state(updateTournamentRequest.getState()).build());
    }

    private Tournament updateAfterPlaning(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        return tournamentRepository.save(tournament.toBuilder()
                .name(updateTournamentRequest.getName())
                .state(updateTournamentRequest.getState()).build());
    }

    public Tournament findByAdminId(UUID adminId) {
        return tournamentRepository.findByAdminId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "adminId", adminId.toString()));
    }

    public Tournament findByVisitorId(UUID visitorId) {
        return tournamentRepository.findByVisitorId(visitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "visitorId", visitorId.toString()));
    }

//    public Page<Tournament> findAll(Specification<Tournament> spec, Pageable pageRequest) {
//        return tournamentRepository.findAll(spec, pageRequest);
//    }

}
