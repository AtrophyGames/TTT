package net.atrophygames.obscrts.scoreboards;

import net.atrophygames.obscrts.TTT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class LobbyScoreboard {


    private TTT plugin;

    public LobbyScoreboard(TTT plugin) {
        this.plugin = plugin;
    }

    public void setScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("main", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§lATROPHYGAMES.NET");

        Team tMap = scoreboard.registerNewTeam("map");

        objective.getScore("").setScore(8);
        objective.getScore("Map§8:").setScore(7);
        objective.getScore("§e").setScore(6);
        objective.getScore(" ").setScore(5);
        objective.getScore("Karma§8:").setScore(4);
        objective.getScore("§b" + plugin.getStatsManager().getKarma(player)).setScore(3);
        objective.getScore("  ").setScore(2);
        objective.getScore("Hit-Cooldown§8:").setScore(1);
        objective.getScore("§cDeaktiviert").setScore(0);

        tMap.addEntry("§e");
        tMap.setPrefix("§e§k_-_-_-");

        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard(Player player, String string) {
        Scoreboard scoreboard = player.getScoreboard();

        Team tMap = scoreboard.getTeam("map");
        tMap.setPrefix("§e" + string);
    }
}
