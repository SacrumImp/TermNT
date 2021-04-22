package enums;

public enum Baud {

    //доступно 8 значений

    A (9600);

    private final int speed;

    Baud(int speed) { this.speed = speed; }

    public int getSpeed() {
        return speed;
    }
}