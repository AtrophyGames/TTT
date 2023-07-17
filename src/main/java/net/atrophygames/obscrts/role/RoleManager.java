package net.atrophygames.obscrts.role;

import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RoleManager {


    private TTT plugin;
    private HashMap<String, Role> playerRoles;
    private ArrayList<Player> players;
    @Getter
    private ArrayList<String> traitorPlayers;

    public RoleManager(TTT plugin) {
        this.plugin = plugin;
        playerRoles = new HashMap<>();
        traitorPlayers = new ArrayList<>();
        players = plugin.getPlayers();
    }

    public void calculateRoles() {
        int playerSize = plugin.getPlayers().size();
        int traitors = (int) Math.round(Math.log(playerSize) * 1.5);
        int detectives = (int) Math.round(Math.log(playerSize) * 0.74);
        int innocents = playerSize - (traitors + detectives);

        Collections.shuffle(players);

        int counter = 0;
        for(int i = counter; i < traitors; i++) {
            playerRoles.put(players.get(i).getName(), Role.TRAITOR);
            traitorPlayers.add(players.get(i).getName());
        }
        counter += traitors;

        for(int i = counter; i < detectives + counter; i++)
            playerRoles.put(players.get(i).getName(), Role.DETECTIVE);
        counter += detectives;

        for(int i = counter; i < innocents + counter; i++)
            playerRoles.put(players.get(i).getName(), Role.INNOCENT);

        for(Player currentPlayer : players) {
            switch(getPlayerRole(currentPlayer)) {
                case TRAITOR:
                    setArmor(currentPlayer, Color.RED);
                    currentPlayer.getInventory().setItem(8, plugin.getRoleInventory().getTraitorItem());
                    break;
                case DETECTIVE:
                    setArmor(currentPlayer, Color.BLUE);
                    currentPlayer.getInventory().setItem(8, plugin.getRoleInventory().getDetectiveItem());
                    break;
                case INNOCENT:
                    setArmor(currentPlayer, Color.GRAY);
                    break;
            }
        }
    }

    public void setArmor(Player player, Color color) {
        player.getInventory().setChestplate(getColoredChestplate(color));
    }

    private ItemStack getColoredChestplate(Color color) {
        ItemStack itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(color);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public Role getPlayerRole(Player player) {
        return playerRoles.get(player.getName());
    }
}
