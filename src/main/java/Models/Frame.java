import enums.FrameTypes;

import java.util.Arrays;

public class Frame {

    private FrameTypes type;
    private byte[] data;
    private byte dataLength;
    private int frameSize;

    public Frame(FrameTypes type){
        this.type = type;
        this.dataLength = 0;
    }

    public Frame(FrameTypes type, byte params){
        this.type = type;
        this.data = new byte[] {params};
        this.dataLength = 1;
        this.frameSize = 7;
    }

    public Frame(FrameTypes type, String data){
        this.type = type;
    }

    public Frame(byte[] data){
        for (FrameTypes enumEl : FrameTypes.values()){
            if (enumEl.getFrameCode() == data[3]) {
                this.type = enumEl;
                break;
            }
        }
        this.frameSize = 5;
        if (data[4] != -1){
            this.dataLength = data[4];
            this.frameSize += this.dataLength + 1;
            this.data = Arrays.copyOfRange(data, 5, 5 + dataLength);
        }
        else{
            this.dataLength = 0;
        }
    }

    public int getFrameSize(){
        return this.frameSize;
    }

    public FrameTypes getType(){
        return this.type;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getSupervisorFrameToWrite(){
        byte[] frame;

        if (this.dataLength == 0){
            frame = new byte[5];
            this.frameSize = 5;
        }
        else{
            frame = new byte[6 + dataLength];
            this.frameSize = 6 + dataLength;
            frame[4] = dataLength;
        }

        frame[0] = 0;
        frame[frame.length - 1] = -1;
        frame[1] = -128;
        frame[2] = -128;
        frame[3] = type.getFrameCode();

        for(int i = 0; i < dataLength; i++){
            frame[i+5] = data[i];
        }

        System.out.println(frame);

        return  frame;
    }

    public String getDataString(){
        return "kek";
    }

}
