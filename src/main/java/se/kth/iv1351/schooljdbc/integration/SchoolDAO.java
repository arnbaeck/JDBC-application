package se.kth.iv1351.schooljdbc.integration;

import se.kth.iv1351.schooljdbc.model.Instrument;
import se.kth.iv1351.schooljdbc.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SchoolDAO{
    //Student
    private static final String STUDENT_TABLE_NAME = "student";
    private static final String STUDENT_PK_COLUMN_NAME = "student_id";
    private static final String AGE_COLUMN_NAME = "age";

    //instrument
    private static final String INSTRUMENT_TABLE_NAME = "instrument";
    private static final String INSTRUMENT_PK_COLUMN_NAME = "instrument_id";
    private static final String INSTRUMENT_NAME_COLUMN_NAME = "name_of_instrument";
    //rental_instrument
    private static final String RENTAL_INSTRUMENT_TABLE_NAME = "rental_instrument";
    private static final String RENTAL_INSTRUMENT_PK_COLUMN_NAME = "rental_instrument_id";
    private static final String FEE_COLUMN_NAME = "renting_fee";
    private static final String INSTRUMENT_FK_COLUMN_NAME = INSTRUMENT_PK_COLUMN_NAME;
    private static final String AVLB_COLUMN_NAME = "available";
    private static final String BRAND_COLUMN_NAME = "brand";
    private static final String TERMINATED_COLUMN_NAME = "terminated";
    //Rentals
    private static final String RENTALS_TABLE_NAME = "rentals";
    private static final String RENTALS_PK_COLUMN_NAME = "rentals_id";
    private static final String DATE_COLUMN_NAME = "date_rented";
    private static final String LEASED_COLUMN_NAME = "leased_until";
    private static final String NAME_RENTALS_COLUMN_NAME = "name_of_instrument";
    private static final String STUDENT_FK_COLUMN_NAME = STUDENT_PK_COLUMN_NAME;
    private static final String RENTAL_INSTRUMENT_FK_COLUMN_NAME = RENTAL_INSTRUMENT_PK_COLUMN_NAME;

    private Connection connection;
    private PreparedStatement listInstrumentsStmt;
    private PreparedStatement rentInstrumentsStmt;
    private PreparedStatement updateAvlbStmt;
    private PreparedStatement updateLeasedUntil;
    private PreparedStatement terminateLease;
    private PreparedStatement numberOfRentals;

    public SchoolDAO() throws SchooldbException {
        try {
            connectToBankDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SchooldbException("Could not connect to datasource.", exception);
        }
    }

    public List<Instrument> listInstruments(String instrumentName) throws SchooldbException {
        String failureMsg ="e";
        ResultSet result = null;
        List<Instrument> instrument = new ArrayList<>();
        try {
            listInstrumentsStmt.setString(1, instrumentName);
            result = listInstrumentsStmt.executeQuery();
            while (result.next()) {
                instrument.add(new Instrument(result.getInt(RENTAL_INSTRUMENT_PK_COLUMN_NAME),
                        result.getDouble(FEE_COLUMN_NAME),
                        result.getString(INSTRUMENT_NAME_COLUMN_NAME),
                        result.getBoolean(AVLB_COLUMN_NAME),
                        result.getString(BRAND_COLUMN_NAME)
                        ));
            }connection.commit();
        }catch (SQLException sqle) {
            handleException(failureMsg, sqle);

        }
        return instrument;
    }

    public String rent(int studentId, String instrumentName, int monthsRented, int instrumentId) throws SchooldbException {
        String failureMsg = "e";
        LocalDate date = LocalDate.now();
        LocalDate leaseDate = date.plusMonths(monthsRented);

        try {
            int rows = 0;

            rentInstrumentsStmt.setDate(1, Date.valueOf(date));
            rentInstrumentsStmt.setDate(2, Date.valueOf(leaseDate));
            rentInstrumentsStmt.setString(3, instrumentName);
            rentInstrumentsStmt.setInt(4, studentId);
            rentInstrumentsStmt.setInt(5, instrumentId);

            rows = rentInstrumentsStmt.executeUpdate();

            if(rows != 1){
                handleException(failureMsg, null);
            }else{
                updateAvlbStmt.setBoolean(1, false);
                updateAvlbStmt.setInt(2, instrumentId);
                rows = updateAvlbStmt.executeUpdate();
                if(rows != 1) {
                    handleException(failureMsg, null);
                }
            }
            connection.commit();

        }
        catch (SQLException e){
            handleException(failureMsg, e);
        }
        return "Instrument rented!";
    }

    public String terminate (int rentalsId, int rentalInstrumentId) throws SchooldbException {
        String failureMsg = "e";
        LocalDate date = LocalDate.now();
        try {
            int rows = 0;
            terminateLease.setInt(1, rentalsId);
            rows = terminateLease.executeUpdate();
            if (rows != 1) {
                handleException(failureMsg, null);
            }else{
                updateLeasedUntil.setDate(1, Date.valueOf(date));
                updateLeasedUntil.setInt(2, rentalsId);
                rows = updateLeasedUntil.executeUpdate();
                if (rows != 1){
                    handleException(failureMsg, null);
                }
            }
            updateAvlbStmt.setBoolean(1, true);
            updateAvlbStmt.setInt(2, rentalInstrumentId);
            rows = updateAvlbStmt.executeUpdate();
            if(rows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        }
        catch (SQLException e){
            handleException(failureMsg, e);
        }
        return "termination complete";
    }

    public Student getStudent(int studentId) throws SchooldbException {
        String failureMsg = "e";
        Student student = null;
        try{
            int row = 0;
            ResultSet result;
            numberOfRentals.setInt(1, studentId);
            result = numberOfRentals.executeQuery();
            while (result.next()){
                row++;
            }
            student = new Student(studentId, row);
            connection.commit();
        }catch (SQLException e){
            handleException(failureMsg, e);
        }return student;
    }


    private void connectToBankDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SoundGarden",
                "postgres", "jgvtpjyhwef12");

        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
        listInstrumentsStmt = connection.prepareStatement("SELECT i." + INSTRUMENT_NAME_COLUMN_NAME
        + ", r." + BRAND_COLUMN_NAME + ", r." + FEE_COLUMN_NAME + ", r." + RENTAL_INSTRUMENT_PK_COLUMN_NAME
        + ", r." + AVLB_COLUMN_NAME
        + " FROM " + RENTAL_INSTRUMENT_TABLE_NAME + " r INNER JOIN " + INSTRUMENT_TABLE_NAME + " i on r."
        + INSTRUMENT_FK_COLUMN_NAME + " = i." + INSTRUMENT_PK_COLUMN_NAME + " WHERE r." + AVLB_COLUMN_NAME
        + " = true AND " + INSTRUMENT_NAME_COLUMN_NAME + " = ?");

        rentInstrumentsStmt = connection.prepareStatement("INSERT INTO " + RENTALS_TABLE_NAME
        + "(" + DATE_COLUMN_NAME + "," + LEASED_COLUMN_NAME + "," + NAME_RENTALS_COLUMN_NAME
        + "," + STUDENT_FK_COLUMN_NAME + "," + RENTAL_INSTRUMENT_FK_COLUMN_NAME
        + ") VALUES (?,?,?,?,?)");


        updateAvlbStmt = connection.prepareStatement("UPDATE " + RENTAL_INSTRUMENT_TABLE_NAME
        + " SET " + AVLB_COLUMN_NAME + " = ? WHERE " + RENTAL_INSTRUMENT_PK_COLUMN_NAME + " = ?" );

        updateLeasedUntil = connection.prepareStatement("UPDATE " + RENTALS_TABLE_NAME
        + " SET " + LEASED_COLUMN_NAME + " = ? WHERE " + RENTALS_PK_COLUMN_NAME + " = ?");

        terminateLease = connection.prepareStatement("UPDATE " + RENTALS_TABLE_NAME
        + " SET " + TERMINATED_COLUMN_NAME + " = true " + "WHERE " + RENTALS_PK_COLUMN_NAME
         + " = ?");

        numberOfRentals = connection.prepareStatement("SELECT "+ RENTALS_PK_COLUMN_NAME +" FROM " + RENTALS_TABLE_NAME + " WHERE "
                + STUDENT_FK_COLUMN_NAME + "= ? AND " + TERMINATED_COLUMN_NAME + " IS null");

    }

    private void handleException(String failureMsg, Exception cause) throws SchooldbException{
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new SchooldbException(failureMsg, cause);
        } else {
            throw new SchooldbException(failureMsg);
        }
    }


}
