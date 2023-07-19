package net.atrophygames.obscrts.commands;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.LobbyState;
import net.atrophygames.obscrts.util.ConfigLocationUtil;
import net.atrophygames.obscrts.util.TesterSetup;
import net.atrophygames.obscrts.voting.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {


    private TTT plugin;

    public SetupCommand(TTT plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if(player.hasPermission("ttt.setup")) {
            if(args.length == 0) {
                player.sendMessage("§c/setup <lobby|create|set|tester|news>");
                return true;
            } else {
                if(args[0].equalsIgnoreCase("lobby")) {
                    new ConfigLocationUtil(plugin, player.getLocation(), "lobby").saveLocation();
                    player.sendMessage(TTT.PREFIX + "§aDie Lobby wurde gesetzt!");
                }
                else if(args[0].equalsIgnoreCase("create")) {
                    if(args.length == 3) {
                        Map map = new Map(plugin, args[1]);
                        if(!map.exists()) {
                            map.create(args[2]);
                            player.sendMessage(TTT.PREFIX + "§aDie Map §e" + map.getName() +
                                    " §avon §e" + map.getBuilder() + " §awurde erstellt!");
                        } else {
                            player.sendMessage(TTT.PREFIX + "§cDiese Map existiert bereits!");
                        }
                    } else {
                        player.sendMessage(TTT.PREFIX + "§cBenutze /setup create <map_name> <builder>!");
                    }
                }
                else if(args[0].equalsIgnoreCase("set")) {
                    if(args.length == 3) {
                        Map map = new Map(plugin, args[1]);
                        if(map.exists()) {
                            try {
                                int spawnID = Integer.parseInt(args[2]);
                                if(spawnID > 0 && spawnID <= LobbyState.MAX_PLAYERS) {
                                    map.setSpawnLocation(spawnID, player.getLocation());
                                    player.sendMessage(TTT.PREFIX + "§aSpawn §e#" + spawnID + " §afür die Map §e" +
                                            map.getName() + " §agesetzt!");
                                } else {
                                    player.sendMessage(TTT.PREFIX + "§cBenutze eine Zahl zwischen <1-" + LobbyState.MAX_PLAYERS +">!");
                                }
                            } catch(NumberFormatException exception) {
                                if(args[2].equalsIgnoreCase("spectator")) {
                                    map.setSpectatorSpawnLocation(player.getLocation());
                                    player.sendMessage(TTT.PREFIX + "§aSpectator-Spawn für die Map §e" +
                                            map.getName() + " §agesetzt!");
                                } else {
                                    player.sendMessage(TTT.PREFIX + "§cBenutze /setup set <map_name> <1-" + LobbyState.MAX_PLAYERS + "> || <spectator>!");
                                }
                            }
                        } else {
                            player.sendMessage(TTT.PREFIX + "§cDiese Map existiert nicht!");
                        }
                    } else {
                        player.sendMessage(TTT.PREFIX + "§cBenutze /setup set <map_name> <1-" + LobbyState.MAX_PLAYERS + "> || <spectator>!");
                    }
                }
                else if(args[0].equalsIgnoreCase("tester")) {
                    if(args.length == 2) {
                        Map map = new Map(plugin, args[1]);
                        if(map.exists())
                            new TesterSetup(plugin, map, player);
                        else player.sendMessage(TTT.PREFIX + "§cDiese Map existiert nicht!");
                    } else {
                        player.sendMessage(TTT.PREFIX + "§cBenutze /setup tester <map_name>");
                    }
                }
                else if(args[0].equalsIgnoreCase("news")) {
                    new ConfigLocationUtil(plugin, player.getLocation(), "lobby.news").saveBlockLocation();
                    player.sendMessage(TTT.PREFIX + "§aDie Neuigkeiten wurden gesetzt!");
                }
            }
        }
        return true;
    }
}
