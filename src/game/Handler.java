package game;

public class Handler {

    private final GameApp gameApp;

    public Handler(GameApp gameApp) {
        this.gameApp = gameApp;
    }

    public GameApp getGame() {
        return gameApp;
    }
}
