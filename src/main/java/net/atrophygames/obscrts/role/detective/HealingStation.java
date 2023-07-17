package net.atrophygames.obscrts.role.detective;

import net.atrophygames.obscrts.TTT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class HealingStation {


    private static final int
            HEALING_DELAY = 3,
            HEALING_RADIUS = 5;

    private static final double HEALING_POWER = 2.5;

    private TTT plugin;
    private int taskID, duration;
    private Entity dummyEntity;
    private Location location;


    public HealingStation(TTT plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
        duration = 4;
        createStation();
    }

    private void createStation() {
        location.getBlock().setType(Material.BEACON);
        dummyEntity = location.getWorld().spawnEntity(location, EntityType.ARROW);

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                duration--;
                dummyEntity.remove();
                dummyEntity = location.getWorld().spawnEntity(location, EntityType.ARROW);
                for(Entity currentEntity : dummyEntity.getNearbyEntities(HEALING_RADIUS, HEALING_RADIUS, HEALING_RADIUS)) {
                    if(currentEntity instanceof Player) {
                        Player player = (Player) currentEntity;
                        if(player.getHealth() <= (20 - HEALING_POWER)) {
                            player.setHealth(player.getHealth() + HEALING_POWER);
                        }
                        player.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    }
                }
                if(duration <= 0)
                    destroyStation();
            }
        }, 0, 20 * HEALING_DELAY);
    }

    private void destroyStation() {
        dummyEntity.remove();
        location.getBlock().setType(Material.AIR);
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
