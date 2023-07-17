package net.atrophygames.obscrts.voting;

import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.LobbyState;
import net.atrophygames.obscrts.role.detective.Tester;
import net.atrophygames.obscrts.util.ConfigLocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class Map {


    private TTT plugin;
    @Getter
    private String name;
    @Getter
    private String builder;
    @Getter
    private Location[] spawnLocations = new Location[LobbyState.MAX_PLAYERS];
    @Getter
    private Location spectatorSpawnLocation;
    @Getter
    private int votes;
    @Getter
    private Tester tester;

    public Map(TTT plugin, String name) {
        this.plugin = plugin;
        this.name = name.toUpperCase();
        this.plugin.getMaps().add(this);
        this.tester = new Tester(plugin, this);

        if(exists()) builder = plugin.getConfig().getString("maps." + name + ".builder");
    }

    public void create(String builder) {
        this.builder = builder;
        plugin.getConfig().set("maps." + name + ".builder", builder);
        plugin.saveConfig();
    }

    public void load() {
        for(int i = 0; i < spawnLocations.length; i++) {
            spawnLocations[i] = new ConfigLocationUtil(plugin, "maps." + name + "." + (i + 1)).loadLocation();
        }
        spectatorSpawnLocation = new ConfigLocationUtil(plugin, "maps." + name + ".spectator").loadLocation();

        if(tester.exists()) tester.load();
    }

    public void setSpawnLocation(int locationID, Location location) {
        spawnLocations[locationID - 1] = location;
        new ConfigLocationUtil(plugin, location, "maps." + name + "." + locationID).saveLocation();
    }

    public void setSpectatorSpawnLocation(Location location) {
        spectatorSpawnLocation = location;
        new ConfigLocationUtil(plugin, location, "maps." + name + ".spectator").saveLocation();
    }

    public void addVote() {
        votes++;
    }

    public void removeVote() {
        votes--;
    }

    public boolean exists() {
        return (plugin.getConfig().getString("maps." + name + ".builder") != null);
    }

    public boolean isPlayable() {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("maps." + name);
        if(!configurationSection.contains("spectator")) return false;
        if(!configurationSection.contains("builder")) return false;

        for(int i = 0; i < LobbyState.MAX_PLAYERS; i++) {
            if(!configurationSection.contains(Integer.toString(i))) return false;
        }

        return true;
    }

    public String convertMapName(String string) {
        StringBuilder stringBuilder = new StringBuilder();

        String[] parts = string.split("_");
        for (int i = 1; i < parts.length; i++) {
            stringBuilder.append(Character.toUpperCase(parts[i].charAt(0)));
            stringBuilder.append(parts[i].substring(1).toLowerCase());
        }

        return stringBuilder.toString();
    }
}
