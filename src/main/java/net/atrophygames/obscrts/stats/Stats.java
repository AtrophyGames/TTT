package net.atrophygames.obscrts.stats;

import org.bukkit.entity.Player;

public interface Stats {


    public int getKarma(Player player);
    public void setKarma(Player player, int karma);

    public int getKills(Player player);
    public void setKills(Player player, int kills);

    public int getDeaths(Player player);
    public void setDeaths(Player player, int deaths);

    public int getWins(Player player);
    public void setWins(Player player, int wins);

    public int getLosses(Player player);
    public void setLosses(Player player, int losses);

    public int getPlayedGames(Player player);
    public void setPlayedGames(Player player, int player_games);

    public int getFK(Player player);
    public void setFK(Player player, int fkq);
}
