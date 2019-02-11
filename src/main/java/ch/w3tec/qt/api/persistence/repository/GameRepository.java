package ch.w3tec.qt.api.persistence.repository;

import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {

    List<Game> findByTournament(Tournament tournament);
    Page<Game> findByTournament(Tournament tournament, Pageable pageable);

    void deleteByTournament(Tournament tournament);

}
