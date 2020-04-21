package edu.wpi.N.database;

import com.opencsv.CSVReader;
import java.io.*;

public class CSVParser {

  /**
   * Parse NodeCSV file and add entries to Database
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
    // String edgeID = row[0];
    String startNodeId = row[1];
    String endNodeId = row[2];

    DbController.addEdge(startNodeId, endNodeId);
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
      char teamAssigned = 'Z';
      if (row.length == 9) {
        teamAssigned = row[8].charAt(0);
      }

      DbController.addNode(
          nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned);
    } catch (Exception e) {
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
}
