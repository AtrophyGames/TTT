package net.atrophygames.obscrts.commands;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {


    private TTT plugin;
    private StatsManager statsManager;

    public StatsCommand(TTT plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player player = null;
        if(args.length == 0)
            player = (Player) commandSender;
        else if(args.length == 1)
            player = Bukkit.getPlayer(args[0]);

        if(player == null) return false;

        switch(label) {
            case "stats":
                sendStatsToPlayer(player, "30 Tage");
                break;
            case "statsall":
                sendStatsToPlayer(player, "insgesamt");
                break;
        }
        return true;
    }

    private void sendStatsToPlayer(Player player, String label) {
        player.sendMessage("§7-= §eStatistiken von §6" + player.getName() + " §7(" + label + ") =-");
        player.sendMessage(" §7Position im Ranking§8: §e");
        player.sendMessage(" §7Karma§8: §e" + statsManager.getKarma(player));
        player.sendMessage(" §7Karma pro Runde§8: §e" + getKarmaPerRound(player));
        player.sendMessage(" §7Kills§8: §e" + statsManager.getKills(player));
        player.sendMessage(" §7Deaths§8: §e" + statsManager.getDeaths(player));
        player.sendMessage(" §7Gespielte Spiele§8: §e" + statsManager.getPlayedGames(player));
        player.sendMessage(" §7Gewonnene Spiele§8: §e" + statsManager.getWins(player));
        player.sendMessage(" §7Siegeswahrscheinlichkeit§8: §e" + getWinPercentage(player) + " %");
        player.sendMessage(" §7Falsch-Kill-Quote§8: §e" + getFQK(player) + " %");
        player.sendMessage("§7---------------------");
    }

    private int getKarmaPerRound(Player player) {
        if(statsManager.getPlayedGames(player) == 0)
            return 0;
        return statsManager.getKarma(player) / statsManager.getPlayedGames(player);
    }

    private int getWinPercentage(Player player) {
        if(statsManager.getPlayedGames(player) == 0)
            return 0;
        return (statsManager.getWins(player) / statsManager.getPlayedGames(player)) * 100;
    }

    private int getFQK(Player player) {
        if(statsManager.getKills(player) == 0)
            return 0;
        return (statsManager.getFK(player) / statsManager.getKills(player) * 100);
    }
}
