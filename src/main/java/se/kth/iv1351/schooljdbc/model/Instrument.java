package se.kth.iv1351.schooljdbc.model;

public class Instrument {
    private int id;
    private double fee;
    private String instrumentName;
    private boolean available;
    private String brand;

    public Instrument (int id, double fee, String instrumentName, boolean available, String brand){
        this.id = id;
        this.fee = fee;
        this. instrumentName = instrumentName;
        this.available = available;
        this.brand = brand;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public int getId() {
        return id;
    }

    public double getFee() {
        return fee;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getBrand() {
        return brand;
    }
}
