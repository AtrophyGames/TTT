package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChestListener implements Listener {

    private final TTT plugin;
    private final ItemStack woodenSword;

    private final ItemStack stoneSword;
    private final ItemStack ironSword;
    private final ItemStack bow;
    private final ItemStack arrows;

    public ChestListener(TTT plugin) {
        this.plugin = plugin;
        woodenSword = new ItemStack(Material.WOODEN_SWORD);
        stoneSword = new ItemStack(Material.STONE_SWORD);
        ironSword = new ItemStack(Material.IRON_SWORD);
        bow = new ItemStack(Material.BOW);
        arrows = new ItemStack(Material.ARROW, 32);
    }

    @EventHandler
    public void onPlayerInteractWithChest(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock().getType() != Material.CHEST) return;
        event.setCancelled(true);

        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = event.getPlayer();
        if(!player.getInventory().contains(woodenSword)) {
            openChest(woodenSword, event.getClickedBlock(), player);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
        } else if(!player.getInventory().contains(bow)) {
            openChest(bow, event.getClickedBlock(), player);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            player.getInventory().addItem(arrows);
        } else if(!player.getInventory().contains(stoneSword)) {
            openChest(stoneSword, event.getClickedBlock(), player);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
        }
        player.updateInventory();
    }

    @EventHandler
    public void onPlayerInteractWithEnderChest(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock().getType() != Material.ENDER_CHEST) return;
        event.setCancelled(true);

        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        Player player = event.getPlayer();

        if(!ingameState.isGrace()) {
            openChest(ironSword, event.getClickedBlock(), player);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        }
        else
            player.sendMessage(TTT.PREFIX + "§cDiese Kiste kann erst nach der Schutzzeit geöffnet werden!");
    }

    private void openChest(ItemStack itemStack, Block block, Player player) {
        player.getInventory().addItem(itemStack);
        block.setType(Material.AIR);
    }
}
