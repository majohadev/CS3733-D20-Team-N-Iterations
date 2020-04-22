package edu.wpi.N;

import edu.wpi.N.database.*;
import edu.wpi.N.entities.DbNode;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.LinkedList;

public class Main {

  public static void main(String[] args) throws SQLException, DBException, ClassNotFoundException {
    MapDB.initDB();
    InputStream nodes = Main.class.getResourceAsStream("csv/newNodes.csv");
    InputStream edges = Main.class.getResourceAsStream("csv/newEdges.csv");
    CSVParser.parseCSV(nodes);
    CSVParser.parseCSV(edges);
    LinkedList<DbNode> offices = new LinkedList();
    offices.add(MapDB.getNode("NDEPT01804"));
    offices.add(MapDB.getNode("NDEPT00604"));
    DoctorDB.addDoctor("Dr.Wilson Wong", "Softeng", offices);
    LinkedList<DbNode> offices1 = new LinkedList();
    offices1.add(MapDB.getNode("NDEPT00104"));
    offices1.add(MapDB.getNode("NDEPT00604"));
    DoctorDB.addDoctor("Dr.Hue Jace", "Ligma", offices);
    LinkedList<DbNode> offices2 = new LinkedList();
    offices2.add(MapDB.getNode("NDEPT00704"));
    offices2.add(MapDB.getNode("NDEPT01004"));
    DoctorDB.addDoctor("Dr.Seymour Butts", "Ligma", offices2);
    LinkedList<String> languagesA = new LinkedList<>();
    languagesA.add("English");
    LinkedList<String> languagesB = new LinkedList<>();
    languagesB.add("English");
    languagesB.add("Mandarin");
    LinkedList<String> languagesC = new LinkedList<>();
    languagesC.add("English");
    languagesC.add("Spanish");
    languagesC.add("Russian");

    ServiceDB.addLaundry("Joe");
    ServiceDB.addLaundry("Randy");
    ServiceDB.addLaundry("Wilson");
    ServiceDB.addTranslator("Bob", languagesA);
    ServiceDB.addTranslator("Andy", languagesB);
    ServiceDB.addTranslator("Camille", languagesC);
    DoctorDB.addDoctor("Dr.Seymour Seymourson", "Ligma", offices2);
    LinkedList<DbNode> offices3 = new LinkedList();
    offices3.add(MapDB.getNode("NDEPT00304"));
    offices3.add(MapDB.getNode("NDEPT01004"));
    DoctorDB.addDoctor("Dr.Doolittle", "Ligma", offices3);
    DoctorDB.addDoctor("Dr.Dre", "Ligma", offices3);
    DoctorDB.addDoctor("Dr.Who", "Ligma", offices3);
    DoctorDB.addDoctor("Dr.Oz", "Ligma", offices3);
    DoctorDB.addDoctor("Dr.Zoidberg", "Ligma", offices3);
    DoctorDB.addDoctor("Doc Brown", "Ligma", offices3);
    DoctorDB.addDoctor("Dr.Suess", "Ligma", offices3);
    App.launch(App.class, args);
  }
}
