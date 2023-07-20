package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.IngameState;
import net.atrophygames.obscrts.role.detective.Tester;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TesterListener implements Listener {

    private final TTT plugin;

    public TesterListener(TTT plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTesterClick(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();

        if((block.getType() != Material.LEGACY_WOOD_BUTTON) && (block.getType() != Material.STONE_BUTTON)) return;
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        IngameState ingameState = (IngameState) plugin.getGameStateManager().getCurrentGameState();
        if(ingameState.isGrace()) return;

        Tester tester = ingameState.getMap().getTester();
        if(tester.getTesterButton().getLocation().equals(block.getLocation()))
            tester.test(event.getPlayer());
    }
}
