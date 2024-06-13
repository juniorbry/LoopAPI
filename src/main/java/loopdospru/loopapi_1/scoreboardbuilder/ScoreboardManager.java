package loopdospru.loopapi_1.scoreboardbuilder;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    private static final Map<String, ScoreboardBuilder> scoreboards = new HashMap<>();
    private static final Map<Player, String> playerScoreboards = new HashMap<>();

    public static void addScoreboard(String name, ScoreboardBuilder scoreboard) {
        scoreboards.put(name, scoreboard);
    }

    public static ScoreboardBuilder getScore(String name) {
        return scoreboards.get(name);
    }

    public static void setPlayerScoreboard(Player player, String scoreboardName) {
        String currentScoreboard = playerScoreboards.get(player);
        if (currentScoreboard != null) {
            scoreboards.get(currentScoreboard).removePlayer(player);
        }
        playerScoreboards.put(player, scoreboardName);
        scoreboards.get(scoreboardName).addPlayer(player);
    }

    public static boolean contains(Player player) {
        return playerScoreboards.containsKey(player);
    }

    public static ScoreboardBuilder getScoreByPlayer(Player player) {
        String scoreboardName = playerScoreboards.get(player);
        return scoreboards.get(scoreboardName);
    }

}

