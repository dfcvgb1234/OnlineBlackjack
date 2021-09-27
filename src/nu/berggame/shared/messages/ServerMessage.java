package nu.berggame.shared.messages;

public class ServerMessage implements Message {

    private String message;
    private boolean expectInput;
    private boolean stopSignal;

    public ServerMessage(String message, boolean expectInput) {
        this.message = message;
        this.expectInput = expectInput;
        this.stopSignal = false;
    }

    public ServerMessage(String rawString) {
        String[] msgInfo = rawString.split(";");
        this.message = msgInfo[1];

        if (msgInfo[2].equalsIgnoreCase("true")) {
            this.expectInput = true;
        }
        else {
            this.expectInput = false;
        }

        if (msgInfo[3].equalsIgnoreCase("true")) {
            this.stopSignal = true;
        }
        else {
            this.stopSignal = false;
        }
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

    public ServerMessage(String message, boolean expectInput, boolean stopSignal) {
        this.message = message;
        this.expectInput = expectInput;
        this.stopSignal = stopSignal;
    }

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
        return "msg;" + message + ";" + inputMessage + ";" + stopMessage;
    }
}
