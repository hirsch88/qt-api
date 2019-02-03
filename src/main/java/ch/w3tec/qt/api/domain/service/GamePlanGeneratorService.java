package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class GamePlanGeneratorService {

    private final GameService gameService;

    @Autowired
    public GamePlanGeneratorService(
            GameService gameService
    ) {
        this.gameService = gameService;
    }

    public void generate(Tournament tournament) {
        Set<Team> teams = tournament.getTeams();

    }

}
