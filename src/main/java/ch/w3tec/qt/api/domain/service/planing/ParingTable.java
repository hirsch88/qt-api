package ch.w3tec.qt.api.domain.service.planing;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
class ParingTable {

    private int numberOfTeams;
    private int numberOfRounds;
    private int numberOfFields;
    private List<Round> rounds;

    ParingTable(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
        this.numberOfRounds = numberOfTeams - 1;
        this.numberOfFields = numberOfTeams / 2;

        this.rounds = new ArrayList<>();
        for (int i = 0; i < this.numberOfRounds; i++) {
            this.rounds.add(new Round(i, this.numberOfFields));
        }
    }

//    ParingTable fillOutTable() {
//        fillOutLastTeam();
//        fillOutHostSide();
//        fillOutGuestSide();
//        swapSideForFirstColumn();
//        return this;
//    }
//
//    private void fillOutLastTeam() {
//        rounds = rounds.stream()
//                .peek(round -> round.getFixtures().get(0).setGuest(numberOfTeams - 1))
//                .collect(Collectors.toList());
//    }
//
//    private void fillOutHostSide() {
//        for (Round round : rounds) {
//            List<Fixture> fixtures = round.getFixtures();
//            for (Fixture fixture : fixtures) {
//                int host = nextHostSideCounter();
//                fixture.setHost(host);
//            }
//        }
//    }
//
//    private Integer nextHostSideCounter() {
//        if (counterHostSide == (numberOfTeams - 1)) {
//            counterHostSide = 0;
//        }
//        return counterHostSide++;
//    }
//
//    private void fillOutGuestSide() {
//        for (Round round : rounds) {
//            List<Fixture> fixtures = round.getFixtures();
//            for (Fixture fixture : fixtures) {
//                if (fixture.getFieldIndex() > 0) {
//                    int guest = nextGuestSideCounter();
//                    fixture.setGuest(guest);
//                }
//            }
//        }
//    }
//
//    private Integer nextGuestSideCounter() {
//        if (counterGuestSide < 0) {
//            counterGuestSide = numberOfTeams - 2;
//        }
//        return counterGuestSide--;
//    }
//
//    private void swapSideForFirstColumn() {
//        rounds = rounds.stream()
//                .peek(round -> round.setFixtures(round.getFixtures().stream()
//                        .peek(fixture -> {
//                            if (fixture.getFieldIndex() == 0 && round.getIndex() % 2 == 0) {
//                                fixture.swapPairing();
//                            }
//                        })
//                        .collect(Collectors.toList())))
//                .collect(Collectors.toList());
//    }

}
