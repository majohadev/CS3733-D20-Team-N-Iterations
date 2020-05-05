package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.database.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class staticMap {
  public static void staticMap(String[] args) throws IOException {
    BufferedImage img =
        ImageIO.read(
            new File(
                "C:\\Users\\Owner\\Desktop\\CS3733 Iteration 1\\CS3733-D20-Team-N-Iterations\\src\\main\\resources\\edu\\wpi\\N\\images\\map\\hospital-to-hospital.png"));
    BufferedImage real = resize(500, 500, img);
    ImageIcon icon = new ImageIcon(img);
    JFrame frame = new JFrame();
    frame.setLayout(new FlowLayout());
    frame.setSize(1920, 1080);
    JLabel lbl = new JLabel();
    lbl.setIcon(icon);
    frame.add(lbl);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static BufferedImage resize(int width, int height, BufferedImage img) {
    Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = resized.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();
    return resized;
  }
}
