package save;

import java.io.Serializable;
import java.time.Instant;

public class GameHistory implements Serializable {

    private String title;
    private String path;
    private long lastPlayed;

    public GameHistory(String title, String path) {
        this.title = title;
        this.path = path;
        this.lastPlayed = Instant.now().toEpochMilli();
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public void updateLastPlayed() {
        this.lastPlayed = Instant.now().toEpochMilli();
    }

}
