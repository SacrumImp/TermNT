package enums;

public enum FrameTypes {

    ACK ((byte)104),
    LINK ((byte)96),
    PRM ((byte)120);

    private final byte frameCode;

    private FrameTypes(byte frameCode){
        this.frameCode = frameCode;
    }

    public byte getFrameCode() {
        return frameCode;
    }
}
