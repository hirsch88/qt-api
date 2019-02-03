package ch.w3tec.qt.api.persistence.seeder;

import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.GameRepository;
import ch.w3tec.qt.api.persistence.repository.TeamRepository;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;

    @Value("${app.seeding.enabled}")
    private boolean isSeedingEnabled;

    @Autowired
    public DatabaseSeeder(
            TournamentRepository tournamentRepository,
            TeamRepository teamRepository,
            GameRepository gameRepository
    ) {
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        if (isSeedingEnabled) {
            LOG.info("STARTING seeding data");
            seedOpenTournament();
            seedPlanableTournamentWith3Teams();
            seedPlayableTournamentWith3Teams();
            LOG.info("FINISHED seeding data");
        }
    }

    private void seedOpenTournament() {
        LOG.info("STARTING seedOpenTournament");
        tournamentRepository.save(Tournament.builder()
                .name("Open Tournament")
                .state(TournamentState.OPEN)
                .build());
        LOG.info("FINISHED seedOpenTournament");
    }

    private void seedPlanableTournamentWith3Teams() {
        LOG.info("STARTING seedOpenTournamentWith3Teams");
        Tournament tournament = tournamentRepository.save(Tournament.builder()
                .name("Planable Tournament With 3 Teams")
                .state(TournamentState.PROJECTABLE)
                .build());
        teamRepository.save(Team.builder()
                .name("Valencia CF")
                .tournament(tournament)
                .build());
        teamRepository.save(Team.builder()
                .name("FC Liverpool")
                .tournament(tournament)
                .build());
        teamRepository.save(Team.builder()
                .name("Real Madrid")
                .tournament(tournament)
                .build());
        LOG.info("FINISHED seedOpenTournamentWith3Teams");
    }

    private void seedPlayableTournamentWith3Teams() {
        LOG.info("STARTING seedOpenTournamentWith3Teams");
        Tournament tournament = tournamentRepository.save(Tournament.builder()
                .name("Playable Tournament With 3 Teams")
                .state(TournamentState.PLAYABLE)
                .build());
        Team team1 = teamRepository.save(Team.builder().name("Betis Sevilla").tournament(tournament).build());
        Team team2 = teamRepository.save(Team.builder().name("Arsenal").tournament(tournament).build());
        Team team3 = teamRepository.save(Team.builder().name("Borussia Dortmund").tournament(tournament).build());
        gameRepository.save(Game.builder().host(team1).guest(team2).tournament(tournament).build());
        gameRepository.save(Game.builder().host(team3).guest(team1).tournament(tournament).build());
        gameRepository.save(Game.builder().host(team2).guest(team3).tournament(tournament).build());
        LOG.info("FINISHED seedOpenTournamentWith3Teams");
    }

}
