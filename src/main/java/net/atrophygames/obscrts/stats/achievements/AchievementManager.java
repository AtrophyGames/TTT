package net.atrophygames.obscrts.stats.achievements;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.database.MySQL;

public class AchievementManager {


    private TTT plugin;
    private MySQL mySQL;

    public AchievementManager(TTT plugin) {
        this.plugin = plugin;
        this.mySQL = plugin.getMySQL();

        createTable();
    }

    private void createTable() {
        mySQL.update("CREATE TABLE IF NOT EXISTS achievements (" +
                "uuid VARCHAR(36)," +
                "achievement_01 boolean," +
                "achievement_02 boolean," +
                "achievement_03 boolean)");
    }
}
