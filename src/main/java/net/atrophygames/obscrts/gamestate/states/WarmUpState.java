package net.atrophygames.obscrts.gamestate.states;

import com.connorlinfoot.titleapi.TitleAPI;
import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.GameState;
import net.atrophygames.obscrts.role.Role;
import net.atrophygames.obscrts.scoreboards.WarmUpScoreboard;
import net.atrophygames.obscrts.voting.Map;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class WarmUpState extends GameState {


    private TTT plugin;
    private Map map;
    private ArrayList<Player> players;
    private int taskID, seconds;
    @Getter
    private boolean afterTeleport;

    public WarmUpState(TTT plugin) {
        this.plugin = plugin;
        seconds = 5;
        afterTeleport = false;
    }

    @Override
    public void start() {
        plugin.setWarmUpScoreboard(new WarmUpScoreboard(plugin));
        Collections.shuffle(plugin.getPlayers());
        players = plugin.getPlayers();

        map = plugin.getVoting().getWinnerMap();
        map.load();

        for(int i = 0; i < players.size(); i++) {
            players.get(i).teleport(map.getSpawnLocations()[i]);
            plugin.getWarmUpScoreboard().setScoreboard(players.get(i));
        }
        afterTeleport = true;
        for(Player currentPlayer : players) {
            currentPlayer.getInventory().clear();
            if(plugin.getRoleManager().getPlayerRole(currentPlayer) != Role.INNOCENT)
                plugin.getRoleInventory().getPointManager().setPlayerPoints(currentPlayer, 10);
        }

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                switch(seconds) {
                    case 3:
                        for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                            TitleAPI.sendTitle(currentPlayer,"§6" + seconds,null, 0, 20, 0);
                            currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                        break;
                    case 2:
                        for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                            TitleAPI.sendTitle(currentPlayer,"§e" + seconds,null, 0, 20, 0);
                            currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                        break;
                    case 1:
                        for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                            TitleAPI.sendTitle(currentPlayer,"§c" + seconds,null, 0, 20, 0);
                            currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            }
                        break;
                    case 0:
                        plugin.getGameStateManager().setGameState(GameState.INGAME_STATE);
                        break;
                }
                seconds--;
            }
        }, 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
