package net.atrophygames.obscrts.events;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.api.CoinAPI;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.stats.StatsManager;
import net.atrophygames.obscrts.role.Role;
import net.atrophygames.obscrts.role.RoleManager;
import net.atrophygames.obscrts.util.Corpse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GameProgressListener implements Listener {

    private final TTT plugin;
    private final RoleManager roleManager;
    private final StatsManager statsManager;

    public GameProgressListener(TTT plugin) {
        this.plugin = plugin;
        roleManager = plugin.getRoleManager();
        statsManager = plugin.getStatsManager();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        if(!(event.getDamager() instanceof Player)) return;
        if(!(event.getEntity() instanceof Player || event.getEntity() instanceof Creeper)) return;

        Player damagingPlayer = (Player) event.getDamager(),
                recievingPlayer = (Player) event.getEntity();
        Role damagerRole = roleManager.getPlayerRole(damagingPlayer),
                recieverRole = roleManager.getPlayerRole(recievingPlayer);

        if(damagerRole == Role.TRAITOR && recieverRole == Role.TRAITOR) event.setDamage(0);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        Player deadPlayer = event.getEntity();

        if(deadPlayer.getKiller() != null) {
            Player killer = deadPlayer.getKiller();
            Role killerRole = roleManager.getPlayerRole(killer),
                    deadPlayerRole = roleManager.getPlayerRole(deadPlayer);

            switch(killerRole) {
                case TRAITOR:
                    if(deadPlayerRole == Role.TRAITOR) {
                        int karma = 20;
                        killer.sendMessage(TTT.PREFIX + "§cDu hast einen Verbündeten getötet!");
                        statsManager.setKarma(killer, (statsManager.getKarma(killer) - karma));
                        spawnHologram(deadPlayer, killer, karma);
                        statsManager.setFK(killer, statsManager.getFK(killer) + 1);
                    } else if(deadPlayerRole == Role.DETECTIVE){
                        int karma = 10;
                        killer.sendMessage(TTT.PREFIX + "Du hast " + deadPlayerRole.getChatColor() +
                                deadPlayer.getName() + " umgebracht");
                        plugin.getRoleInventory().getPointManager().addPoints(killer, 3);
                        statsManager.setKarma(killer, (statsManager.getKarma(killer) + karma));
                        spawnHologram(deadPlayer, killer, karma);
                    } else if(deadPlayerRole == Role.INNOCENT){
                        int karma = 10;
                        killer.sendMessage(TTT.PREFIX + "Du hast §e" + deadPlayer.getName() + " umgebracht");
                        plugin.getRoleInventory().getPointManager().addPoints(killer, 1);
                        statsManager.setKarma(killer, (statsManager.getKarma(killer) + karma));
                        spawnHologram(deadPlayer, killer, karma);
                    }
                    statsManager.setKills(killer, statsManager.getKills(killer) + 1);
                    break;

                case DETECTIVE:
                case INNOCENT:
                    if (deadPlayerRole == Role.TRAITOR) {
                        int karma = 20;
                        killer.sendMessage(TTT.PREFIX + "§aDu hast einen §cVerräter §aerwischt!");
                        plugin.getRoleInventory().getPointManager().addPoints(killer, 2);
                        statsManager.setKarma(killer, statsManager.getKarma(killer) + karma);
                        spawnHologram(deadPlayer, killer, karma);
                    } else if (deadPlayerRole == Role.DETECTIVE) {
                        int karma = 50;
                        killer.sendMessage(TTT.PREFIX + "§cDu hast einen §9Detektiv §cgetötet!");
                        plugin.getRoleInventory().getPointManager().addPoints(killer, 3);
                        statsManager.setKarma(killer, (statsManager.getKarma(killer) - karma));
                        spawnHologram(deadPlayer, killer, karma);
                        statsManager.setFK(killer, statsManager.getFK(killer) + 1);
                    } else {
                        int karma = 20;
                        killer.sendMessage(TTT.PREFIX + "§cDu hast einen §aUnschuldigen §cgetötet!");
                        statsManager.setKarma(killer, (statsManager.getKarma(killer) - karma));
                        spawnHologram(deadPlayer, killer, karma);
                        statsManager.setFK(killer, statsManager.getFK(killer) + 1);
                    }
                    statsManager.setKills(killer, statsManager.getKills(killer) + 1);
                    break;
            }
            deadPlayer.sendMessage(TTT.PREFIX + "§cDu wurdest von " + killerRole.getChatColor() + killer.getName() + " §cgetötet!");
            statsManager.setDeaths(deadPlayer, statsManager.getDeaths(deadPlayer) + 1);
        } else {
            deadPlayer.sendMessage(TTT.PREFIX + "§cDu bist gestorben!");
            statsManager.setDeaths(deadPlayer, statsManager.getDeaths(deadPlayer) + 1);
        }
        if(plugin.getRoleManager().getPlayerRole(deadPlayer) == Role.TRAITOR)
            roleManager.getTraitorPlayers().remove(deadPlayer.getName());
        plugin.getPlayers().remove(deadPlayer);
        new Corpse(plugin, deadPlayer, deadPlayer.getKiller(), deadPlayer.getLocation());
        respawn(deadPlayer);
        deadPlayer.sendMessage(TTT.PREFIX + "§7Du bist jetzt ein Zuschauer");
        CoinAPI.getApi().addCoins(deadPlayer.getUniqueId(),
                (int) ((-0.3 * ingameState.getGameEndCountdown().getSeconds()) + 200));
        for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
            ingameState.getIngameScoreboard().updateScoreboard(currentPlayer);
        }
        ingameState.checkForGameEnding();
    }

    private void respawn(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.spigot().respawn();
            }
        }, 1);
    }

    private void spawnHologram(Player deadPlayer, Player killer, int karma) {
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);
        Hologram hologram = api.createHologram(deadPlayer.getLocation().add(0, 1, 0));
        if(karma > 0) hologram.getLines().appendText("§a+" + karma + " §7Karma");
        else hologram.getLines().appendText("§c-" + karma + " §7Karma");

        VisibilitySettings visibilitySettings = hologram.getVisibilitySettings();
        visibilitySettings.setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        visibilitySettings.setIndividualVisibility(killer, VisibilitySettings.Visibility.VISIBLE);

        new BukkitRunnable() {
            int ticksRun;

            @Override
            public void run() {
                ticksRun++;
                hologram.setPosition(hologram.getPosition().add(0.0, 0.1, 0.0));

                if(ticksRun > 30) {
                    hologram.delete();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        Player player = event.getPlayer();
        if(plugin.getPlayers().contains(player)) {
            plugin.getPlayers().remove(player);
            ((IngameState) plugin.getGameStateManager().getCurrentGameState()).checkForGameEnding();
        }
        event.setQuitMessage("");
    }
}
