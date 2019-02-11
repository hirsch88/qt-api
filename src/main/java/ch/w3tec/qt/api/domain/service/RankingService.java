package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.domain.service.ranking.Ranking;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TournamentService.class);

    private final TournamentService tournamentService;
    private final GameService gameService;

    @Autowired
    public RankingService(
            TournamentService tournamentService,
            GameService gameService
    ) {
        this.tournamentService = tournamentService;
        this.gameService = gameService;
    }

    public List<Ranking> calculate(UUID visitorId) {
        LOGGER.info("STARTING calculate(visitorId={})", visitorId);
        Tournament tournament = tournamentService.findByVisitorId(visitorId);

        List<Ranking> rankings = buildInitialRanking(tournament);
        calculateRankingOfTournament(tournament, rankings);
        calculateRankOfRanking(rankings);

        LOGGER.info("FINISHED calculate(visitorId={}) => ", visitorId, rankings);
        return rankings;
    }

    private void calculateRankOfRanking(List<Ranking> rankings) {
        rankings.sort((a, b) -> b.compareTo(a));
        int rank = 1;
        Ranking previousRanking = null;
        for (Ranking ranking : rankings) {
            if (previousRanking == null || previousRanking.getPoints() == ranking.getPoints()) {
                ranking.setRank(rank);
            } else {
                ranking.setRank(++rank);
            }
            previousRanking = ranking;
        }
    }

    private void calculateRankingOfTournament(Tournament tournament, List<Ranking> rankings) {
        List<Game> games = gameService.findByTournament(tournament);

        for (Ranking ranking : rankings) {
            for (Game game : games) {
                if (game.wasPlayed()) {
                    if (game.getHost().equals(ranking.getTeam())) {
                        evaluateHomeGame(game, ranking);
                    } else if (game.getGuest().equals(ranking.getTeam())) {
                        evaluateGuestGame(game, ranking);
                    }
                }
            }
        }

    }

    private void evaluateHomeGame(Game game, Ranking ranking) {
        ranking.increasePlayed();
        if (game.getHostScore() > game.getGuestScore()) {
            ranking.addWin();
        } else if (game.getHostScore().equals(game.getGuestScore())) {
            ranking.addDraw();
        } else {
            ranking.addLose();
        }
    }

    private void evaluateGuestGame(Game game, Ranking ranking) {
        ranking.increasePlayed();
        if (game.getHostScore() < game.getGuestScore()) {
            ranking.addWin();
        } else if (game.getHostScore().equals(game.getGuestScore())) {
            ranking.addDraw();
        } else {
            ranking.addLose();
        }
    }

    private List<Ranking> buildInitialRanking(Tournament tournament) {
        Set<Team> teams = tournament.getTeams();
        return teams.stream()
                .map(team -> Ranking.builder().rank(1).team(team).played(0).points(0).build())
                .collect(Collectors.toList());
    }

}
