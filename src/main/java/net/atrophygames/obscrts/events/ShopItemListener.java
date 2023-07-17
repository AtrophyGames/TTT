package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.inventory.RoleInventory;
import net.atrophygames.obscrts.role.Role;
import net.atrophygames.obscrts.role.detective.HealingStation;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ShopItemListener implements Listener {

    private final TTT plugin;

    public ShopItemListener(TTT plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreeperArrow(ProjectileHitEvent event) {
        if(event.getEntity().getType() != EntityType.ARROW) return;
        if(!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();

        if(plugin.getRoleManager().getPlayerRole(player) != Role.TRAITOR) return;
        if(RoleInventory.removeMaterialItem(player, Material.CREEPER_SPAWN_EGG)) {
            World world = event.getEntity().getWorld();
            world.spawnEntity(event.getEntity().getLocation(), EntityType.CREEPER);
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityTargetTraitor(EntityTargetEvent event) {
        if(!(event.getTarget() instanceof Player)) return;
        if(!(event.getEntity() instanceof Creeper || (!(event.getEntity() instanceof Vex)))) return;
        Player player = (Player) event.getTarget();

        if(plugin.getRoleManager().getPlayerRole(player) == Role.TRAITOR) {
            event.setTarget(null);
        }
    }

   @EventHandler(priority = EventPriority.HIGH)
   public void onPlaceHealingStation(BlockPlaceEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = event.getPlayer();;
        if(plugin.getRoleManager().getPlayerRole(player) != Role.DETECTIVE) return;

        if(event.getBlock().getType() == Material.BEACON) {
            event.setCancelled(false);
            new HealingStation(plugin, event.getBlock().getLocation());
        }
   }
}
