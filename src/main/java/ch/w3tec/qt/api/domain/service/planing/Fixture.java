package ch.w3tec.qt.api.domain.service.planing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Fixture {

    private Integer host;
    private Integer guest;

    @EqualsAndHashCode.Exclude
    private int fieldIndex;

    Fixture(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    void swapPairing() {
        int tempHost = getHost();
        setHost(getGuest());
        setGuest(tempHost);
    }
}
