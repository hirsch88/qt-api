package ch.w3tec.qt.api.application.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UpdateGameRequest {

    @NotNull
    private Integer hostScore;

    @NotNull
    private Integer guestScore;

}
