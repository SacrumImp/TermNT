public class Frame {

    private FrameTypes type;
    private byte[] data;
    private int dataLength;
    private int frameSize;

    public Frame(FrameTypes type){
        this.type = type;
        this.dataLength = 0;
    }

    public Frame(FrameTypes type, String data){
        this.type = type;
    }

    public int getFrameSize(){
        return this.frameSize;
    }

    public byte[] getSupervisorFrameToWrite(){
        byte[] frame;

        if (this.dataLength == 0){
            frame = new byte[5];
            this.frameSize = 5;
        }
        else{
            frame = new byte[6 + dataLength];
            this.frameSize = 6;
        }

        frame[0] = 0;
        frame[frame.length - 1] = -1;
        frame[1] = -128;
        frame[2] = -128;
        frame[3] = type.getFrameCode();

        return  frame;
    }


}
