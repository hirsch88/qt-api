package ch.w3tec.qt.api.domain.service.planing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParingTable {

    @Setter(AccessLevel.PROTECTED)
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

}
