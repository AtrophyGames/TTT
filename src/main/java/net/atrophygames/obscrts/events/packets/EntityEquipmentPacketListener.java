package net.atrophygames.obscrts.events.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.role.Role;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class EntityEquipmentPacketListener extends PacketAdapter {

    private final TTT plugin;

    public EntityEquipmentPacketListener(TTT plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player playerReceivingPacket = event.getPlayer();
        Entity entityHoldingItem = packet.getEntityModifier(playerReceivingPacket.getWorld()).read(0);

        int holdingSlot = packet.getIntegers().read(1);
        ItemStack holdingItem = packet.getItemModifier().read(0);

        if(holdingSlot == 0) {
            if(entityHoldingItem instanceof Player &&
                    isTraitor((Player) entityHoldingItem) &&
                    !isAllowedItem(holdingItem.getType())) {

                packet.getItemModifier().write(0, new ItemStack(Material.AIR));
            }
        } else if(!isTraitor(playerReceivingPacket) &&
                holdingItem.getType() == Material.LEATHER_CHESTPLATE) {

            LeatherArmorMeta armorMeta = (LeatherArmorMeta) holdingItem.getItemMeta();

            if(armorMeta != null) {
                armorMeta.setColor(Color.GRAY);
                holdingItem.setItemMeta(armorMeta);

                event.getPacket().getItemModifier().write(0, holdingItem);
            }
        }
    }

    private boolean isTraitor(Player player) {
        return plugin.getRoleManager().getPlayerRole(player) == Role.TRAITOR;
    }

    private boolean isAllowedItem(Material material) {
        return material == Material.WOODEN_SWORD || material == Material.STONE_SWORD || material == Material.IRON_SWORD ||
                material == Material.BOW || material == Material.ARROW;
    }
}