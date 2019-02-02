package ch.w3tec.qt.api.persistence.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "tournaments")
@Getter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Tournament extends Auditable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @EqualsAndHashCode.Include
    private UUID id;

    @Size(max = 40)
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentState state;

}
