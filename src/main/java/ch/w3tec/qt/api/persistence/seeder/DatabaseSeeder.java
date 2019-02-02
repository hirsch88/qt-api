package ch.w3tec.qt.api.persistence.seeder;

import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
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

    @Value("${app.seeding.enabled}")
    private boolean isSeedingEnabled;

    @Autowired
    public DatabaseSeeder(
            TournamentRepository tournamentRepository
) {
        this.tournamentRepository = tournamentRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        if (isSeedingEnabled) {
            LOG.info("STARTING seeding data");
            seedOpenTournament();
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

}
