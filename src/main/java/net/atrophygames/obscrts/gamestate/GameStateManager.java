package net.atrophygames.obscrts.gamestate;

import lombok.Getter;
import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.gamestate.states.*;

public class GameStateManager {


    @Getter
    private TTT plugin;
    @Getter
    private GameState currentGameState;
    private GameState[] gameStates;

    public GameStateManager(TTT plugin) {
        this.plugin = plugin;
        gameStates = new GameState[4];

        gameStates[GameState.LOBBY_STATE] = new LobbyState(plugin,this);
        gameStates[GameState.INGAME_STATE] = new IngameState(plugin);
        gameStates[GameState.WARM_UP_STATE] = new WarmUpState(plugin);
        gameStates[GameState.ENDING_STATE] = new EndingState(plugin);
    }

    public void setGameState(int gameStateID) {
        stopCurrentGameState();
        currentGameState = gameStates[gameStateID];
        currentGameState.start();
    }

    public void stopCurrentGameState() {
        if(currentGameState!= null) currentGameState.stop();
        currentGameState = null;
    }
}
