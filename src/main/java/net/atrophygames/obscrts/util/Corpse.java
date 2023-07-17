package net.atrophygames.obscrts.util;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.role.Role;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Corpse implements Listener {


    private static final int TIME_TO_IDENTIFY = 30;

    private TTT plugin;
    private Player player,
                   killer;
    private Role role;
    private Zombie corpse;
    private boolean inProgress, isFinished;

    public Corpse(TTT plugin, Player player, Player killer, Location location) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.player = player;
        this.killer = killer;
        role = plugin.getRoleManager().getPlayerRole(player);
        inProgress = false;
        isFinished = false;

        spawnCorpse(location);
    }

    private void spawnCorpse(Location location) {
        location.setPitch(0);
        corpse = location.getWorld().spawn(location, Zombie.class);
        corpse.setCustomName("§7???");
        corpse.setCustomNameVisible(true);
        corpse.getEquipment().clear();
        corpse.setRemoveWhenFarAway(false);
        corpse.setBaby(false);
        if(corpse.isVillager()) corpse.setVillager(false);
        corpse.setCanPickupItems(false);
        corpse.getEquipment().setHelmet(getQuestionSkull());

        LivingEntity entity = corpse;
        entity.setAI(false);
        entity.setSilent(true);
        entity.setInvulnerable(true);
    }

    @EventHandler
    public void identifyCorpse(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Zombie)) return;
        if(plugin.getRoleManager().getPlayerRole(event.getPlayer()) != Role.DETECTIVE) {
            event.getPlayer().sendMessage(TTT.PREFIX + "§cNur ein Detektiv kann eine Leiche identifizieren");
            return;
        }
        if(inProgress && !isFinished) {
            event.getPlayer().sendMessage(TTT.PREFIX + "§7Die Spurensicherung arbeitet noch...");
            return;
        }
        inProgress = true;
        corpse.setCustomName(player.getDisplayName() + " §7(§8???§7)");
        corpse.getEquipment().setHelmet(getPlayerSkull());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                event.getPlayer().sendMessage(TTT.PREFIX + "§7Die Ermittlungen über §a" +
                        player.getDisplayName() + " §7sind abgeschlossen");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                corpse.setCustomName(player.getDisplayName() + " §7(" + role.getChatColor() + role.getName() + "§7)");
                isFinished = true;
            }
        }, 20 * TIME_TO_IDENTIFY);
    }

    private ItemStack getPlayerSkull() {
        return SkullCreator.itemFromUuid(player.getUniqueId());
    }

    private ItemStack getQuestionSkull() {
        return SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=");
    }
}
