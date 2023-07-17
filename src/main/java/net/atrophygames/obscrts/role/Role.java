package net.atrophygames.obscrts.role;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Role {


    INNOCENT("Unschuldiger", ChatColor.GREEN),
    DETECTIVE("Detektiv", ChatColor.BLUE),
    TRAITOR("Verräter", ChatColor.DARK_RED);

    @Getter
    private String name;
    @Getter
    private ChatColor chatColor;

    private Role(String name, ChatColor chatColor) {
        this.name = name;
        this.chatColor = chatColor;
    }
}
