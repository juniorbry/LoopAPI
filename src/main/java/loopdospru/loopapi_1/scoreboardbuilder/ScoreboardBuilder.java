package loopdospru.loopapi_1.scoreboardbuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardBuilder {
    private final Plugin plugin;
    private final String name;
    private String title;
    private List<String> animatedTitle;
    private int animationInterval;
    private final List<String> lines;
    private final Map<String, TeamInfo> teamMap;
    private final Map<Player, Scoreboard> playerScoreboards;
    private final Map<Integer, LineProvider> dynamicLines;

    public ScoreboardBuilder(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.lines = new ArrayList<>();
        this.teamMap = new HashMap<>();
        this.playerScoreboards = new HashMap<>();
        this.dynamicLines = new HashMap<>();
    }

    public ScoreboardBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ScoreboardBuilder setAnimatedTitle(List<String> animatedTitle, int interval) {
        this.animatedTitle = animatedTitle;
        this.animationInterval = interval;
        startTitleAnimation();
        return this;
    }

    public ScoreboardBuilder addLine(String line) {
        this.lines.add(line);
        return this;
    }

    public ScoreboardBuilder addDynamicLine(LineProvider lineProvider) {
        this.dynamicLines.put(this.lines.size(), lineProvider);
        this.lines.add(""); // Placeholder
        return this;
    }

    public ScoreboardBuilder addTeamLine(String teamName, String prefix, String suffix) {
        this.teamMap.put(teamName, new TeamInfo(prefix, suffix));
        return this;
    }

    public void build() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player);
        }
        ScoreboardManager.addScoreboard(name, this);
    }

    public void addPlayer(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(scoreboard);
        playerScoreboards.put(player, scoreboard);
        updateScoreboard(player);
    }

    public void removePlayer(Player player) {
        playerScoreboards.remove(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); // Clear scoreboard
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = playerScoreboards.get(player);
        org.bukkit.scoreboard.Objective objective = scoreboard.getObjective(name);

        if (objective == null) {
            objective = scoreboard.registerNewObjective(name, title);
            objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        }

        int score = lines.size();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (dynamicLines.containsKey(i)) {
                line = dynamicLines.get(i).getLine(player);
            }
            objective.getScore(ChatColor.translateAlternateColorCodes('&', line)).setScore(score--);
        }

        for (Map.Entry<String, TeamInfo> entry : teamMap.entrySet()) {
            Team team = scoreboard.getTeam(entry.getKey());
            if (team == null) {
                team = scoreboard.registerNewTeam(entry.getKey());
            }
            team.setPrefix(entry.getValue().getPrefix());
            team.setSuffix(entry.getValue().getSuffix());
        }
    }

    public void update() {
        for (Player player : playerScoreboards.keySet()) {
            updateScoreboard(player);
        }
    }

    private void startTitleAnimation() {
        if (animatedTitle == null || animationInterval <= 0) return;

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= animatedTitle.size()) {
                    index = 0;
                }
                title = animatedTitle.get(index);
                index++;
                update();
            }
        }.runTaskTimer(plugin, 0, animationInterval * 20);
    }

    public interface LineProvider {
        String getLine(Player player);
    }

    private static class TeamInfo {
        private final String prefix;
        private final String suffix;

        public TeamInfo(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }
    }
}
