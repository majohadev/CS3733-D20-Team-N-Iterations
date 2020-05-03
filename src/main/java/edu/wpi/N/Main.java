package edu.wpi.N;

import edu.wpi.N.database.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

  public static void main(String[] args)
      throws SQLException, DBException, ClassNotFoundException, IOException {
    MapDB.initDB();
    // ArduinoController periperal = new ArduinoController();
    // periperal.initialize();
    //    MapDB.setKiosk("NSERV00301", 180);

    /*final String DEFAULT_NODES = "csv/UPDATEDTeamNnodes.csv";
    final String DEFAULT_PATHS = "csv/UPDATEDTeamNedges.csv";
    final InputStream INPUT_NODES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_NODES);
    final InputStream INPUT_EDGES_DEFAULT = Main.class.getResourceAsStream(DEFAULT_PATHS);
    CSVParser.parseCSV(INPUT_NODES_DEFAULT);
    CSVParser.parseCSV(INPUT_EDGES_DEFAULT);*/

    // App.launch(App.class, args);
    BufferedImage img =
        ImageIO.read(
            new File(
                "C:\\Users\\Owner\\Desktop\\CS3733 Iteration 1\\CS3733-D20-Team-N-Iterations\\src\\main\\resources\\edu\\wpi\\N\\images\\map\\hospitalsPath.jpg"));
    ImageIcon icon = new ImageIcon(img);
    // Image scaleImage = icon.getImage().getScaledInstance(28, 28, Image.SCALE_DEFAULT);
    JFrame frame = new JFrame();
    frame.setLayout(new FlowLayout());
    frame.setSize(1920, 1080);
    JLabel lbl = new JLabel();
    lbl.setIcon(icon);
    frame.add(lbl);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
