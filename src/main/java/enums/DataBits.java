package enums;

public enum DataBits {

    //доступно 4 зачения

    A (8);
    //B (7),
    //C (6),
    //D (5);

    private final int bitsNum;

    DataBits(int bitsNum){
        this.bitsNum = bitsNum;
    }

    public int getBitsNum() {
        return bitsNum;
    }
}
