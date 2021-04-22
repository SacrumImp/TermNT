package enums;

public enum StopBits {

    //доступно 4 значения

    A ("1"),
    B ("1,5"),
    C ("2");

    private final String stopBits;

    StopBits(String stopBits){
        this.stopBits = stopBits;
    }

    public String getStopBits() {
        return stopBits;
    }
}
