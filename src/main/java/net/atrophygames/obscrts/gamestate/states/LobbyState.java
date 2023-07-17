package net.atrophygames.obscrts.gamestate.states;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.countdown.LobbyCountdown;
import net.atrophygames.obscrts.gamestate.GameState;
import net.atrophygames.obscrts.gamestate.GameStateManager;
import net.atrophygames.obscrts.scoreboards.LobbyScoreboard;

public class LobbyState extends GameState {


    public static final int MIN_PLAYERS = 1, //5
                            MAX_PLAYERS = 12;

    private TTT plugin;
    @Getter
    private LobbyCountdown lobbyCountdown;
    @Getter
    private LobbyScoreboard lobbyScoreboard;

    public LobbyState(TTT plugin, GameStateManager gameStateManagern) {
        this.plugin = plugin;
        lobbyCountdown = new LobbyCountdown(gameStateManagern);
        lobbyScoreboard = new LobbyScoreboard(plugin);
    }

    @Override
    public void start() {
        lobbyCountdown.startIdle();
    }

    @Override
    public void stop() {
        InjectionLayer.ext().instance(BridgeServiceHelper.class).changeToIngame();
    }
}

