package edu.wpi.N.database;
//TODO: Add your imports
import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.*;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Laundry;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.entities.request.Request;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/*TODO: implement tests for your serviceType
Follow the golden rule of database tests: WHEN THE CONTROL FLOW EXITS YOUR FUNCTION, THE DATABASE SHOULD BE 110,000%
IDENTICAL TO WHAT IT WAS WHEN CONTROL FLOW ENTERED YOUR FUNCTION
For service requests, if you mutate them, that's probably ok.
DO NOT RELY ON IDS BEING PARTICULAR VALUES
ServiceDB.getEmployee(1) <--- NO
TURN OFF AUTOCOMMIT BEFORE ENTERING YOUR TESTS, CATCH DBEXCEPTION AND ROLLBACK
*/
public class ServicesTest {
    private static Connection con;
    @BeforeAll
    public void setup() throws ClassNotFoundException, SQLException, DBException, FileNotFoundException {
        MapDB.initTestDB();
    }

    @Test
    public void example() throws DBException {
        try{
            con.setAutoCommit(false);
            //Insertion statements, like addTranslator
            con.commit();
            con.setAutoCommit(true);
            //checking statements
            //deleting statements
        }catch(SQLException e){ //also wanna catch DBException e
            try{
                con.rollback();
                con.setAutoCommit(true);
            }catch(SQLException ex){
                throw new DBException("Oh no");
            }
        }
    }

}
