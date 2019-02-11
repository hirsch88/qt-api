package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.domain.service.RankingService;
import ch.w3tec.qt.api.domain.service.ranking.Ranking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentRankingController {

    private final RankingService rankingService;

    @Autowired
    public TournamentRankingController(
            RankingService rankingService
    ) {
        this.rankingService = rankingService;
    }

    @GetMapping("/{visitorId}/ranking")
    public ResponseEntity<List<Ranking>> findByVisitorId(@PathVariable("visitorId") UUID visitorId) {
        List<Ranking> rankingList = rankingService.calculate(visitorId);
        return ResponseEntity.ok().body(rankingList);
    }

}
