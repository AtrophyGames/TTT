package net.atrophygames.obscrts.stats;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.database.MySQL;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StatsManager implements Stats {


    private TTT plugin;
    private MySQL mySQL;
    private File playerStats;

    public StatsManager(TTT plugin) {
        this.plugin = plugin;
        this.mySQL = plugin.getMySQL();

        createTables();
        createFile();
    }

    private void createFile() {
        playerStats = new File("plugins/TTT", "stats.yml");
        if (!playerStats.exists()) {
            try {
                playerStats.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPlayerToFile(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".karma", getKarmaForPlayer(player));
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".kills", getKillsForPlayer(player));
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".deaths", getDeathsForPlayer(player));
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".wins", getWinsForPlayer(player));
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".losses", getLossesForPlayer(player));
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".played_games", getPlayedGamesForPlayer(player));
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".fkq", getFKForPlayer(player));
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getKarma(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        return fileConfiguration.getInt("players." + player.getUniqueId().toString() + ".karma");
    }

    public void setKarma(Player player, int karma) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".karma", karma);
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getKills(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        return fileConfiguration.getInt("players." + player.getUniqueId().toString() + ".kills");
    }

    public void setKills(Player player, int kills) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".kills", kills);
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDeaths(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        return fileConfiguration.getInt("players." + player.getUniqueId().toString() + ".deaths");
    }

    public void setDeaths(Player player, int deaths) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".deaths", deaths);
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getWins(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        return fileConfiguration.getInt("players." + player.getUniqueId().toString() + ".wins");
    }

    public void setWins(Player player, int wins) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".wins", wins);
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLosses(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        return fileConfiguration.getInt("players." + player.getUniqueId().toString() + ".losses");
    }

    public void setLosses(Player player, int losses) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".losses", losses);
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayedGames(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        return fileConfiguration.getInt("players." + player.getUniqueId().toString() + ".played_games");
    }

    public void setPlayedGames(Player player, int playedGames) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".played_games", playedGames);
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getFK(Player player) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        return fileConfiguration.getInt("players." + player.getUniqueId().toString() + ".fkq");
    }

    public void setFK(Player player, int fkq) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        fileConfiguration.set("players." + player.getUniqueId().toString() + ".fkq", fkq);
        try {
            fileConfiguration.save(playerStats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getKarmaForPlayer(Player player) {
        String query = "SELECT karma FROM karma WHERE uuid=?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("karma");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private int getKillsForPlayer(Player player) {
        String query = "SELECT kills FROM stats WHERE uuid=?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("kills");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private int getDeathsForPlayer(Player player) {
        String query = "SELECT deaths FROM stats WHERE uuid=?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("deaths");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private int getWinsForPlayer(Player player) {
        String query = "SELECT wins FROM stats WHERE uuid=?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("wins");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private int getLossesForPlayer(Player player) {
        String query = "SELECT losses FROM stats WHERE uuid=?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("losses");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private int getPlayedGamesForPlayer(Player player) {
        String query = "SELECT played_games FROM stats WHERE uuid=?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("played_games");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private int getFKForPlayer(Player player) {
        String query = "SELECT fk FROM stats WHERE uuid=?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("fk");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public boolean isUserExistentForKarma(UUID uuid) {
        String query = "SELECT count(*) AS count FROM karma WHERE uuid=?";

        try(ResultSet rs = mySQL.query(query, uuid.toString())) {
            if(rs.next()) {
                return rs.getInt("count") != 0;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUserExistentForStats(UUID uuid) {
        String query = "SELECT count(*) AS count FROM stats WHERE uuid=?";

        try(ResultSet rs = mySQL.query(query, uuid.toString())) {
            if(rs.next()) {
                return rs.getInt("count") != 0;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initKarmaForPlayer(UUID uuid) {
        String query = "INSERT INTO karma(uuid, karma) VALUES(?,?)";

        mySQL.update(query, uuid.toString(), 0);
    }

    public void initStatsForPlayer(UUID uuid) {
        String query = "INSERT INTO stats(uuid, kills, deaths, wins, losses, played_games, fk) VALUES(?,?,?,?,?,?,?)";

        mySQL.update(query, uuid.toString(), 0, 0, 0, 0, 0, 0);
    }

    public void updateTables() {
        String karmaQuery = "INSERT INTO karma (uuid, karma) VALUES (?, ?)";
        String statsQuery = "UPDATE stats SET kills=?, deaths=?, wins=?, losses=?, played_games=?, fk=? WHERE uuid=?";

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerStats);
        ConfigurationSection playersSection = fileConfiguration.getConfigurationSection("players");

        for(String key : playersSection.getKeys(false)) {
            int karmaValue = playersSection.getInt(key + ".karma");
            int killsValue = playersSection.getInt(key + ".kills");
            int deathsValue = playersSection.getInt(key + ".deaths");
            int winsValue = playersSection.getInt(key + ".wins");
            int lossesValue = playersSection.getInt(key + ".losses");
            int playedGamesValue = playersSection.getInt(key + ".played_games");
            int fkValue = playersSection.getInt(key + ".fk");

            mySQL.update(karmaQuery, key, karmaValue);
            mySQL.update(statsQuery, killsValue, deathsValue, winsValue, lossesValue, playedGamesValue, fkValue, key);
        }
    }

    private void createTables() {
        mySQL.update("CREATE TABLE IF NOT EXISTS karma (" +
                "uuid VARCHAR(36)," +
                "karma INT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP");
        mySQL.update("CREATE TABLE IF NOT EXISTS stats (" +
                "uuid VARCHAR(36)," +
                "kills INT," +
                "deaths INT," +
                "wins INT," +
                "losses INT," +
                "played_games INT," +
                "fk INT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }
}
