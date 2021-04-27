package Coding;

import java.util.ArrayDeque;

public class Hamming {

    private final byte[] data;

    public Hamming(byte[] data){
        this.data = data;
    }

    public byte[] encode() {

        //проверочные биты
        byte first = 0;
        byte second = 0;
        byte fourth = 0;
        byte eighth = 0;

        ArrayDeque<Byte> que = new ArrayDeque<>();

        byte index = 0;
        byte bit;

        for (byte datum : data) {
            for (byte j = 0; j < 8; j++) {
                bit = (byte) ((datum & (1 << (7 - j))) >> (7 - j));
                switch (index) {
                    case 0 -> {
                        first ^= bit;
                        second ^= bit;
                    }
                    case 1 -> {
                        first ^= bit;
                        fourth ^= bit;
                    }
                    case 2 -> {
                        second ^= bit;
                        fourth ^= bit;
                    }
                    case 3 -> {
                        first ^= bit;
                        second ^= bit;
                        fourth ^= bit;
                    }
                    case 4 -> {
                        first ^= bit;
                        eighth ^= bit;
                    }
                    case 5 -> {
                        second ^= bit;
                        eighth ^= bit;
                    }
                    case 6 -> {
                        first ^= bit;
                        second ^= bit;
                        eighth ^= bit;
                    }
                    case 7 -> {
                        fourth ^= bit;
                        eighth ^= bit;
                    }
                    case 8 -> {
                        first ^= bit;
                        fourth ^= bit;
                        eighth ^= bit;
                    }
                    case 9 -> {
                        second ^= bit;
                        fourth ^= bit;
                        eighth ^= bit;
                    }
                    case 10 -> {
                        first ^= bit;
                        second ^= bit;
                        fourth ^= bit;
                        eighth ^= bit;
                    }
                }
                if (index != 10) index++;
                else {
                    index = 0;
                    que.add(first);
                    first = 0;
                    que.add(second);
                    second = 0;
                    que.add(fourth);
                    fourth = 0;
                    que.add(eighth);
                    eighth = 0;
                }
            }
        }

        byte[] result = new byte[(int)Math.ceil(15.0*(double)data.length/11.0)];
        index = 0;
        int k = 0;
        int n = 0;

        for (int i = 0; i < result.length; i++){
            for (int j = 0; j < 8; j++){
                switch (index) {
                    case 0, 1, 3, 7 -> result[i] |= ((que.peekFirst() != null) ? que.pollFirst() : 0) << (7 - j);
                    default -> {
                        if (k < data.length) result[i] |= (data[k] & (1 << (7 - n))) << (n-j);
                        else return result;
                        if (n != 7) n++;
                        else {
                            n = 0;
                            k++;
                        }
                    }
                }
                if (index != 14) index++;
                else index = 0;
            }
        }
        return result;
    }

}
