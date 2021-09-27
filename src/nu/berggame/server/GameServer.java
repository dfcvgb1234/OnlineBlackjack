package nu.berggame.server;

public class GameServer {

    public static void main(String[] args) {
        System.out.println("Starting GameHost server!");
        GameHost server = new GameHost();
        server.start(6060);
    }
}
