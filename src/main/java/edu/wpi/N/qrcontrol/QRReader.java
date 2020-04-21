package edu.wpi.N.qrcontrol;

/*
Original project:
https://github.com/sarxos/webcam-capture/tree/master/webcam-capture-examples/webcam-capture-qrcode
webcam-capture repo by sarxos, example project using ZXing barcode library

Modified to fit JavaFX library
*/

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javafx.concurrent.Task;

public abstract class QRReader {

  /*
  Every UI controller that uses QR reading functions should EXTEND this class!
  Also need to:
  -override onScanSucceed
  -override onScanFail
  -place WebcamPanel panel in SwingNode (since WebcamPanel extends JPanel)
   */

  private Webcam webcam;
  private WebcamPanel panel;

  private final int THREAD_SLEEP_DELAY = 100; // Thread sleep duration in milliseconds
  private final int TIMEOUT_THRESHOLD = 60; // Timeout threshold in seconds
  private final int MAX_CYCLES =
      TIMEOUT_THRESHOLD
          * 1000
          / THREAD_SLEEP_DELAY; // # of times reader thread should run before timing out
  protected final String ERR_CODE = "NO_CODE";

  public QRReader() {

    Dimension size = WebcamResolution.QVGA.getSize();

    webcam = Webcam.getWebcams().get(0);
    webcam.setViewSize(size);

    panel = new WebcamPanel(webcam);
    panel.setPreferredSize(size);
    panel.pause();
    panel.setVisible(false);
  }

  protected WebcamPanel getWebcamView() {
    return panel;
  }

  protected void startScan() {

    // Set up thread to fetch QR code
    Task<String> getQRCode =
        new Task<String>() {

          @Override
          public String call() {

            // Open camera
            panel.resume();
            panel.setVisible(true);

            // Look for code
            String result = fetchQRCode();

            // Close camera
            panel.pause();
            panel.setVisible(false);

            return result;
          }
        };

    getQRCode.setOnSucceeded(
        e -> {
          String result = getQRCode.getValue();
          if (result == ERR_CODE) {
            onScanFail();
          } else {
            onScanSucceed(result);
          }
        });

    getQRCode.setOnCancelled(
        e -> {
          onScanFail();
        });

    getQRCode.setOnFailed(
        e -> {
          onScanFail();
        });

    Thread t = new Thread(getQRCode);
    t.setDaemon(true); // Make this thread low priority
    t.start(); // Begin thread
  }

  // Override these methods in UI controller
  protected void onScanSucceed(String result) {}

  protected void onScanFail() {}

  // Attempts to get a QR code until timeout is reached
  protected String fetchQRCode() {

    Result result = null;
    BufferedImage image;
    int cycles = 0;

    do {
      try {
        Thread.sleep(THREAD_SLEEP_DELAY);
        cycles++;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (webcam.isOpen()) {

        if ((image = webcam.getImage()) == null) {
          continue;
        }

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
          result = new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) {
          // No QR code in image
        }
      }

      if (result != null) {
        return result.getText(); // Exit if code is scanned
      }

    } while (cycles < MAX_CYCLES); // Exit if timed out
    return ERR_CODE;
  }
}
