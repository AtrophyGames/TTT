package net.atrophygames.obscrts.voting;

import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Voting {


    private TTT plugin;
    private ArrayList<Map> maps;
    @Getter
    private Map[] votingMaps;
    @Getter
    private int[] votingInventoryOrder = new int[] {2, 4, 6};
    @Getter
    private HashMap<String, Integer> playerVotes;
    @Getter
    private Inventory votingInventory;

    public static final int MAP_AMOUNT = 3;
    public static final String VOTING_INVENTORY_TITLE = "§6Stimme für deine Karte:";

    public Voting(TTT plugin, ArrayList<Map> maps) {
        this.plugin = plugin;
        this.maps = maps;
        votingMaps = new Map[MAP_AMOUNT];
        playerVotes = new HashMap<>();

        selectVotingMaps();
        initVotingInventory();
    }

    private void selectVotingMaps() {
        for(int i = 0; i < votingMaps.length; i++) {
            Collections.shuffle(maps);
            votingMaps[i] = maps.remove(1);
            new WorldCreator(votingMaps[i].getName().toLowerCase()).createWorld();
        }
    }

    public void initVotingInventory() {
        votingInventory = Bukkit.createInventory(null, 9, VOTING_INVENTORY_TITLE);
        for(int i = 0; i < votingMaps.length; i++) {
            Map currentMap = votingMaps[i];
            votingInventory.setItem(votingInventoryOrder[i], new ItemBuilder(Material.PAPER)
                            .setDisplayName(currentMap.convertMapName(currentMap.getName()))
                            .setLore(
                                    " ",
                                    "§7Erbauer: §e" + currentMap.getBuilder(),
                                    "§7Aktuelle Stimmen: §a" + String.valueOf(currentMap.getVotes()))
                            .build());
        }
    }

    public Map getWinnerMap() {
        Map winnerMap = votingMaps[0];

        for(int i = 1; i < votingMaps.length; i++) {
            if(votingMaps[i].getVotes() > winnerMap.getVotes())
                winnerMap = votingMaps[i];
        }
        return winnerMap;
    }

    public void vote(Player player, int votingMap) {
        if (!playerVotes.containsKey(player.getName())) {
            votingMaps[votingMap].addVote();
            player.closeInventory();
            player.sendMessage(TTT.PREFIX + "§aDu hast für die Karte §e" + votingMaps[votingMap].convertMapName(votingMaps[votingMap].getName()) + " §aabgestimmt!");
            playerVotes.put(player.getName(), votingMap);
            initVotingInventory();
        } else player.sendMessage(TTT.PREFIX + "§cDu hast bereits abgestimmt!");
    }
}
