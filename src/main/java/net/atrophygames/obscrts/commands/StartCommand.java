package net.atrophygames.obscrts.commands;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.LobbyState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {


    private TTT plugin;

    private static final int START_SECONDS = 10;

    public StartCommand(TTT plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if(!player.hasPermission("ttt.start")) return false;
        if(args.length == 0) {
            if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
                if(lobbyState.getLobbyCountdown().isRunning() &&
                        lobbyState.getLobbyCountdown().getSeconds() > START_SECONDS) {
                    lobbyState.getLobbyCountdown().setSeconds(START_SECONDS);
                    player.sendMessage(TTT.PREFIX + "§aDas Spiel startet jetzt schneller!");
                } else {
                    player.sendMessage(TTT.PREFIX + "§cDer Start des Spiels kann nicht weiter beschleunigt werden!");
                }
            }
        }
        return true;
    }
}
