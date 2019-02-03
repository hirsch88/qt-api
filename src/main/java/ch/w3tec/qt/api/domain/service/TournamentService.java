package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.application.request.CreateTournamentRequest;
import ch.w3tec.qt.api.application.request.UpdateTournamentRequest;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.entity.TournamentState;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TournamentService {

//    private static final Logger LOG = LoggerFactory.getLogger(TournamentService.class);

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(
            TournamentRepository tournamentRepository
    ) {
        this.tournamentRepository = tournamentRepository;
    }

    public Page<Tournament> findAll(Pageable pageRequest) {
        return tournamentRepository.findAll(pageRequest);
    }

    public Tournament findById(UUID id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", id.toString()));
    }

    public Tournament create(CreateTournamentRequest createTournamentRequest) {
        Tournament tournament = Tournament.builder()
                .name(createTournamentRequest.getName())
                .state(TournamentState.OPEN)
                .build();
        return tournamentRepository.save(tournament);
    }

    public void deleteById(UUID id) {
        Tournament tournament = findById(id);
        tournamentRepository.delete(tournament);
    }

    public Tournament update(UUID id, UpdateTournamentRequest updateTournamentRequest) {
        Tournament tournament = findById(id);

        // TODO: Add update logic

        return tournament;
    }
}
