package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.voting.Voting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VotingListener implements Listener {

    private final Voting voting;

    public VotingListener(TTT plugin) {
        voting = plugin.getVoting();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();;
        ItemStack itemStack = player.getItemInHand();

        if(itemStack.getItemMeta() == null) return;
        if(itemStack.getItemMeta().getDisplayName().equals(PlayerConnectLobbyListener.VOTING_ITEM_NAME))
            player.openInventory(voting.getVotingInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if(!(event.getView().getTitle().equals(Voting.VOTING_INVENTORY_TITLE))) return;
        event.setCancelled(true);

        for(int i = 0; i < voting.getVotingInventoryOrder().length; i++) {
            if(voting.getVotingInventoryOrder()[i] == event.getSlot()) {
                voting.vote(player, i);
                return;
            }
        }
    }
}
