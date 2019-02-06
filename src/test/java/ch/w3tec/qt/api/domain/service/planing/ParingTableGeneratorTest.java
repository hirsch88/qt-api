package ch.w3tec.qt.api.domain.service.planing;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParingTableGeneratorTest {

    @Test
    public void build_tableForZero_returnsParingTable() {
        int numberOfTeams = 0;

        ParingTable paringTable = ParingTableGenerator.getInstance()
                .withNumberOfTeams(numberOfTeams)
                .generate();

        assertThat(paringTable.getRounds().size()).isEqualTo(0);
    }

    @Test
    public void build_tableForTwo_returnsParingTable() {
        int numberOfTeams = 2;

        ParingTable paringTable = ParingTableGenerator.getInstance()
                .withNumberOfTeams(numberOfTeams)
                .generate();

        testRound(paringTable, 0, Arrays.asList(1, 0));
    }

    @Test
    public void build_tableForThree_returnsParingTable() {
        int numberOfTeams = 3;

        ParingTable paringTable = ParingTableGenerator.getInstance()
                .withNumberOfTeams(numberOfTeams)
                .generate();

        testRound(paringTable, 0, Arrays.asList(1, 2));
        testRound(paringTable, 1, Arrays.asList(0, 1));
        testRound(paringTable, 2, Arrays.asList(2, 0));
    }

    @Test
    public void build_tableForFour_returnsParingTable() {
        int numberOfTeams = 4;

        ParingTable paringTable = ParingTableGenerator.getInstance()
                .withNumberOfTeams(numberOfTeams)
                .generate();

        testRound(paringTable, 0, Arrays.asList(3, 0, 1, 2));
        testRound(paringTable, 1, Arrays.asList(2, 3, 0, 1));
        testRound(paringTable, 2, Arrays.asList(3, 1, 2, 0));
    }

    @Test
    public void build_tableForFive_returnsParingTable() {
        int numberOfTeams = 5;

        ParingTable paringTable = ParingTableGenerator.getInstance()
                .withNumberOfTeams(numberOfTeams)
                .generate();

        testRound(paringTable, 0, Arrays.asList(1, 4, 2, 3));
        testRound(paringTable, 1, Arrays.asList(4, 2, 0, 1));
        testRound(paringTable, 2, Arrays.asList(2, 0, 3, 4));
        testRound(paringTable, 3, Arrays.asList(0, 3, 1, 2));
        testRound(paringTable, 4, Arrays.asList(3, 1, 4, 0));
    }

    @Test
    public void build_tableForSix_returnsParingTable() {
        int numberOfTeams = 6;

        ParingTable paringTable = ParingTableGenerator.getInstance()
                .withNumberOfTeams(numberOfTeams)
                .generate();

        testRound(paringTable, 0, Arrays.asList(5, 0, 1, 4, 2, 3));
        testRound(paringTable, 1, Arrays.asList(3, 5, 4, 2, 0, 1));
        testRound(paringTable, 2, Arrays.asList(5, 1, 2, 0, 3, 4));
        testRound(paringTable, 3, Arrays.asList(4, 5, 0, 3, 1, 2));
        testRound(paringTable, 4, Arrays.asList(5, 2, 3, 1, 4, 0));
    }

    private void testRound(ParingTable paringTable, int roundIndex, List<Integer> pairs) {
        Round round = paringTable.getRounds().get(roundIndex);
        int pairIndex = 0;
        for (Fixture fixture : round.getFixtures()) {
            assertThat(fixture.getHost()).isEqualTo(pairs.get(pairIndex++));
            assertThat(fixture.getGuest()).isEqualTo(pairs.get(pairIndex++));
        }
    }
}