package ch.w3tec.qt.api.domain.service.planing;

import java.util.List;

public class ParingTableGenerator implements Generator<ParingTable> {

    private int counterHostSide;
    private int counterGuestSide;
    private int numberOfTeams;
    private boolean hasLuckyOne = false;

    public static ParingTableGenerator getInstance() {
        return new ParingTableGenerator();
    }

    public ParingTableGenerator withNumberOfTeams(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
        if (this.numberOfTeams % 2 != 0) {
            hasLuckyOne = true;
            this.numberOfTeams++;
        }
        return this;
    }

    @Override
    public ParingTable generate() {
        counterHostSide = 0;
        counterGuestSide = numberOfTeams - 2;

        ParingTable paringTable = new ParingTable(numberOfTeams);
        fillOutLastTeam(paringTable);
        fillOutHostSide(paringTable);
        fillOutGuestSide(paringTable);
        swapSideForFirstColumn(paringTable);

        if (hasLuckyOne) {
            removeLuckyOne(paringTable);
            this.numberOfTeams--;
            paringTable.setNumberOfTeams(this.numberOfTeams);
        }

        return paringTable;
    }

    private void fillOutLastTeam(ParingTable paringTable) {
        for (Round round : paringTable.getRounds()) {
            round.getFixtures().get(0).setGuest(numberOfTeams - 1);
        }
    }

    private Integer nextHostSideCounter() {
        if (counterHostSide == (numberOfTeams - 1)) {
            counterHostSide = 0;
        }
        return counterHostSide++;
    }

    private void fillOutHostSide(ParingTable paringTable) {
        for (Round round : paringTable.getRounds()) {
            List<Fixture> fixtures = round.getFixtures();
            for (Fixture fixture : fixtures) {
                int host = nextHostSideCounter();
                fixture.setHost(host);
            }
        }
    }

    private Integer nextGuestSideCounter() {
        if (counterGuestSide < 0) {
            counterGuestSide = numberOfTeams - 2;
        }
        return counterGuestSide--;
    }

    private void fillOutGuestSide(ParingTable paringTable) {
        for (Round round : paringTable.getRounds()) {
            List<Fixture> fixtures = round.getFixtures();
            for (Fixture fixture : fixtures) {
                if (fixture.getFieldIndex() > 0) {
                    int guest = nextGuestSideCounter();
                    fixture.setGuest(guest);
                }
            }
        }
    }

    private void swapSideForFirstColumn(ParingTable paringTable) {
        for (Round round : paringTable.getRounds()) {
            List<Fixture> fixtures = round.getFixtures();
            for (Fixture fixture : fixtures) {
                if (fixture.getFieldIndex() == 0 && round.getIndex() % 2 == 0) {
                    fixture.swapPairing();
                }
            }
        }
    }

    private void removeLuckyOne(ParingTable paringTable) {
        for (Round round : paringTable.getRounds()) {
            List<Fixture> fixtures = round.getFixtures();
            fixtures.remove(0);
            round.setFixtures(fixtures);
        }
    }

}
