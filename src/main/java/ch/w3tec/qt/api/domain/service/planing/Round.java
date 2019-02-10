package ch.w3tec.qt.api.domain.service.planing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Round {

    @EqualsAndHashCode.Include
    private int index;

    @Setter
    private List<Fixture> fixtures;

    Round(int index, int numberOfFields) {
        this.index = index;
        this.fixtures = new ArrayList<>();

        for (int i = 0; i < numberOfFields; i++) {
            this.fixtures.add(new Fixture(i));
        }
    }

}
