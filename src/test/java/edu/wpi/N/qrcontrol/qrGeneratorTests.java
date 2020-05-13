// package edu.wpi.N.qrcontrol;
//
// import edu.wpi.N.algorithms.Direction;
// import javafx.embed.swing.JFXPanel;
// import org.junit.Test;
// import org.junit.jupiter.api.Assertions;
//
// import java.util.ArrayList;
//
// public class qrGeneratorTests extends QRGenerator {
//
//    @Test
//    public void testStoreToPath() {
//      JFXPanel jfxPanel = new JFXPanel(); // Necessary for generating image from file
//      ArrayList<Direction> lines = new ArrayList<>();
//      lines.add("Cool");
//      lines.add("and");
//      lines.add("good.");
//      Assertions.assertNotNull(generateImage(lines, true));
//    }
//
//    // Tests normal functionality - i.e. a non-null list of non-null string(s) is passed in
//    @Test
//    public void testStringValid() {
//      ArrayList<String> lines = new ArrayList<>();
//      lines.add("Cool");
//      lines.add("and");
//      lines.add("good.");
//      Assertions.assertNotNull(generateImage(lines, false));
//    }
//
//    // Test whether a final string with too many characters produces a QR code
//    @Test
//    public void testStringTooLong() {
//      ArrayList<Direction> lines = new ArrayList<>();
//      for (int i = 0; i < 26; i++) {
//        lines.add("Add. 20. characters!");
//      }
//      Assertions.assertNull(generateImage(lines, false));
//    }
//
//    // Test whether an empty list of strings produces a QR code
//    @Test
//    public void testStringEmptyList() {
//      ArrayList<Direction> lines = new ArrayList<>();
//      Assertions.assertNull(generateImage(lines, false));
//    }
//
//    // test whether a null reference to a list of strings produces a QR code
//    @Test
//    public void testStringNullList() {
//      Assertions.assertNull(generateImage(null, false));
//    }
//
//    // Test whether null list items passed in count toward generation
//    @Test
//    public void testStringBlankItems() {
//      ArrayList<Direction> lines = new ArrayList<>();
//      lines.add(null);
//      Assertions.assertNull(generateImage(lines, false));
//    }
// }
