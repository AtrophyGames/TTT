package net.atrophygames.obscrts;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.Setter;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import net.atrophygames.obscrts.commands.*;
import net.atrophygames.obscrts.database.MySQL;
import net.atrophygames.obscrts.gamestate.*;
import net.atrophygames.obscrts.inventory.RoleInventory;
import net.atrophygames.obscrts.stats.StatsManager;
import net.atrophygames.obscrts.events.*;
import net.atrophygames.obscrts.events.packets.EntityEquipmentPacketListener;
import net.atrophygames.obscrts.role.RoleManager;
import net.atrophygames.obscrts.scoreboards.WarmUpScoreboard;
import net.atrophygames.obscrts.stats.achievements.AchievementManager;
import net.atrophygames.obscrts.util.ConfigLocationUtil;
import net.atrophygames.obscrts.voting.*;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.File;
import java.util.ArrayList;

@Plugin(name = "TTT", version = "0.5.5")
@Author("Obscrts")
@Description("an adaptation of the classic TTT game mode")
@ApiVersion(ApiVersion.Target.v1_16)

@Website("atrophygames.net")
@LogPrefix("TTT")

@Dependency("ProtocolLib")
@Dependency("LuckPerms")
@Dependency("TitleAPI")
@Dependency("HolographicDisplays")
@SoftDependency("CoinAPI")

@Command(name = "setup",
        desc = "basic ttt setup command",
        usage = "/<command>",
        permission = "ttt.setup")
@Command(name = "start",
        desc = "reduces the lobby countdown",
        usage = "/<command>",
        permission = "ttt.start")
@Command(name = "shop",
        desc = "opens the player's role shop",
        usage = "/<command>")
@Command(name = "stats",
        desc = "display the player's stats of the last 30 days",
        usage = "/<command>")
@Command(name = "statsall",
        desc = "display the player's stats",
        usage = "/<command>")

@Command(name = "test",
        desc = "display the player's stats",
        usage = "/<command>")

@Getter
public class TTT extends JavaPlugin {


    public static final String PREFIX = "§7[§4TTT§7] §r";

    private GameStateManager gameStateManager;
    private ArrayList<Player> players;
    private ArrayList<Map> maps;
    private Voting voting;
    private RoleManager roleManager;
    private RoleInventory roleInventory;
    private ProtocolManager protocolManager;
    private MySQL mySQL;
    private StatsManager statsManager;
    private AchievementManager achievementManager;

    private HolographicDisplaysAPI holographicDisplaysAPI;
    private LuckPerms luckPerms;

    @Setter
    private WarmUpScoreboard warmUpScoreboard;

    @Override
    public void onEnable() {
        loadDatabaseConfig();

        luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        protocolManager = ProtocolLibrary.getProtocolManager();

        statsManager = new StatsManager(this);
        achievementManager = new AchievementManager(this);

        holographicDisplaysAPI  = HolographicDisplaysAPI.get(this);

        gameStateManager = new GameStateManager(this);
        gameStateManager.setGameState(GameState.LOBBY_STATE);

        players = new ArrayList<>();

        initVoting();
        displayUpdateNews();

        roleManager = new RoleManager(this);
        roleInventory = new RoleInventory(this);

        registerEvents(getServer().getPluginManager());
        registerCommands();
    }

    @Override
    public void onDisable() {}

    private void loadDatabaseConfig() {
        File configFile = new File(getDataFolder(), "database.yml");

        if (!configFile.exists()) saveResource("database.yml", false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        String mysqlUrl = config.getString("mysql.url");
        int mysqlPort = config.getInt("mysql.port");
        String mysqlDatabase = config.getString("mysql.database");
        String mysqlUser = config.getString("mysql.user");
        String mysqlPassword = config.getString("mysql.password");

        this.mySQL = MySQL.newBuilder()
                .withUrl(mysqlUrl)
                .withPort(mysqlPort)
                .withDatabase(mysqlDatabase)
                .withUser(mysqlUser)
                .withPassword(mysqlPassword)
                .create();
    }

    private void initVoting() {
        maps = new ArrayList<>();
        for (String mapName : this.getConfig().getConfigurationSection("maps").getKeys(false)) {
            Map map = new Map(this, mapName);
            if (map.isPlayable()) {
                maps.add(map);
            }
        }
        if(maps.size() >= Voting.MAP_AMOUNT) voting = new Voting(this, maps);
        else voting = null;
    }

    private void displayUpdateNews() {
        ConfigLocationUtil configLocationUtil = new ConfigLocationUtil(this, "lobby.news");
        if(configLocationUtil.loadBlockLocation() != null) {
            File news = new File("plugins/TTT", "news.yml");
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(news);

            if(!fileConfiguration.getBoolean("displayNews")) return;

            Hologram hologramTitle = holographicDisplaysAPI.createHologram(
                    configLocationUtil.loadLocation().add(0, 2.6, 0));
            hologramTitle.getLines().appendText(PREFIX + "§6Update");
            VisibilitySettings visibilitySettingsTitle = hologramTitle.getVisibilitySettings();
            visibilitySettingsTitle.setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);

            for(String key : fileConfiguration.getConfigurationSection("news").getKeys(true)) {
                String line = fileConfiguration.getString("news." + key);

                Hologram hologram = holographicDisplaysAPI.createHologram(
                        configLocationUtil.loadLocation().add(0, 1 + (.33 * Integer.parseInt(key) + .45), 0));
                hologram.getLines().appendText(line);
                VisibilitySettings visibilitySettings = hologram.getVisibilitySettings();
                visibilitySettings.setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
            }
        }
    }

    private void registerEvents(PluginManager pluginManager) {
        //protocolManager.addPacketListener(new EntityEquipmentPacketListener(this));

        pluginManager.registerEvents(new PlayerConnectLobbyListener(this), this);
        pluginManager.registerEvents(new VotingListener(this), this);
        pluginManager.registerEvents(new GameProgressListener(this), this);
        pluginManager.registerEvents(new GameProtectionListener(this), this);
        pluginManager.registerEvents(new ChatListener(this), this);
        pluginManager.registerEvents(new ChestListener(this), this);
        pluginManager.registerEvents(new TesterListener(this), this);
        pluginManager.registerEvents(new ShopItemListener(this), this);
        pluginManager.registerEvents(new WarmUpListener(this), this);
        pluginManager.registerEvents(roleInventory, this);
    }

    private void registerCommands() {
        getCommand("setup").setExecutor(new SetupCommand(this));
        getCommand("start").setExecutor(new StartCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));

        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("stats").setTabCompleter(new StatsCommand(this));
        getCommand("statsall").setExecutor(new StatsCommand(this));
        getCommand("statsall").setTabCompleter(new StatsCommand(this));

        getCommand("test").setExecutor(new TestCommand(this));
    }
}
