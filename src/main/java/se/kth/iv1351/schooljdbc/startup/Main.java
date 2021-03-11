package se.kth.iv1351.schooljdbc.startup;


//import se.kth.iv1351.schooljdbc.controller.Controller;
import se.kth.iv1351.schooljdbc.controller.Controller;
import se.kth.iv1351.schooljdbc.integration.SchoolDAO;
import se.kth.iv1351.schooljdbc.integration.SchooldbException;
import se.kth.iv1351.schooljdbc.view.BlockingInterpreter;
//import se.kth.iv1351.schooljdbc.view.BlockingInterpreter;

/**
 * Starts the bank client.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
           new BlockingInterpreter(new Controller()).handleCmds();



        } catch(SchooldbException bdbe) {
            System.out.println("Could not connect to School db.");
            bdbe.printStackTrace();
        }
    }
}