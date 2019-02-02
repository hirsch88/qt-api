package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.domain.exception.IllegalTournamentUpdateException;
import ch.w3tec.qt.api.domain.exception.ResourceNotFoundException;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import ch.w3tec.qt.api.persistence.repository.TournamentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    public Tournament findById(UUID id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", id.toString()));
    }

}
