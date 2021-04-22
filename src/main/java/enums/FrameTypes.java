package enums;

public enum FrameTypes {

    //доступно 4 значения

    ACK ((byte)104),
    LINK ((byte)96),
    UNLINK ((byte)97),
    PRM ((byte)120);

    private final byte frameCode;

    private FrameTypes(byte frameCode){
        this.frameCode = frameCode;
    }

    public byte getFrameCode() {
        return frameCode;
    }
}
