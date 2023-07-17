package net.atrophygames.obscrts.gamestate.states;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.countdown.EndingCountdown;
import net.atrophygames.obscrts.gamestate.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndingState extends GameState {


    private EndingCountdown endingCountdown;

    public EndingState(TTT plugin) {
        endingCountdown = new EndingCountdown(plugin);
    }

    @Override
    public void start() {
        for(Player currentPlayer: Bukkit.getOnlinePlayers())
            currentPlayer.getInventory().clear();
        endingCountdown.start();
    }

    @Override
    public void stop() {
        for(Player currentPlayer : Bukkit.getOnlinePlayers())
            currentPlayer.kickPlayer("");
        Bukkit.getServer().shutdown();
    }
}
