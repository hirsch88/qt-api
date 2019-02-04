package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.CreateTeamRequest;
import ch.w3tec.qt.api.application.request.CreateTournamentRequest;
import ch.w3tec.qt.api.application.request.UpdateTournamentRequest;
import ch.w3tec.qt.api.domain.exception.IllegalTeamCreationException;
import ch.w3tec.qt.api.domain.exception.IllegalTeamDeletionException;
import ch.w3tec.qt.api.domain.exception.IllegalTournamentUpdateException;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.domain.service.planing.GamePlanService;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<Tournament> findAll(Specification<Tournament> spec, Pageable pageRequest) {
        return tournamentRepository.findAll(spec, pageRequest);
    }

    public Tournament findById(UUID id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", id.toString()));
    }

    public Page<Game> findGamesByTournamentId(UUID id, Pageable pageRequest) {
        Tournament tournament = findById(id);
        return gameService.findByTournament(tournament, pageRequest);
    }

    public Page<Team> findTeamsByTournamentId(UUID id, Pageable pageRequest) {
        Tournament tournament = findById(id);
        return teamService.findByTournament(tournament, pageRequest);
    }

    public Tournament create(CreateTournamentRequest createTournamentRequest) {
        Tournament tournament = Tournament.builder()
                .name(createTournamentRequest.getName())
                .state(TournamentState.OPEN)
                .build();
        return tournamentRepository.save(tournament);
    }

    public Team addTeamToTournament(UUID id, CreateTeamRequest createTeamRequest) {
        Tournament tournament = findById(id);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            throw new IllegalTeamCreationException();
        }

        return teamService.addTeamToTournament(tournament, createTeamRequest);
    }

    public void removeTeamFromTournament(UUID id, UUID teamId) {
        Tournament tournament = findById(id);
        if (!tournament.getState().equals(TournamentState.OPEN)) {
            throw new IllegalTeamDeletionException();
        }

        teamService.removeTeamFromTournament(teamId);
    }

    public void deleteById(UUID id) {
        Tournament tournament = findById(id);
        tournamentRepository.delete(tournament);
    }

    public Tournament update(UUID id, UpdateTournamentRequest updateTournamentRequest) {
        Tournament tournament = findById(id);
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

    private Tournament updateBeforePlaying(Tournament tournament, UpdateTournamentRequest updateTournamentRequest){
        return tournamentRepository.save(tournament.toBuilder()
                .name(updateTournamentRequest.getName())
                .state(updateTournamentRequest.getState()).build());
    }

    private Tournament updateAfterPlaning(Tournament tournament, UpdateTournamentRequest updateTournamentRequest){
        return tournamentRepository.save(tournament.toBuilder()
                .name(updateTournamentRequest.getName())
                .state(updateTournamentRequest.getState()).build());
    }

}
