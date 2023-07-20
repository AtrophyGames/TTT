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

        createTable();
    }

    public int getKarma(Player player) {
        String query = "SELECT uuid, SUM(karma) as karma FROM stats WHERE uuid = ?";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("karma");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public int getKarmaLast30Days(Player player) {
        String query = "SELECT uuid, SUM(karma) as karma FROM " +
                "(SELECT * FROM stats WHERE timestamp >= NOW() - INTERVAL 30 day AND uuid =?)" +
                "as karma";

        try(ResultSet resultSet = mySQL.query(query, player.getUniqueId().toString())) {
            if(resultSet.next()) {
                return resultSet.getInt("karma");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public void addKarma(Player player, int karma) {}

    public int getKills(Player player) {
        return 0;
    }

    public void addKill(Player player) {}

    public int getDeaths(Player player) {
        return 0;
    }

    public void addDeath(Player player) {}

    public int getWins(Player player) {
        return 0;
    }

    public void addWin(Player player) {}

    public int getLosses(Player player) {
        return 0;
    }

    public void addLoss(Player player) {}

    public void setLosses(Player player, int losses) {}

    public int getPlayedGames(Player player) {
        return 0;
    }

    public void addPlayedGame(Player player) {}

    public int getFK(Player player) {
        return 0;
    }

    public void addFK(Player player) {}

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

    public void initStatsForPlayer(UUID uuid) {
        String query = "INSERT INTO stats(uuid, karma, kills, deaths, wins, losses, played_games, fk) VALUES(?,?,?,?,?,?,?,?)";

        mySQL.update(query, uuid.toString(), 0, 0, 0, 0, 0, 0, 0);
    }

    public void updateTables() {}

    private void createTable() {
        mySQL.update("CREATE TABLE IF NOT EXISTS stats (" +
                "uuid VARCHAR(36)," +
                "karma INT," +
                "kills INT," +
                "deaths INT," +
                "wins INT," +
                "losses INT," +
                "played_games INT," +
                "fk INT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }
}
