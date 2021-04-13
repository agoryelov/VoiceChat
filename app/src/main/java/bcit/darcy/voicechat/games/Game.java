package bcit.darcy.voicechat.games;

import bcit.darcy.voicechat.GameClient;
import bcit.darcy.voicechat.Packet.Response;
import bcit.darcy.voicechat.VoiceChat;

public abstract class Game {
    GameClient client;
    VoiceChat voiceChat;
    int uuid;
    public Game(GameClient client, int uuid) {
        this.client = client;
        this.uuid = uuid;
//        this.voiceChat = new VoiceChat(uuid);
    }
    abstract public String[] getActions();
    abstract public boolean hasEnded();
    abstract public void handleAction(String action);
    abstract public void applyUpdate(Response update);
    abstract public String getCurrentState();

//    public void startVoice() {
//        voiceChat.startSpeak();
//    }
//
//    public void stopVoice() {
//        voiceChat.stopSpeak();
//    }
}
