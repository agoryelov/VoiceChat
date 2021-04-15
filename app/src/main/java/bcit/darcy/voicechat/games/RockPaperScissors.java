package bcit.darcy.voicechat.games;

import java.util.Arrays;

import bcit.darcy.voicechat.ByteCodes;
import bcit.darcy.voicechat.GameClient;
import bcit.darcy.voicechat.Packet;
import bcit.darcy.voicechat.Packet.Request;
import bcit.darcy.voicechat.Packet.Response;

public class RockPaperScissors extends Game {
    private final String[] outcomes = {"win", "lose", "tie"};
    private final String[] actions = {"Rock", "Paper", "Scissors"};

    public RockPaperScissors(GameClient client, int uuid) {
        super(client, uuid);
    }

    @Override
    public String[] getActions() {
        return actions;
    }

    @Override
    public boolean hasEnded() {
        return hasEnded;
    }

    @Override
    public void handleAction(String action) {
        if (!hasStarted) {
            client.printMessage("RPS: Game not started yet");
            return;
        }

        if (hasEnded) {
            client.printMessage("RPS: Game already ended");
            return;
        }

        client.printMessage("RPS: You played " + action);
        byte actionCode = (byte) (Arrays.asList(actions).indexOf(action) + 1);

        byte[] payload = {actionCode};
        Request r = Packet.getRequest(
                uuid,
                ByteCodes.GAME_ACTION,
                ByteCodes.MAKE_MOVE,
                payload);

        client.printMessage("Client: Sending packet " + Arrays.toString(Packet.getBytes(r)));
        client.sendRequest(r);
    }

    @Override
    public void applyUpdate(Response update) {
        if (update.context == ByteCodes.START_GAME) {
            startVoiceChat();
            hasStarted = true;
            client.printMessage("RPS: Game started");
        }

        if (update.context == ByteCodes.GAME_END) {
            String message = parseGameEnd(update);
            client.printMessage("RPS: " + message);
        }

        if (update.context == ByteCodes.DISCONNECTED) {
            client.printMessage("RPS: Opponent disconnected");
        }
    }


    private String parseGameEnd(Response update) {
        if (update.payloadSize < 1) {
            return "Unknown(empty)";
        }

        String unknownPayload = "Unknown (" + Arrays.toString(update.payload) + ")";

        if (update.payloadSize != 2) {
            return unknownPayload;
        }

        int outcomeIndex = update.payload[0];
        if (outcomeIndex < 1 || outcomeIndex > 3) {
            return unknownPayload;
        }

        int actionIndex = update.payload[1];
        if (actionIndex < 1 || actionIndex > 3) {
            return unknownPayload;
        }

        String outcome = outcomes[outcomeIndex - 1];
        String opponentAction = actions[actionIndex - 1];

        return "Opponent chose " + opponentAction + ", you " + outcome;
    }
}
