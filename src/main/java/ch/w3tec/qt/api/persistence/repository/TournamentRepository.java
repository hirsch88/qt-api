package ch.w3tec.qt.api.persistence.repository;

import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
}
