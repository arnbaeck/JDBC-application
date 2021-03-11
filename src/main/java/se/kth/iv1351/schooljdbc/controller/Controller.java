package se.kth.iv1351.schooljdbc.controller;

import se.kth.iv1351.schooljdbc.integration.SchoolDAO;
import se.kth.iv1351.schooljdbc.integration.SchooldbException;
import se.kth.iv1351.schooljdbc.model.Instrument;
import se.kth.iv1351.schooljdbc.model.Student;

import java.util.List;

public class Controller {
    private final SchoolDAO schoolDao;

    public Controller() throws SchooldbException {
        this.schoolDao = new SchoolDAO();
    }

    public List<Instrument> getInstruments(String instrumentName) throws Exception {
        try{
            return schoolDao.listInstruments(instrumentName);

        }
        catch (SchooldbException e){
           throw new Exception("e", e);
        }
    }

    public Student getStudent(int studentId) throws Exception {
        try{
            return schoolDao.getStudent(studentId);
        }
        catch (SchooldbException e){
            throw new Exception("e", e);
        }
    }

    public String rentInstrument (Student student, int instrumentId,
                                  String instrumentName, int monthsRented) throws Exception {
        String message;
        if (student.getInstrumentsRented() < 2){
            try{
                message = schoolDao.rent(student.getStudentID(), instrumentName, monthsRented, instrumentId);
                student.addInstrumentsRented();
            }
            catch (SchooldbException e){
                throw new Exception("e", e);
            }
        }else {
            throw new Exception("e");
        }
        return message;
    }

    public String terminateRental(Student student, int rentalId, int rentalInstrumentID) throws Exception {
        String message;
        try{
            message = schoolDao.terminate(rentalId, rentalInstrumentID);
            if (student.getInstrumentsRented()>0) {
                student.reduceInstrumentsRented();
            }
        }
        catch (SchooldbException e){
            throw new Exception("e", e);
        }
        return message;
    }






}
