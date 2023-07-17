package net.atrophygames.obscrts.role;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PointManager {


    private HashMap<String, Integer> playerPoints;

    public PointManager() {
        playerPoints = new HashMap<>();
    }

    public void setPlayerPoints(Player player, int points) {
        playerPoints.put(player.getName(), points);
    }

    public int getPlayerPoints(Player player) {
        return playerPoints.get(player.getName());
    }

    public void addPoints(Player player, int points) {
        if(playerPoints.containsKey(player.getName()))
            playerPoints.put(player.getName(), playerPoints.get(player.getName()) + points);
    }

    public boolean removePoints(Player player, int points) {
        if(!(playerPoints.containsKey(player.getName()))) return false;
        if(playerPoints.get(player.getName()) >= points) {
            playerPoints.put(player.getName(), playerPoints.get(player.getName()) - points);
            return true;
        }
        return false;
    }
}
