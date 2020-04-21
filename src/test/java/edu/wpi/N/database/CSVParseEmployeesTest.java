package edu.wpi.N.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class CSVParseEmployeesTest {

    @BeforeAll
    public static void initialize() throws FileNotFoundException, SQLException, DBException, ClassNotFoundException {
        DbController.initDB();
        File fNodes = new File("src/test/resources/edu/wpi/N/csv/Employees.csv");
        String path = fNodes.getAbsolutePath();
        CSVParser.parserCSVEmployeesFromPath(path);
    }

    /**
     * Tests that the function parses the inputted CSV successfully
     */
    @Test
    public void testParseEmployees(){


    }


    @AfterAll
    public static void clearDb() throws DBException {
        DbController.clearNodes();
    }
}
