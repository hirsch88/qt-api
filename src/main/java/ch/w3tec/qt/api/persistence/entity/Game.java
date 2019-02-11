package ch.w3tec.qt.api.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Game extends Auditable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne()
    @ToString.Exclude
    @JsonIgnore
    private Tournament tournament;

    private Integer round;

    @ManyToOne()
    @ToString.Exclude
    private Team host;

    private Integer hostScore;

    @ManyToOne()
    @ToString.Exclude
    private Team guest;

    private Integer guestScore;

    public boolean wasPlayed() {
        return hostScore != null && guestScore != null;
    }
}
