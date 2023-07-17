package net.atrophygames.obscrts.countdown;

import com.connorlinfoot.titleapi.TitleAPI;
import lombok.Getter;
import lombok.Setter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.GameState;
import net.atrophygames.obscrts.gamestate.GameStateManager;
import net.atrophygames.obscrts.gamestate.states.LobbyState;
import net.atrophygames.obscrts.voting.Map;
import net.atrophygames.obscrts.voting.Voting;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

public class LobbyCountdown extends Countdown {


    private static final int IDLE_TIME = 15,
                             COUNTDOWN_TIME = 60;

    @Getter @Setter
    private int seconds;
    private int idleID;
    private boolean isIdling;
    @Getter
    private boolean isRunning;
    private final GameStateManager gameStateManager;

    public LobbyCountdown(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        seconds = COUNTDOWN_TIME;
    }

    @Override
    public void start() {
        isRunning = true;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {
            Voting voting = gameStateManager.getPlugin().getVoting();
            Map winnerMap;

            @Override
            public void run() {
                switch(seconds) {
                    case 60: case 30: case 15: case 10: case 5: case 4: case 3: case 2:
                        Bukkit.broadcastMessage(TTT.PREFIX + "§7Das Spiel startet in §e" + seconds + " §7Sekunden");
                        if(seconds == 10) for(Player currentPlayer : Bukkit.getOnlinePlayers())
                            currentPlayer.getInventory().clear(4);
                        if(seconds == 5)for(Player currentPlayer : Bukkit.getOnlinePlayers())
                            TitleAPI.sendTitle(currentPlayer, "§4TTT", winnerMap.convertMapName(winnerMap.getName()),
                                    10, 50, 10);
                        break;
                    case 7:

                        if(voting != null) winnerMap = voting.getWinnerMap();
                        else {
                            ArrayList<Map> maps = gameStateManager.getPlugin().getMaps();
                            Collections.shuffle(maps);
                            winnerMap = maps.get(0);
                        }
                        for(Player currentPlayer : Bukkit.getOnlinePlayers())
                            ((LobbyState) gameStateManager.getCurrentGameState()).getLobbyScoreboard()
                                    .updateScoreboard(currentPlayer, winnerMap.convertMapName(winnerMap.getName()));
                        Bukkit.broadcastMessage(TTT.PREFIX + "§7Es wird auf §a" + winnerMap.convertMapName(winnerMap.getName()) + " §7gespielt!");
                        unloadUnusedMaps(voting, winnerMap);
                        break;
                    case 1:
                        Bukkit.broadcastMessage(TTT.PREFIX + "§7Das Spiel startet in §eeiner §7Sekunde");
                        break;
                    case 0:
                        stop();
                        break;
                }
                for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                    if(isRunning) {
                        currentPlayer.setLevel(seconds);
                        currentPlayer.setExp((float) seconds / COUNTDOWN_TIME);
                        if(gameStateManager.getCurrentGameState() instanceof LobbyState)
                            switch(seconds) {
                                case 60: case 30: case 15: case 10: case 5: case 3: case 2: case 1:
                                    currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                                case 0:
                                    currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            }
                        }
                    }
                seconds--;
            }
        }, 0, 20);
    }

    private void unloadUnusedMaps(Voting voting, Map winnerMap) {
        Map[] votingMaps = voting.getVotingMaps();
        for(Map currentMap : votingMaps)
            if(currentMap != winnerMap) Bukkit.getServer().unloadWorld(currentMap.getName().toLowerCase(), false);
    }

    @Override
    public void stop() {
        if(isRunning) {
            Bukkit.getScheduler().cancelTask(taskID);
            isRunning = false;
            seconds = COUNTDOWN_TIME;
        }
        gameStateManager.setGameState(GameState.WARM_UP_STATE);
    }

    public void startIdle() {
        isIdling = true;
        idleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable() {

            @Override
            public void run() {
                if(LobbyState.MIN_PLAYERS - gameStateManager.getPlugin().getPlayers().size() == 1) {
                    Bukkit.broadcastMessage(TTT.PREFIX + "§cEs fehlt noch §eein weiterer §cSpieler");
                } else {
                    Bukkit.broadcastMessage(TTT.PREFIX + "§cEs fehlen noch §e" +
                            (LobbyState.MIN_PLAYERS - gameStateManager.getPlugin().getPlayers().size())
                            + " weitere §cSpieler");
                }
            }
        }, 0, 20 * IDLE_TIME);
    }

    public void stopIdle() {
        if(isIdling) {
            Bukkit.getScheduler().cancelTask(idleID);
            isIdling = false;
        }
    }
}
