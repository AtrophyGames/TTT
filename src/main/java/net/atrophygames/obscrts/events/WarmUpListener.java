package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.WarmUpState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WarmUpListener implements Listener {


    private TTT plugin;

    public WarmUpListener(TTT plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof WarmUpState)) return;
        WarmUpState warmUpState = (WarmUpState) plugin.getGameStateManager().getCurrentGameState();

        for(Player currentPlayer : Bukkit.getOnlinePlayers())
            plugin.getWarmUpScoreboard().updateScoreboard(currentPlayer, 30);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof WarmUpState)) return;
        WarmUpState warmUpState = (WarmUpState) plugin.getGameStateManager().getCurrentGameState();
        if(warmUpState.isAfterTeleport()) {
            Location newLocation = event.getFrom();
            newLocation.setY(event.getTo().getY());
            newLocation.setYaw(event.getTo().getYaw());
            newLocation.setPitch(event.getTo().getPitch());
            event.setTo(newLocation);
        }
    }
}
