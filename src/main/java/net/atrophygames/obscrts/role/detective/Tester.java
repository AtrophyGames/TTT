package net.atrophygames.obscrts.role.detective;

import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.inventory.RoleInventory;
import net.atrophygames.obscrts.role.Role;
import net.atrophygames.obscrts.util.ConfigLocationUtil;
import net.atrophygames.obscrts.voting.Map;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Tester {


    private static final int TESTING_TIME = 5;

    private TTT plugin;
    private Map map;
    private Block[] borderBlocks, lamps;
    @Getter
    private Block testerButton;
    @Getter
    private Block trapButton;
    private Location testerLocation;
    private World world;
    private boolean inUse;
    private int seconds;

    public Tester(TTT plugin, Map map) {
        this.plugin = plugin;
        this.map = map;
        borderBlocks = new Block[3];
        lamps = new Block[2];
    }

    public void test(Player player) {
        seconds = TESTING_TIME;
        Role playerRole = plugin.getRoleManager().getPlayerRole(player);
        if(inUse) return;
        if(playerRole == Role.DETECTIVE) {
            player.sendMessage(TTT.PREFIX + "§cAls Detektiv kannst du den Tester nicht nutzen!");
            return;
        }

        if(playerRole == Role.TRAITOR) {
            if(RoleInventory.removeMaterialItem(player, Material.PAPER)) {
                player.sendMessage(TTT.PREFIX + "§7Du hast eine Chance von §e70% §7als Unschuldiger erkannt zu werden");
                if(Math.random() <= 0.7)
                    playerRole = Role.INNOCENT;
            }
        }

        player.teleport(testerButton.getLocation());
        inUse = true;
        for(Block currentBlock : borderBlocks) world.getBlockAt(currentBlock.getLocation()).setType(Material.GLASS);
        for(Player currentPlayer : Bukkit.getOnlinePlayers())
            currentPlayer.playSound(borderBlocks[1].getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1);
        Bukkit.broadcastMessage(TTT.PREFIX + player.getName() + " hat den Tester betreten");
        for(Entity currentEntity : player.getNearbyEntities(4, 4, 4)) {
            if(currentEntity instanceof Player) {
                currentEntity.teleport(testerLocation);
            }
        }

        Role endRole = playerRole;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {endTesting(endRole);}
        }, 20 * TESTING_TIME);
    }

    private void endTesting(Role playerRole) {
        for(Block currentBlock : lamps) {
            if(playerRole != Role.TRAITOR)
                world.getBlockAt(currentBlock.getLocation()).setType(Material.LIME_STAINED_GLASS);
            else world.getBlockAt(currentBlock.getLocation()).setType(Material.RED_STAINED_GLASS);
        };
        for(Block currentBlock : borderBlocks) world.getBlockAt(currentBlock.getLocation()).setType(Material.AIR);
        for(Player currentPlayer : Bukkit.getOnlinePlayers())
            currentPlayer.playSound(borderBlocks[1].getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, 1);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                resetTester();
            }
        }, 20 * TESTING_TIME);
    }

    public void load() {
        for(int i = 0; i < borderBlocks.length; i++) {
            borderBlocks[i] = new ConfigLocationUtil(
                    plugin,
                    "maps." + map.getName() + ".tester.border_blocks." + i).loadBlockLocation();
        }

        for(int i = 0; i < lamps.length; i++) {
            lamps[i] = new ConfigLocationUtil(
                    plugin,
                    "maps." + map.getName() + ".tester.lamps." + i).loadBlockLocation();
        }

        testerButton = new ConfigLocationUtil(plugin, "maps." + map.getName() + ".tester.button").loadBlockLocation();
        trapButton = new ConfigLocationUtil(plugin, "maps." + map.getName() + ".tester.trap_button").loadBlockLocation();
        testerLocation = new ConfigLocationUtil(plugin, "maps." + map.getName() + ".tester.location").loadLocation();

        world = map.getSpectatorSpawnLocation().getWorld();
        resetTester();
    }

    private void resetTester() {
        inUse = false;
        for(Block currentBlock : borderBlocks) world.getBlockAt(currentBlock.getLocation()).setType(Material.AIR);
        for(Block currentBlock : lamps) world.getBlockAt(currentBlock.getLocation()).setType(Material.WHITE_STAINED_GLASS);
    }

    public boolean exists() {
        return plugin.getConfig().getString("maps." + map.getName() + ".tester.location.world") != null;
    }
}
