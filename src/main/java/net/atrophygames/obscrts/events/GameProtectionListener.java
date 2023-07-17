package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.gamestate.states.LobbyState;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameProtectionListener implements Listener {

    private final TTT plugin;

    public GameProtectionListener(TTT plugin) {
        this.plugin = plugin;
        //initGameRules();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onStompSoil(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            event.setCancelled(true);
            return;
        }

        Material material = event.getItemDrop().getItemStack().getType();
        if(material == Material.LEATHER_CHESTPLATE || material == Material.EMERALD) {
            event.setCancelled(true);
            System.out.println("kein drop");
            return;
        }
        event.setCancelled(false);
        System.out.println("drop");
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
            return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isGrace()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
            return;
        }
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isGrace()) {
            event.setCancelled(true);
        }

        if(!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if(ingameState.getSpectators().contains(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState))
            event.setCancelled(true);

        if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            if(ingameState.isGrace())
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        Player player = event.getEntity();
        player.getInventory().clear();

        if(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
            ingameState.addSpectator(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE) event.setCancelled(true);
    }

    private void initGameRules() {
        for(World currentWorld : plugin.getServer().getWorlds() ) {
            currentWorld.setGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE.getName(), "false");
        }
    }
}
