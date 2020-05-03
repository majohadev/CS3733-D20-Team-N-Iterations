package edu.wpi.N.database;

import com.opencsv.CSVReader;
import edu.wpi.N.entities.DbNode;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class CSVParser {

  /**
   * Parse NodeCSV or EdgeCSV file and add entries to Database
   *
   * @param pathToFile: path to the CSV file as an InputStream
   */
  public static void parseCSV(InputStream pathToFile) {
    try {
      // Assume that it is NodeCSV
      Boolean isNodeCSV = true;

      // create csvReader object passing
      CSVReader csvReader = new CSVReader(new InputStreamReader(pathToFile, "UTF-8"));

      // Read header
      String[] nextLine = csvReader.readNext();

      // Check if it is EdgeCSV
      if (nextLine[0].toLowerCase().equals("edgeid")
          || nextLine[1].toLowerCase().equals("startnode")) {
        isNodeCSV = false;
      }

      if (isNodeCSV) {
        // Parse NodeCSV data line by line except header
        while ((nextLine = csvReader.readNext()) != null) {
          parseNodeRow(nextLine);
        }
      } else {
        // Parse EdgesCSV data line by line except header
        while ((nextLine = csvReader.readNext()) != null) {
          parseEdgesRow(nextLine);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Parse data from a given row: add Edge to the database
   *
   * @param row: a row to parse data from
   */
  private static void parseEdgesRow(String[] row) throws Exception {
    try {
      // String edgeID = row[0];
      String startNodeId = row[1];
      String endNodeId = row[2];

      MapDB.addEdge(startNodeId, endNodeId);
    } catch (Exception e) {
      // for debugging purposes
      System.out.println(row[0]);
      throw (e);
    }
  }

  /**
   * Parse data from a given row: add Node to the database
   *
   * @param row: a row to parse data from
   */
  private static void parseNodeRow(String[] row) throws Exception {
    try {
      String nodeID = row[0];
      int xcoord = Integer.parseInt(row[1]);
      int ycoord = Integer.parseInt(row[2]);
      int floor = Integer.parseInt(row[3]);
      String building = row[4];
      String nodeType = row[5];
      String longName = row[6];
      String shortName = row[7];
      char teamAssigned;

      try {
        teamAssigned = row[8].charAt(0);
      } catch (Exception ex){
        teamAssigned = 'Z';
      }

      MapDB.addNode(
          nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned);
    } catch (Exception e) {
      // for debugging purposes
      System.out.println(row[0]);
      throw (e);
    }
  }

  /**
   * Parses CSV file from a given Full Path to file
   *
   * @param pathToFile full path to file
   * @throws FileNotFoundException
   */
  public static void parseCSVfromPath(String pathToFile) throws FileNotFoundException {
    try {
      File initialFile = new File(pathToFile);
      InputStream input = new FileInputStream(initialFile);

      CSVParser.parseCSV(input);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw (e);
    }
  }

  /**
   * Parses CSV with employees from given path and adds them to database
   *
   * @param pathToFile: full path to file as a string
   * @throws FileNotFoundException
   */
  public static void parseCSVEmployeesFromPath(String pathToFile) throws FileNotFoundException {
    try {
      File initialFile = new File(pathToFile);
      InputStream input = new FileInputStream(initialFile);

      CSVParser.parseCSVEmployees(input);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw (e);
    }
  }

  /**
   * Parse Employees CSV file and add entries to Database
   *
   * @param pathToFile: path to the CSV file as an InputStream
   */
  public static void parseCSVEmployees(InputStream pathToFile) {
    try {

      // create csvReader object passing
      CSVReader csvReader = new CSVReader(new InputStreamReader(pathToFile, "UTF-8"));

      // Read header
      String[] nextLine = csvReader.readNext();

      while ((nextLine = csvReader.readNext()) != null) {
        parseEmployeeRow(nextLine);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses a row with an employee and adds an employee to database
   *
   * @param row
   * @throws Exception
   */
  private static void parseEmployeeRow(String[] row) throws Exception {
    try {
      String name = row[1].trim();
      String serviceType = row[2];
      serviceType = serviceType.toLowerCase();

      if (serviceType.equals("translator")) {
        String[] languages = row[3].replaceAll("\\s+", "").split(",");

        ServiceDB.addTranslator(name, new LinkedList<String>(Arrays.asList(languages)));
      } else if (serviceType.equals("laundry")) {
        ServiceDB.addLaundry(name);
      } else if (serviceType.equals("medicine")) {
        String field = row[5];
        String userName = row[6];
        createDoctor(name, field, userName);
      } else if (serviceType.equals("emotional support")) {
        ServiceDB.addEmotionalSupporter(name);
      } else if (serviceType.equals("flower")) {
        ServiceDB.addFlowerDeliverer(name);
      } else if (serviceType.equals("internal transportation")) {
        ServiceDB.addInternalTransportationEmployee(name);
      } else if (serviceType.equals("it")) {
        ServiceDB.addIT(name);
      } else if (serviceType.equals("sanitation")) {
        ServiceDB.addSanitationEmp(name);
      } else if (serviceType.equals("security")) {
        ServiceDB.addSecurityOfficer(name);
      } else if (serviceType.equals("wheelchair")) {
        ServiceDB.addWheelchairEmployee(name);
      }
    } catch (Exception e) {
      // for debugging purposes
      System.out.println(row[1]);
      throw (e);
    }
  }

  /**
   * Function creates a doctor with the given name, field and userName. Default password is 12345
   *
   * @param name
   * @param field
   * @param userName
   */
  private static void createDoctor(String name, String field, String userName) throws DBException {
    String password = "12345";

    LinkedList<DbNode> locations = generateRandomLocations();

    DoctorDB.addDoctor(name, field, userName, password, locations);
  }

  /**
   * Function generates random DEPT locations
   *
   * @return
   */
  private static LinkedList<DbNode> generateRandomLocations() throws DBException {

    LinkedList<DbNode> randomLocations = new LinkedList<DbNode>();

    // generate random floor in a range between min (inclusive) and max (inclusive).
    int min = 1;
    int max = 4;
    Random r = new Random();
    int randFloor = r.nextInt((max - min) + 1) + min;

    LinkedList<DbNode> locations = MapDB.searchVisNode(randFloor, null, "DEPT", "");
    int numLocations = locations.size();

    // generate 3 random indexes corresponding to locations
    int randLocOne = r.nextInt(numLocations);
    int randLocTwo = r.nextInt(numLocations);
    int randLocThree = r.nextInt(numLocations);

    // avoid duplicates
    while (randLocOne == randLocTwo || randLocTwo == randLocThree || randLocOne == randLocThree) {
      if (randLocOne == randLocTwo) {
        randLocOne = r.nextInt(numLocations);
      } else if (randLocTwo == randLocThree) {
        randLocTwo = r.nextInt(numLocations);
      } else if (randLocOne == randLocThree) {
        randLocOne = r.nextInt(numLocations);
      }
    }

    // add them to doctor's locations
    randomLocations.add(locations.get(randLocOne));
    randomLocations.add(locations.get(randLocTwo));
    randomLocations.add(locations.get(randLocThree));

    return randomLocations;
  }

  /**
   * maps a floor string (L2, L1, G, 1, 2, 3) to a number
   * @param floor the floor in the CSV parser
   * @return integer representing that floor (0 for invalid floors)
   */
  public static int convertFloor(String floor){
    try{
      int convert = Integer.parseInt(floor);
      return convert + 3;
    }catch(NumberFormatException e){
      if(floor.equals("L2")) return 1;
      if(floor.equals("L1")) return 2;
      if(floor.equals("G")) return 3;
      return 0;
    }
  }

  /**
   * Maps a floor number to a string
   * @param floor The floor number, must be 1-6 inclusive
   * @return A string representing that floor number ("Invalid" if invalid)
   */
  public static String convertBack(int floor){
    String[] floors = {"L2", "L1", "G", "1", "2", "3"};
    try{
      return floors[floor-1];
    }catch(ArrayIndexOutOfBoundsException e){
      return "Invalid";
    }
  }
}
