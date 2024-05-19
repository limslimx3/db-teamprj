package domain;

public class Seat {
    private String seatNumber;
    private boolean isAvailable;

    public Seat(String seatNumber, boolean isAvailable) {
        this.seatNumber = seatNumber;
        this.isAvailable = isAvailable;
    }

    public String getSeatNumber() { return seatNumber; }
    public boolean isAvailable() { return isAvailable; }
}
