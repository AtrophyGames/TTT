package net.atrophygames.obscrts.gamestate;

public abstract class GameState {


    public static final int LOBBY_STATE = 0,
                            INGAME_STATE = 1,
                            WARM_UP_STATE= 2,
                            ENDING_STATE = 3;

    public abstract void start();
    public abstract void stop();
}
