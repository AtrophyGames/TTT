package net.atrophygames.obscrts.commands;

import net.atrophygames.obscrts.TTT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {


    private TTT plugin;

    public TestCommand(TTT plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage(String.valueOf(plugin.getStatsManager().getKarma((Player) commandSender)));
        return true;
    }
}
