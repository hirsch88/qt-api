package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.*;
import ch.w3tec.qt.api.domain.exception.IllegalTeamCreationException;
import ch.w3tec.qt.api.domain.exception.IllegalTeamDeletionException;
import ch.w3tec.qt.api.domain.exception.IllegalTournamentUpdateException;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class TournamentService {

//    private static final Logger LOG = LoggerFactory.getLogger(TournamentService.class);

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
        Tournament tournament = Tournament.builder()
                .name(createTournamentRequest.getName())
                .adminId(UUID.randomUUID())
                .visitorId(UUID.randomUUID())
                .state(TournamentState.OPEN)
                .build();
        return tournamentRepository.save(tournament);
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
        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            throw new IllegalTeamCreationException();
        }

        return teamService.addTeamToTournament(tournament, createTeamRequest);
    }

    public Team updateTeamOfTournament(UUID visitorOrAdminId, UUID teamId, UpdateTeamRequest updateTeamRequest) {
        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            throw new IllegalTeamCreationException();
        }

        return teamService.update(teamId, updateTeamRequest);
    }

    public void removeTeamFromTournament(UUID visitorOrAdminId, UUID teamId) {
        Tournament tournament = findByVisitorIdOrAdminId(visitorOrAdminId);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            throw new IllegalTeamDeletionException();
        }

        teamService.removeTeamFromTournament(teamId);
    }

    public Game updateGamesByAdminId(UUID adminId, UUID gameId, UpdateGameRequest updateGameRequest) {
        Tournament tournament = findByAdminId(adminId);
        if (!tournament.getState().equals(TournamentState.PLAYABLE)) {
            throw new IllegalTeamDeletionException();
        }

        return gameService.update(gameId, updateGameRequest);
    }

    public Tournament updateByAdminId(UUID adminId, UpdateTournamentRequest updateTournamentRequest) {
        Tournament tournament = findByAdminId(adminId);
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();

        switch (fromState) {
            case OPEN:
                return updateOpenTournament(tournament, updateTournamentRequest);
            case PROJECTABLE:
                return updateProjectableTournament(tournament, updateTournamentRequest);
            case PLAYABLE:
                return updatePlayableTournament(tournament, updateTournamentRequest);
            default:
                throw new IllegalTournamentUpdateException(fromState, toState);
        }

    }

    private Tournament updateOpenTournament(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();

        if (!(toState.equals(TournamentState.PROJECTABLE) || toState.equals(fromState))) {
            throw new IllegalTournamentUpdateException(fromState, toState);
        }

        if (toState.equals(TournamentState.PROJECTABLE)) {
            return updateProjectableTournamentAndGenerateGames(tournament, updateTournamentRequest);
        }

        return updateBeforePlaying(tournament, updateTournamentRequest);
    }

    private Tournament updateProjectableTournament(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();

        if (!(toState.equals(TournamentState.OPEN) || toState.equals(TournamentState.PLAYABLE) || toState.equals(fromState))) {
            throw new IllegalTournamentUpdateException(fromState, toState);
        }

        if (toState.equals(TournamentState.OPEN)) {
            return updateBeforePlaying(tournament, updateTournamentRequest);
        }

        return updateProjectableTournamentAndGenerateGames(tournament, updateTournamentRequest);
    }

    private Tournament updatePlayableTournament(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        final TournamentState fromState = tournament.getState();
        final TournamentState toState = updateTournamentRequest.getState();

        if (!(toState.equals(TournamentState.CLOSED) || toState.equals(fromState))) {
            throw new IllegalTournamentUpdateException(fromState, toState);
        }

        return updateAfterPlaning(tournament, updateTournamentRequest);
    }

    private Tournament updateProjectableTournamentAndGenerateGames(Tournament tournament, UpdateTournamentRequest updateTournamentRequest) {
        Tournament newTournament = updateBeforePlaying(tournament, updateTournamentRequest);
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

    private Tournament findByAdminId(UUID adminId) {
        return tournamentRepository.findByAdminId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "adminId", adminId.toString()));
    }

//    private Tournament findById(UUID id) {
//        return tournamentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", id.toString()));
//    }
//
//    private Tournament findByVisitorId(UUID visitorId) {
//        return tournamentRepository.findByVisitorId(visitorId)
//                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "visitorId", visitorId.toString()));
//    }
//
//    public Page<Tournament> findAll(Specification<Tournament> spec, Pageable pageRequest) {
//        return tournamentRepository.findAll(spec, pageRequest);
//    }

}
