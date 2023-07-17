package net.atrophygames.obscrts.countdown;

import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.GameState;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.role.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameEndCountdown extends Countdown {


    private TTT plugin;
    private final int GAME_TIME = 60 * 10;
    @Getter
    private int seconds;

    public GameEndCountdown(TTT plugin) {
        this.plugin = plugin;
        seconds = GAME_TIME;
    }

    @Override
    public void start() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
                for(Player currentPlayer : Bukkit.getOnlinePlayers())
                    ingameState.getIngameScoreboard().updateScoreboard(currentPlayer, seconds);
                if(seconds == 0) {
                    ingameState.setWinningRole(Role.INNOCENT);
                    stop();
                }
                seconds--;
            }
        }, 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        plugin.getGameStateManager().setGameState(GameState.ENDING_STATE);
    }
}
