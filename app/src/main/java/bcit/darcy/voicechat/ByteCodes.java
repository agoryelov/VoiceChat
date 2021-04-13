package bcit.darcy.voicechat;

public class ByteCodes {
    public static final byte[] TTT = {1, 1};
    public static final byte[] RPS = {1, 2};

    public static final byte CONFIRM_RULES = 1;
    public static final byte MAKE_MOVE = 1;

    public static final byte CONFIRM = 1;
    public static final byte META_ACTION = 3;
    public static final byte GAME_ACTION = 4;

    public static final byte SUCCESS = 10;
    public static final byte UPDATE = 20;
    public static final byte CLIENT_ERROR = 30;
    public static final byte SERVER_ERROR = 40;
    public static final byte GAME_ERROR = 50;

    public static final byte START_GAME = 1;
    public static final byte MOVE_MADE = 2;
    public static final byte GAME_END = 3;
    public static final byte DISCONNECTED = 4;
}
