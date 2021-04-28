package enums;

public enum FrameTypes {

    //код должен быть меньше 127
    ACK ((byte)104),
    LINK ((byte)96),
    UNLINK ((byte)97),
    PRM ((byte)120),
    I ((byte)111),
    RET ((byte)115);

    private final byte frameCode;

    FrameTypes(byte frameCode){
        this.frameCode = frameCode;
    }

    public byte getFrameCode() {
        return frameCode;
    }
}
