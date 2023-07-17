package net.atrophygames.obscrts.util;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.voting.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class TesterSetup implements Listener {


    private TTT plugin;
    private Player player;
    private Map map;
    private int phase;

    private Block[] borderBlocks, lamps;
    private Block button;
    private Location testerLocation;
    private boolean isFinished;

    public TesterSetup(TTT plugin, Map map, Player player) {
        this.plugin = plugin;
        this.map = map;
        this.player = player;
        phase = 1;
        isFinished = false;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        borderBlocks = new Block[3];
        lamps = new Block[2];
        startSetup();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!(event.getPlayer().getName().equals(player.getName()))) return;
        if(isFinished) return;
        switch(phase) {
            case 1: case 2: case 3:
                borderBlocks[phase - 1] = event.getBlock();
                player.sendMessage(TTT.PREFIX + "§aBegrenzungsblock §e#" + phase + " §aerfolgreich gesetzt");
                phase++;
                startPhase(phase);
                break;
            case 4: case 5:
                if(event.getBlock().getType() == Material.WHITE_STAINED_GLASS) {
                    lamps[phase - 4] = event.getBlock();
                    player.sendMessage(TTT.PREFIX + "§aLampe §e#" + phase + " §aerfolgreich gesetzt");
                    phase++;
                    startPhase(phase);
                } else
                    player.sendMessage(TTT.PREFIX + "§cDie Lampe muss aus Glas sein!");
                break;
            case 6:
                if(event.getBlock().getType() == Material.STONE_BUTTON || event.getBlock().getType() == Material.LEGACY_WOOD_BUTTON) {
                    button = event.getBlock();
                    player.sendMessage(TTT.PREFIX + "§aTester-Knopf erfolgreich gesetzt");
                    phase++;
                    startPhase(phase);
                } else
                    player.sendMessage(TTT.PREFIX + "§cDas ist kein Knopf!");
                break;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if(!(event.getPlayer().getName().equals(player.getName()))) return;
        if(isFinished) return;
        if(phase == 7) {
            testerLocation = player.getLocation();
            player.sendMessage(TTT.PREFIX + "§aTester-Location erfolgreich gesetzt");
            finishSetup();
        }
    }

    public void finishSetup() {
        player.sendMessage(TTT.PREFIX + "§aTester-Setup abgeschlossen!");
        isFinished = true;

        for(int i = 0; i < borderBlocks.length; i++)
            new ConfigLocationUtil(
                    plugin,
                    borderBlocks[i].getLocation(),
                    "maps." + map.getName() + ".tester.border_blocks." + i).saveBlockLocation();

        for(int i = 0; i < lamps.length; i++)
            new ConfigLocationUtil(
                    plugin,
                    lamps[i].getLocation(),
                    "maps." + map.getName() + ".tester.lamps." + i).saveBlockLocation();

        new ConfigLocationUtil(plugin, button.getLocation(), "maps." + map.getName() + ".tester.button")
                .saveBlockLocation();
        new ConfigLocationUtil(plugin, testerLocation, "maps." + map.getName() + ".tester.location")
                .saveLocation();
    }

    public void startPhase(int phase) {
        switch(phase) {
            case 1: case 2: case 3:
                player.sendMessage(TTT.PREFIX + "Bitte klicke einen Begrenzungsblöcke an");
                break;
            case 4: case 5:
                player.sendMessage(TTT.PREFIX + "Bitte klicke eine Lampe an");
                break;
            case 6:
                player.sendMessage(TTT.PREFIX + "Bitte klicke den Tester-Knopf an");
                break;
            case 7:
                player.sendMessage(TTT.PREFIX + "Bitte schleiche an der Tester-Location");
                break;
        }
    }

    public void startSetup() {
        player.sendMessage(TTT.PREFIX + "Du hast das Tester-Setup gestartet");
        startPhase(phase);
    }
}
