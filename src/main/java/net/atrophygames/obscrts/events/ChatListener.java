package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.role.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final TTT plugin;

    public ChatListener(TTT plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDefaultChat(AsyncPlayerChatEvent event) {
        if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        event.setFormat(getChatFormat(ChatColor.GOLD, event.getPlayer()) + event.getMessage());
    }

    @EventHandler
    public void onIngameStateChat(AsyncPlayerChatEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        Player player = event.getPlayer();

        if(ingameState.isGrace()) {
            event.setFormat(getChatFormat(ChatColor.GOLD, event.getPlayer()) + event.getMessage());
            return;
        }

        if(ingameState.getSpectators().contains(player)) {
            event.setCancelled(true);
            for(Player currentPlayer : ingameState.getSpectators())
                currentPlayer.sendMessage(getChatFormat(ChatColor.DARK_GRAY, player) + event.getMessage());
            return;
        }
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);

        if(playerRole == Role.DETECTIVE || playerRole == Role.INNOCENT) {
            event.setFormat(getChatFormat(playerRole.getChatColor(), player) + event.getMessage());
            return;
        }

        if(playerRole == Role.TRAITOR) {
            event.setCancelled(true);
            for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                Role currentRole = plugin.getRoleManager().getPlayerRole(currentPlayer);

                if(currentRole == Role.TRAITOR)
                    currentPlayer.sendMessage(getChatFormat(currentRole.getChatColor(), currentPlayer) + event.getMessage());
                else
                    currentPlayer.sendMessage(getChatFormat(Role.INNOCENT.getChatColor(), currentPlayer) + event.getMessage());
            }
        }
    }

    private String getChatFormat(ChatColor playerColor, Player player) {
        return "§7[" + playerColor + player.getName() + "§7] §8>> §7";
    }
}
