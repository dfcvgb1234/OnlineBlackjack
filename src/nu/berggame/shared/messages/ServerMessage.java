package nu.berggame.shared.messages;

/**
 * Represents a standard text message sent from the server to the client.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.3
 */

public class ServerMessage implements Message {

    private String message;
    private boolean expectInput;
    private boolean stopSignal;

    // Constructor which takes the message needed to be sent, and if it should expect input from the client, and a stop signal that tells the client to disconnect.
    public ServerMessage(String message, boolean expectInput, boolean stopSignal) {
        this.message = message;
        this.expectInput = expectInput;
        this.stopSignal = stopSignal;
    }

    // Constructor which takes the message needed to be sent, and if it should expect input from the client.
    public ServerMessage(String message, boolean expectInput) {
        this.message = message;
        this.expectInput = expectInput;
        this.stopSignal = false;
    }

    // Constructor which takes a raw string sent from the server, which is then parsed into useful data.
    public ServerMessage(String rawString) {
        String[] msgInfo = rawString.split(";");

        this.message = msgInfo[1];
        this.expectInput = msgInfo[2].equalsIgnoreCase("true");
        this.stopSignal = msgInfo[3].equalsIgnoreCase("true");
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isExpectInput() {
        return this.expectInput;
    }

    public boolean isStopSignal() {
        return this.stopSignal;
    }

    // Turns this object into a String representation that can be read by itself when it reaches the client.
    @Override
    public String toString() {
        String inputMessage = "false";
        String stopMessage = "false";

        if (expectInput) {
            inputMessage = "true";
        }
        if (stopSignal) {
            stopMessage = "true";
        }
        return "msg;" + message + ";" + inputMessage + ";" + stopMessage; // Creates a separated list, which can be split up when it should be parsed.
    }
}
