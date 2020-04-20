package edu.wpi.N.qrcontrol;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.image.Image;

/*
 Basic QR code generation based on ZXing tutorial:
   https://www.callicoder.com/generate-qr-code-in-java-using-zxing/

 Every UI controller that uses QR generating functions should EXTEND this class!

*/

public abstract class QRGenerator {

  // Path of image containing last stored QR code
  private final String IMAGE_PATH = "edu/wpi/N/images/qrTemp.png";

  // Number of characters after which QR codes get unreasonably large
  private final int MAX_CHARS = 500;

  // How large to make the exported image
  private final int IMAGE_SIZE = 350;

  // Generate QR code as JavaFX Image using concatenated list of strings, optionally storing the
  // result
  protected Image generateImage(ArrayList<String> linesToEncode, boolean storeImage) {

    if (linesToEncode != null) {

      StringBuilder sb = new StringBuilder();

      for (String s : linesToEncode) {
        if (s != null) {
          sb.append(s);
          sb.append("\n");
        }
      }

      String trimmedText = sb.toString();

      if (trimmedText.length() > MAX_CHARS) {
        System.out.println("Too many characters in QR code!");
        return null;
      } else if (trimmedText.length() == 0) {
        System.out.println("No non-whitespace characters in input!");
        return null;
      }

      try {
        if (storeImage) {
          return generateQRCodeToPath(trimmedText, IMAGE_SIZE, IMAGE_SIZE);
        } else {
          return generateQRCodeToStream(trimmedText, IMAGE_SIZE, IMAGE_SIZE);
        }
      } catch (WriterException e) {
        System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
      } catch (IOException e) {
        System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
      }
    } else {
      System.out.println("String list was null!");
    }
    return null;
  }

  // Generate QR code from string, storing the result and loading it into a JavaFX Image
  private Image generateQRCodeToPath(String text, int width, int height)
      throws WriterException, IOException {

    try {
      URL res = getClass().getClassLoader().getResource(IMAGE_PATH);
      Path tempImagePath = Paths.get(res.toURI());
      BitMatrix bm = encodeToBitMatrix(text, width, height);
      MatrixToImageWriter.writeToPath(bm, "PNG", tempImagePath);
      System.out.println(res.toString());
      Image imageToReturn = new Image(res.toString());
      return imageToReturn;
    } catch (URISyntaxException e) {
      System.out.println("Couldn't get qrTemp.png!");
      return null;
    } catch (NullPointerException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Generate QR code from string, loading the result into a JavaFX Image using byte array stream
  private Image generateQRCodeToStream(String text, int width, int height)
      throws WriterException, IOException {

    BitMatrix bm = encodeToBitMatrix(text, width, height);

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

    MatrixToImageWriter.writeToStream(bm, "PNG", pngOutputStream);

    byte[] pngData = pngOutputStream.toByteArray();

    return new Image(new ByteArrayInputStream(pngData));
  }

  // Generate QR code from string as a Bit Matrix (common functionality for both above methods)
  private BitMatrix encodeToBitMatrix(String text, int width, int height)
      throws WriterException, IOException {

    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
    return bitMatrix;
  }
}
