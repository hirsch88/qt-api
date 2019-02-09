package ch.w3tec.qt.api.application.controller;

import ch.w3tec.qt.api.application.request.CreateTournamentRequest;
import ch.w3tec.qt.api.application.request.UpdateTournamentRequest;
import ch.w3tec.qt.api.domain.service.TournamentService;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(
            TournamentService tournamentService
    ) {
        this.tournamentService = tournamentService;
    }

    @PostMapping()
    public ResponseEntity<Tournament> create(@RequestBody @Valid CreateTournamentRequest createTournamentRequest) {
        Tournament tournament = tournamentService.create(createTournamentRequest);
        return ResponseEntity.ok().body(tournament);
    }

    @GetMapping("/{visitorOrAdminId}")
    public ResponseEntity<Tournament> findByVisitorId(@PathVariable("visitorOrAdminId") UUID visitorOrAdminId) {
        Tournament tournament = tournamentService.findByVisitorIdOrAdminId(visitorOrAdminId);
        return ResponseEntity.ok().body(tournament);
    }

    @PutMapping("/{adminId}/admin")
    public ResponseEntity<Tournament> update(@PathVariable("adminId") UUID adminId,
                                             @Valid @RequestBody UpdateTournamentRequest updateTournamentRequest) {
        Tournament tournament = tournamentService.updateByAdminId(adminId, updateTournamentRequest);
        return ResponseEntity.ok().body(tournament);
    }

//    @GetMapping()
//    public ResponseEntity<PageResponse<Tournament>> findAll(@RequestParam(value = "search", required = false) String search, Pageable pageRequest) {
//        Specification<Tournament> spec = Specification.where(null);
//
//        if (Strings.isNotEmpty(search)) {
//            try {
//                Node rootNode = new RSQLParser().parse(search);
//                spec = rootNode.accept(new CustomRsqlVisitor<>());
//            } catch (RSQLParserException e) {
//                throw new IllegalSearchFilterException();
//            }
//        }
//
//        Page<Tournament> page = tournamentService.findAll(spec, pageRequest);
//        PageResponse<Tournament> pageResponse = PageResponse.build(page);
//        return ResponseEntity.ok().body(pageResponse);
//    }

}
