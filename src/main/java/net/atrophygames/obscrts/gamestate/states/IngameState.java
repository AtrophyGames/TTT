package net.atrophygames.obscrts.gamestate.states;

import lombok.Getter;
import lombok.Setter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.api.CoinAPI;
import net.atrophygames.obscrts.countdown.GameEndCountdown;
import net.atrophygames.obscrts.countdown.RoleCountdown;
import net.atrophygames.obscrts.gamestate.GameState;
import net.atrophygames.obscrts.role.Role;
import net.atrophygames.obscrts.scoreboards.IngameScoreboard;
import net.atrophygames.obscrts.stats.StatsManager;
import net.atrophygames.obscrts.util.ConfigLocationUtil;
import net.atrophygames.obscrts.voting.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IngameState extends GameState {


    private TTT plugin;
    private StatsManager statsManager;
    @Getter
    private Map map;
    @Getter
    private ArrayList<Player> spectators;
    private RoleCountdown roleCountdown;
    @Getter
    private GameEndCountdown gameEndCountdown;
    @Setter
    private Role winningRole;
    @Getter @Setter
    private boolean grace;
    @Getter
    private IngameScoreboard ingameScoreboard;

    public IngameState(TTT plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
        roleCountdown = new RoleCountdown(plugin);
        gameEndCountdown = new GameEndCountdown(plugin);
        spectators = new ArrayList<>();
    }

    @Override
    public void start() {
        grace = true;
        roleCountdown.start();
        ingameScoreboard = new IngameScoreboard(plugin);

        for(Player currentPlayer : plugin.getPlayers()) {
            statsManager.setPlayedGames(currentPlayer, statsManager.getPlayedGames(currentPlayer) + 1);
            statsManager.setLosses(currentPlayer, statsManager.getLosses(currentPlayer) + 1);
        }

        map = plugin.getVoting().getWinnerMap();
        map.load();
    }

    public void checkForGameEnding() {
       if(plugin.getRoleManager().getTraitorPlayers().isEmpty()) {
           winningRole = Role.INNOCENT;
           plugin.getGameStateManager().setGameState(GameState.ENDING_STATE);
           for(Player currentPlayer : plugin.getPlayers()) {
               plugin.getStatsManager().setKarma(currentPlayer, 20);
               CoinAPI.getApi().addCoins(currentPlayer.getUniqueId(),
                       (int) ((-0.3 * getGameEndCountdown().getSeconds()) + 200) + 50);
           }

       } else if(plugin.getRoleManager().getTraitorPlayers().size() == plugin.getPlayers().size()) {
           winningRole = Role.TRAITOR;
           plugin.getGameStateManager().setGameState(GameState.ENDING_STATE);
           for(String currentPlayer : plugin.getRoleManager().getTraitorPlayers()) {
               plugin.getStatsManager().setKarma(Bukkit.getPlayer(currentPlayer), 20);
               CoinAPI.getApi().addCoins(Bukkit.getPlayer(currentPlayer).getUniqueId(),
                       (int) ((-0.3 * getGameEndCountdown().getSeconds()) + 200) + 50);
           }
       }
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        player.setFlying(true);

        for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
            currentPlayer.hidePlayer(player);
            player.teleport(map.getSpectatorSpawnLocation());
        }
    }

    @Override
    public void stop() {
        Bukkit.broadcastMessage(TTT.PREFIX + "§6§lDas Spiel ist aus!");

        switch(winningRole) {
            case TRAITOR:
                Bukkit.broadcastMessage(TTT.PREFIX + "§7Die " + winningRole.getChatColor() +
                        "Verräter §7haben alle anderen eliminiert!");
                for(String currentPlayerName : plugin.getRoleManager().getTraitorPlayers()) {
                    CoinAPI.getApi().addCoins(Bukkit.getPlayer(currentPlayerName).getUniqueId(),
                            (int) ((-0.3 * getGameEndCountdown().getSeconds()) + 200) + 35);
                    statsManager.setWins(Bukkit.getPlayer(currentPlayerName),
                            statsManager.getWins(Bukkit.getPlayer(currentPlayerName)) + 1);
                    statsManager.setLosses(Bukkit.getPlayer(currentPlayerName),
                            statsManager.getLosses(Bukkit.getPlayer(currentPlayerName)) - 1);
                }
                break;
            case INNOCENT:
                Bukkit.broadcastMessage(TTT.PREFIX + "§7Die " + winningRole.getChatColor() +
                        "Unschuldigen §7haben überlebt!");
                for(Player currentPlayer : plugin.getPlayers()) {
                    CoinAPI.getApi().addCoins(currentPlayer.getUniqueId(),
                            (int) ((-0.3 * getGameEndCountdown().getSeconds()) + 200) + 35);
                    plugin.getStatsManager().setWins(currentPlayer, statsManager.getWins(currentPlayer) + 1);
                }
                break;
        }

        plugin.getStatsManager().updateTables();

        ConfigLocationUtil configLocationUtil = new ConfigLocationUtil(plugin, "lobby");
        for(Player currentPlayer : Bukkit.getOnlinePlayers())
            if(configLocationUtil.loadLocation() != null) currentPlayer.teleport(configLocationUtil.loadLocation());
    }
}
