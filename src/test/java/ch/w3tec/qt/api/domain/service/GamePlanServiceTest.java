package ch.w3tec.qt.api.domain.service;

import ch.w3tec.qt.api.domain.service.planing.ParingTable;
import ch.w3tec.qt.api.domain.service.planing.ParingTableGenerator;
import ch.w3tec.qt.api.domain.service.planing.ParingTableService;
import ch.w3tec.qt.api.persistence.entity.Game;
import ch.w3tec.qt.api.persistence.entity.Team;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GamePlanServiceTest {

    @InjectMocks
    private GamePlanService gamePlanService;

    @Mock
    private GameService gameService;

    @Mock
    private ParingTableService paringTableService;

    @Test
    public void generate_valid_returnsNumberOfGames() {
        Set<Team> teamSet = new HashSet<>();
        teamSet.add(Team.builder().id(UUID.randomUUID()).name("Team 1").build());
        teamSet.add(Team.builder().id(UUID.randomUUID()).name("Team 2").build());
        teamSet.add(Team.builder().id(UUID.randomUUID()).name("Team 3").build());
        teamSet.add(Team.builder().id(UUID.randomUUID()).name("Team 4").build());
        List<Team> teamList = new ArrayList<>(teamSet);
        Tournament tournament = Tournament.builder()
                .teams(teamSet)
                .build();
        ParingTable paringTable = ParingTableGenerator.getInstance()
                .withNumberOfTeams(4)
                .generate();

        when(paringTableService.generate(any())).thenReturn(paringTable);
        when(gameService.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Game> games = gamePlanService.generate(tournament);
        assertThat(games.size()).isEqualTo(6);
        assertThat(games.get(0).getHost()).isEqualTo(teamList.get(paringTable.getRounds().get(0).getFixtures().get(0).getHost()));
        assertThat(games.get(0).getGuest()).isEqualTo(teamList.get(paringTable.getRounds().get(0).getFixtures().get(0).getGuest()));
    }
}
