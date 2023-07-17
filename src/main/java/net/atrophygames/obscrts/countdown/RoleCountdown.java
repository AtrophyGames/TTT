package net.atrophygames.obscrts.countdown;

import com.connorlinfoot.titleapi.TitleAPI;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.role.Role;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class RoleCountdown extends Countdown {


    private TTT plugin;
    private int seconds = 30;

    public RoleCountdown(TTT plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                switch(seconds) {
                    case 30:
                        Bukkit.broadcastMessage(TTT.PREFIX + "Die Rollen werden in " + seconds + " Sekunden enthüllt!");
                    break;
                    case 15: case 10: case 5: case 4: case 3: case 2: case 1:
                        for(Player currentPlayer : plugin.getPlayers())
                            currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2.75f);;
                        break;
                    case 0:
                        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
                        ingameState.setGrace(false);
                        plugin.getRoleManager().calculateRoles();

                        ArrayList<String> traitorPlayers = plugin.getRoleManager().getTraitorPlayers();
                        for(Player currentPlayer : plugin.getPlayers()) {
                            Role playerRole = plugin.getRoleManager().getPlayerRole(currentPlayer);
                            currentPlayer.sendMessage(TTT.PREFIX + "§7Du bist ein: " +
                                    playerRole.getChatColor() + playerRole.getName());

                            currentPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    new TextComponent(playerRole.getChatColor() + playerRole.getName()));

                            switch(playerRole) {
                                case TRAITOR:
                                    TitleAPI.sendTitle(currentPlayer,
                                            playerRole.getChatColor() + playerRole.getName(),
                                            "Eliminiere unauffällig alle Unschuldigen und Detektive",
                                            10, 50, 10);
                                    break;
                                case DETECTIVE:
                                    TitleAPI.sendTitle(currentPlayer,
                                            playerRole.getChatColor() + playerRole.getName(),
                                            "Enttarne alle Verräter",
                                            10, 50, 10);
                                    break;
                                case INNOCENT:
                                    TitleAPI.sendTitle(currentPlayer,
                                            playerRole.getChatColor() + playerRole.getName(),
                                            "Hilfe den Detektiven die Verräter zu entlarven",
                                            10, 50, 10);
                                    break;
                            }

                            if(playerRole == Role.TRAITOR) currentPlayer.sendMessage(TTT.PREFIX + "§7Die Verräter sind: " +
                                    playerRole.getChatColor() + String.join(", ", traitorPlayers));
                            currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);;
                            ((IngameState) plugin.getGameStateManager().getCurrentGameState()).getIngameScoreboard()
                                    .setScoreboard(currentPlayer);
                        }
                        stop();
                        break;
                }
                for(Player currentPlayer : plugin.getPlayers())
                    plugin.getWarmUpScoreboard().updateScoreboard(currentPlayer, seconds);
                seconds--;
            }
        }, 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        ((IngameState) plugin.getGameStateManager().getCurrentGameState()).getGameEndCountdown().start();
    }
}
