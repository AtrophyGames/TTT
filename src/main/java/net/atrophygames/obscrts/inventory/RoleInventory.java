package net.atrophygames.obscrts.inventory;

import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.role.PointManager;
import net.atrophygames.obscrts.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RoleInventory implements Listener {


    public static final String
            TRAITOR_ITEM = "§cVerräter-Shop",
            DETECTIVE_ITEM = "§bDetektiv-Shop",
            TRAITOR_SHOP_TITLE = TRAITOR_ITEM,
            DETECTIVE_SHOP_TITLE = DETECTIVE_ITEM;

    public static final String
            TRAITOR_CREEPER = "3x Creeper-Pfeile",
            TRAITOR_FAKER = "§6Traitor-Ticket",
            DETECTIVE_HEALER = "§aHealing-Station";

    private TTT plugin;
    @Getter
    private ItemStack traitorItem, detectiveItem;
    @Getter
    private Inventory traitorShop, detectiveShop;
    @Getter
    private PointManager pointManager;

    public RoleInventory(TTT plugin) {
        this.plugin = plugin;

        traitorItem = new ItemBuilder(Material.EMERALD).setDisplayName(TRAITOR_ITEM).build();
        detectiveItem = new ItemBuilder(Material.EMERALD).setDisplayName(DETECTIVE_ITEM).build();

        traitorShop = Bukkit.createInventory(null, 36, TRAITOR_SHOP_TITLE);
        detectiveShop = Bukkit.createInventory(null, 36, DETECTIVE_SHOP_TITLE);

        pointManager = new PointManager();
        fillInventories();
    }

    @EventHandler
    public void onPlayerBuyInShop(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack.getItemMeta() == null) return;
        event.setCancelled(true);

        if(event.getView().getTitle().equals(TRAITOR_SHOP_TITLE)) {
            switch(itemStack.getItemMeta().getDisplayName()) {
                case TRAITOR_CREEPER:
                    if(!pointManager.removePoints(player, 3)) {
                        ItemStack creeperEggs = new ItemBuilder(Material.CREEPER_SPAWN_EGG)
                                .setDisplayName(TRAITOR_CREEPER)
                                .build();
                        creeperEggs.setAmount(3);
                        player.getInventory().addItem(creeperEggs);
                        return;
                    }
                    player.sendMessage(TTT.PREFIX + "§cDu hast nicht genug Punkte um dieses Item zu kaufen");
                    break;
                case TRAITOR_FAKER:
                    if(!pointManager.removePoints(player, 5)) {
                        ItemStack faker = new ItemBuilder(Material.PAPER)
                                .setDisplayName(TRAITOR_FAKER)
                                .build();
                        player.getInventory().addItem(faker);
                        return;
                    }
                    player.sendMessage(TTT.PREFIX + "§cDu hast nicht genug Punkte um dieses Item zu kaufen");
                    break;
            }
        } else if(event.getView().getTitle().equals(DETECTIVE_SHOP_TITLE)) {
            switch(itemStack.getItemMeta().getDisplayName()) {
                case DETECTIVE_HEALER:
                    if(!pointManager.removePoints(player, 5)) {
                        ItemStack healer = new ItemBuilder(Material.BEACON)
                                .setDisplayName(DETECTIVE_HEALER)
                                .build();
                        player.getInventory().addItem(healer);
                        return;
                    }
                    player.sendMessage(TTT.PREFIX + "§cDu hast nicht genug Punkte um dieses Item zu kaufen");
                    break;
            }
        }
        player.closeInventory();
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        ingameState.getIngameScoreboard().updateScoreboard(player);
    }

    @EventHandler
    public void onPlayerOpenShop(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if(itemStack.getItemMeta() == null) return;

        switch(itemStack.getItemMeta().getDisplayName()) {
            case TRAITOR_ITEM:
                player.openInventory(traitorShop);
                break;
            case DETECTIVE_ITEM:
                player.openInventory(detectiveShop);
                break;
        }
    }

    public static boolean removeMaterialItem(Player player, Material material) {
        Inventory inventory = player.getInventory();
        int slot = inventory.first(material);
        if (slot == -1)
            return false;

        ItemStack itemStack = inventory.getItem(slot);
        itemStack.setAmount(itemStack.getAmount() - 1);
        if(itemStack.getAmount() <= 0)
            inventory.setItem(slot, null);
        player.updateInventory();
        return true;
    }

    private void fillInventories() {
        for (int i = 0; i <= 35; i++) {
            if(i == 8 || (i >= 10 && i <= 16) || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 24 || i == 25) continue;
            traitorShop.setItem(i,new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, (short) 15)
                    .setDisplayName(" ")
                    .setLore()
                    .build());

            detectiveShop.setItem(i,new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, (short) 15)
                    .setDisplayName(" ")
                    .setLore()
                    .build());
        }
        traitorShop.setItem(8, new ItemBuilder(Material.RED_STAINED_GLASS_PANE, (short) 14)
                .setDisplayName("§cSchließen")
                .setLore()
                .build());
        detectiveShop.setItem(8, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, (short) 14)
                .setDisplayName("§cSchließen")
                .setLore()
                .build());

        traitorShop.setItem(10, new ItemBuilder(Material.CREEPER_SPAWN_EGG)
                .setDisplayName(TRAITOR_CREEPER)
                .setLore(
                        "lore1",
                        "lore2")
                .build());
        traitorShop.setItem(11, new ItemBuilder(Material.PAPER)
                .setDisplayName(TRAITOR_FAKER)
                .setLore(
                        "lore1",
                        "lore2")
                .build());

        detectiveShop.setItem(10, new ItemBuilder(Material.BEACON)
                .setDisplayName(DETECTIVE_HEALER)
                .setLore(
                        "lore1",
                        "lore2")
                .build());
    }
}
