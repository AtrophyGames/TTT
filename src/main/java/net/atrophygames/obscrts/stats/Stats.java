package net.atrophygames.obscrts.stats;

import org.bukkit.entity.Player;

public interface Stats {


    public int getKarma(Player player);
    public void addKarma(Player player, int karma);

    public int getKills(Player player);
    public void addKill(Player player);

    public int getDeaths(Player player);
    public void addDeath(Player player);

    public int getWins(Player player);
    public void addWin(Player player);

    public int getLosses(Player player);
    public void addLoss(Player player);

    public int getPlayedGames(Player player);
    public void addPlayedGame(Player player);

    public int getFK(Player player);
    public void addFK(Player player);
}
