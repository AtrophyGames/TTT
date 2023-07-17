package net.atrophygames.obscrts.scoreboards;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.role.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class IngameScoreboard {


    private TTT plugin;
    private int max_players;

    public IngameScoreboard(TTT plugin) {
        this.plugin = plugin;
        max_players = plugin.getPlayers().size();
    }

    public void setScoreboard(Player player) {
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("main", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§lATROPHYGAMES.NET");

        Team tPointsTraitor = scoreboard.registerNewTeam("points_traitor");
        Team tPointsDetective = scoreboard.registerNewTeam("points_detective");
        Team tPlayers = scoreboard.registerNewTeam("players");
        Team tTime = scoreboard.registerNewTeam("time");

        switch(playerRole) {
            case TRAITOR:
                objective.getScore("").setScore(11);
                objective.getScore("Shop-Punkte:").setScore(10);
                objective.getScore("§4").setScore(9);

                tPointsTraitor.addEntry("§4");
                tPointsTraitor.setPrefix("§4" + plugin.getRoleInventory().getPointManager().getPlayerPoints(player));
                break;
            case DETECTIVE:
                objective.getScore("").setScore(11);
                objective.getScore("Shop-Punkte:").setScore(10);
                objective.getScore("§9").setScore(9);

                tPointsDetective.addEntry("§9");
                tPointsDetective.setPrefix("§9" + plugin.getRoleInventory().getPointManager().getPlayerPoints(player));
                break;
        }

        objective.getScore(" ").setScore(8);
        objective.getScore("Spieler§8:").setScore(7);
        objective.getScore("§a").setScore(6);
        objective.getScore("  ").setScore(5);
        objective.getScore("Ende§8:").setScore(4);
        objective.getScore("§e").setScore(3);
        objective.getScore("   ").setScore(2);
        objective.getScore("Hit-Cooldown§8:").setScore(1);
        objective.getScore("§cDeaktiviert").setScore(0);

        tPlayers.addEntry("§a");
        tPlayers.setPrefix("§a" + plugin.getPlayers().size() + "§7/§a" + max_players);

        tTime.addEntry("§e");
        tTime.setPrefix("§e10:00");

        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);

        Team tPointsTraitor = scoreboard.getTeam("points_traitor");
        Team tPointsDetective = scoreboard.getTeam("points_detective");

        switch(playerRole) {
            case TRAITOR:
                tPointsTraitor.addEntry("§4");
                tPointsTraitor.setPrefix("§4" + plugin.getRoleInventory().getPointManager().getPlayerPoints(player));
                break;
            case DETECTIVE:
                tPointsDetective.addEntry("§9");
                tPointsDetective.setPrefix("§9" + plugin.getRoleInventory().getPointManager().getPlayerPoints(player));
                break;
        }
        Team tPlayers = scoreboard.getTeam("players");
        tPlayers.setPrefix("§a" + plugin.getPlayers().size() + "§7/§a" + max_players);
    }

    public void updateScoreboard(Player player, int time) {
        Scoreboard scoreboard = player.getScoreboard();
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);

        Team tTime = scoreboard.getTeam("time");
        tTime.setPrefix("§e" + String.format("%02d:%02d", (time / 60), (time % 60)));
    }
}
