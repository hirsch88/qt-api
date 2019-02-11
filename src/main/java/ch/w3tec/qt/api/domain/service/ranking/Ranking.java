package ch.w3tec.qt.api.domain.service.ranking;

import ch.w3tec.qt.api.persistence.entity.Team;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Ranking {
    private int rank;
    private Team team;
    private int played;
    private int points;
    private int wins;
    private int draws;
    private int loses;

    public void increasePlayed() {
        played = played + 1;
    }

    public void addWin() {
        addPoints(3);
        wins = wins + 1;
    }

    public void addDraw() {
        addPoints(1);
        draws = draws + 1;
    }

    public void addLose() {
        loses = loses + 1;
    }

    private void addPoints(int points) {
        this.points = this.points + points;
    }

    public int compareTo(Ranking another) {
        if (points < another.points) {
            return -1;
        } else if (points == another.points) {
            return 0;
        }
        return 1;
    }
}
