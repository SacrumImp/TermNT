package enums;

public enum Parity {

    //доступно 2 значения

    A ("Нет"),
    B ("Да");

    private final String status;

    Parity(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
