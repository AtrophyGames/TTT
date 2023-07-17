package net.atrophygames.obscrts.commands;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.inventory.RoleInventory;
import net.atrophygames.obscrts.role.Role;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {


    private TTT plugin;

    public ShopCommand(TTT plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return false;
        if(plugin.getRoleManager().getPlayerRole(player) != Role.INNOCENT) {
            switch(plugin.getRoleManager().getPlayerRole(player)) {
                case DETECTIVE:
                    player.openInventory(plugin.getRoleInventory().getDetectiveShop());
                    break;
                case TRAITOR:
                    player.openInventory(plugin.getRoleInventory().getTraitorShop());
                    break;
            }
        }
        else player.sendMessage(TTT.PREFIX + "§cDu hast keinen Shop!");
        return true;
    }
}
