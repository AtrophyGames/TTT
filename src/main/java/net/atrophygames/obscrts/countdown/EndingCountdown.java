package net.atrophygames.obscrts.countdown;

import net.atrophygames.obscrts.TTT;
import org.bukkit.Bukkit;

public class EndingCountdown extends Countdown {


    private static final int ENDING_SECONDS = 20;

    private TTT plugin;
    private int seconds;

    public EndingCountdown(TTT plugin) {
        this.plugin = plugin;
        seconds = ENDING_SECONDS;
    }

    @Override
    public void start() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
                switch(seconds) {
                    case 20: case 15: case 10: case 5: case 3: case 2:
                        Bukkit.broadcastMessage(TTT.PREFIX + "§cDer Server stoppt in §e" + seconds + " §cSekunden!");
                        break;
                    case 1:
                        Bukkit.broadcastMessage(TTT.PREFIX + "§cDer Server stoppt in §eeiner §cSekunde!");
                        break;
                    case 0:
                        Bukkit.broadcastMessage(TTT.PREFIX + "§cDer Server stoppt jetzt!");
                        plugin.getGameStateManager().getCurrentGameState().stop();
                        stop();
                        break;
                }
                seconds--;
            }
        }, 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
