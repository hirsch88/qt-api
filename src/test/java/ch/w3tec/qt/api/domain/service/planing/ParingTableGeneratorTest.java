package ch.w3tec.qt.api.domain.service.planing;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParingTableGeneratorTest {

    @Test
    public void generate_tableForSix_returnsParingTable() {
        int numberOfTeams = 6;

        ParingTable paringTable = ParingTableGenerator.generate(numberOfTeams);

        assertThat(paringTable.getRounds().size())
                .isEqualTo(numberOfTeams - 1);
        assertThat(paringTable.getRounds().get(0).getFixtures().size())
                .isEqualTo(numberOfTeams / 2);

        testRoundOfSixPlayers(paringTable, 0, Arrays.asList(5, 0, 1, 4, 2, 3));
        testRoundOfSixPlayers(paringTable, 1, Arrays.asList(3, 5, 4, 2, 0, 1));
        testRoundOfSixPlayers(paringTable, 2, Arrays.asList(5, 1, 2, 0, 3, 4));
        testRoundOfSixPlayers(paringTable, 3, Arrays.asList(4, 5, 0, 3, 1, 2));
        testRoundOfSixPlayers(paringTable, 4, Arrays.asList(5, 2, 3, 1, 4, 0));

    }

    private void testRoundOfSixPlayers(ParingTable paringTable, int roundIndex, List<Integer> pairs) {
        Round round = paringTable.getRounds().get(roundIndex);
        assertThat(round.getFixtures().get(0).getHost()).isEqualTo(pairs.get(0));
        assertThat(round.getFixtures().get(0).getGuest()).isEqualTo(pairs.get(1));
        assertThat(round.getFixtures().get(1).getHost()).isEqualTo(pairs.get(2));
        assertThat(round.getFixtures().get(1).getGuest()).isEqualTo(pairs.get(3));
        assertThat(round.getFixtures().get(2).getHost()).isEqualTo(pairs.get(4));
        assertThat(round.getFixtures().get(2).getGuest()).isEqualTo(pairs.get(5));
    }
}