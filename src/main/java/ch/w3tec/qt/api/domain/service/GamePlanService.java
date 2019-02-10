package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.domain.service.planing.*;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class GamePlanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePlanService.class);

    private final GameService gameService;
    private final ParingTableService paringTableService;

    @Autowired
    public GamePlanService(
            GameService gameService,
            ParingTableService paringTableService
    ) {
        this.gameService = gameService;
        this.paringTableService = paringTableService;
    }

    public List<Game> generate(Tournament tournament) {
        LOGGER.info("STARTED generate(tournamentId={})", tournament.getId());
        Set<Team> teams = tournament.getTeams();
        List<Team> teamList = new ArrayList<>(teams);
        LOGGER.info("Creating games for {} teams", teamList.size());

        ParingTable paringTable = paringTableService.generate(ParingTableGenerationOptions.builder()
                .numberOfTeams(teams.size())
                .build());
        LOGGER.info("Created paringTable with {} rounds and {} fixture per round", paringTable.getRounds().size(), paringTable.getRounds().get(0).getFixtures().size());

        List<Game> games = new ArrayList<>();
        for (Round round : paringTable.getRounds()) {
            for (Fixture fixture : round.getFixtures()) {
                Game game = gameService.create(Game.builder()
                        .tournament(tournament)
                        .round(round.getIndex() + 1)
                        .host(teamList.get(fixture.getHost()))
                        .guest(teamList.get(fixture.getGuest()))
                        .build());
                LOGGER.info("Created game={}", game);
                games.add(game);
            }
        }

        LOGGER.info("FINISHED generate() => numberOfGames={}", games.size());
        return games;
    }

}
