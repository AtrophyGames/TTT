package net.atrophygames.obscrts.scoreboards;

import net.atrophygames.obscrts.TTT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class WarmUpScoreboard {


    private TTT plugin;
    private int startPlayers;

    public WarmUpScoreboard(TTT plugin) {
        this.plugin = plugin;
        startPlayers = plugin.getPlayers().size();
    }

    public void setScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("main", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§lATROPHYGAMES.NET");

        Team tPlayers = scoreboard.registerNewTeam("players");
        Team tGrace = scoreboard.registerNewTeam("grace");

        objective.getScore("").setScore(11);
        objective.getScore("Karma§8:").setScore(10);
        objective.getScore("§b" + plugin.getStatsManager().getKarma(player)).setScore(9);
        objective.getScore(" ").setScore(8);
        objective.getScore("Spieler§8:").setScore(7);
        objective.getScore("§a").setScore(6);
        objective.getScore("  ").setScore(5);
        objective.getScore("Map§8:").setScore(4);
        objective.getScore("§e" + plugin.getVoting().getWinnerMap().convertMapName(
                plugin.getVoting().getWinnerMap().getName())).setScore(3);
        objective.getScore("   ").setScore(2);
        objective.getScore("Schutzzeit§8:").setScore(1);
        objective.getScore("§e").setScore(0);

        tPlayers.addEntry("§a");
        tPlayers.setPrefix("§a" + plugin.getPlayers().size() + "§7/§a" + startPlayers);

        tGrace.addEntry("§e");
        tGrace.setPrefix("§e00:30");

        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard(Player player, int grace) {
        Scoreboard scoreboard = player.getScoreboard();

        Team tPlayers = scoreboard.getTeam("players");
        tPlayers.setPrefix("§a" + plugin.getPlayers().size() + "§7/§a" + startPlayers);

        Team tGrace = scoreboard.getTeam("grace");
        tGrace.setPrefix("§e00:" + String.format("%02d", grace));
    }
}
