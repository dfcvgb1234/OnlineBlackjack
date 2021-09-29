package nu.berggame.server;

/**
 * Main entry point for the server, this is run whenever the server i started.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.2
 */

public class GameServer {

    public static void main(String[] args) {
        System.out.println("Starting GameHost server!");
        GameHost server = new GameHost();
        server.start(6060);
    }
}
