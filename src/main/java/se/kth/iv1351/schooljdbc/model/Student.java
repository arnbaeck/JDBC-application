package se.kth.iv1351.schooljdbc.model;

public class Student {
    private int studentID;
    private int instrumentsRented = 0;

    public Student(int studentID, int instrumentsRented){
        this. studentID = studentID;
        this.instrumentsRented = instrumentsRented;
    }


    public int getInstrumentsRented() {
        return instrumentsRented;
    }

    public int getStudentID() {
        return studentID;
    }

    public void addInstrumentsRented(){
        this.instrumentsRented ++;
    }

    public void reduceInstrumentsRented(){
        this.instrumentsRented --;
    }
}
