package bcit.darcy.voicechat.games;


import bcit.darcy.voicechat.GameClient;
import bcit.darcy.voicechat.Packet;

public class TicTacToe extends Game {
    private String[] actions = new String[] {
            "Cell 1x1", "Cell 1x2", "Cell 1x3", "Cell 2x1", "Cell 2x2", "Cell 2x3", "Cell 3x1", "Cell 3x2", "Cell 3x3"
    };

    public TicTacToe(GameClient client, int uuid) {
        super(client, uuid);
    }

    @Override
    public String[] getActions() {
        return new String[0];
    }

    @Override
    public boolean hasEnded() {
        return false;
    }

    @Override
    public void handleAction(String action) {

    }

    @Override
    public void applyUpdate(Packet.Response update) {

    }

    @Override
    public String getCurrentState() {
        return null;
    }
}
