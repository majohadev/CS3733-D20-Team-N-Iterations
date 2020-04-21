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
  protected WebcamPanel panel;

  private final int THREAD_SLEEP_DELAY = 100; // Thread sleep duration in milliseconds
  private final int TIMEOUT_THRESHOLD = 60; // Timeout threshold in seconds
  private final int MAX_CYCLES =
      TIMEOUT_THRESHOLD
          * 1000
          / THREAD_SLEEP_DELAY; // # of times reader thread should run before timing out
  protected final String ERR_CODE = "NO_CODE";

  private Thread currentThread;
  private boolean blockTimeOut = false;

  // Set up thread to fetch QR code
  private Task<String> getQRCode =
      new Task<String>() {

        @Override
        public String call() {

          System.out.println("Starting!");

          int cycles = 0;
          String result = ERR_CODE;

          // Continue while:
          // - Thread is not cancelled
          // - Timeout hasn't been reached (if timeout is used)
          // - result == ERR_CODE

          do {

            try {
              Thread.sleep(100);
              if (!blockTimeOut) {
                cycles++;
              }
            } catch (InterruptedException interrupted) {
              if (isCancelled()) {
                updateMessage("Cancelled");
                break;
              }
            }

            result = fetchQRCode();

            if (result != ERR_CODE) {
              break;
            }

            if (isCancelled()) {
              updateMessage("Cancelled");
              break;
            }

          } while (cycles < MAX_CYCLES);
          return result;
        }

        @Override
        protected void succeeded() {
          super.succeeded();
          updateMessage("Scanning thread complete!");
          if (getValue() == ERR_CODE) {
            onScanFail();
          } else {
            onScanSucceed(getValue());
          }
        }

        @Override
        protected void cancelled() {
          super.cancelled();
          updateMessage("Scanning thread cancelled!");
          onScanFail();
        }

        @Override
        protected void failed() {
          super.failed();
          updateMessage("Scanning thread failed!");
          onScanFail();
        }
      };

  public QRReader() {

    Dimension size = WebcamResolution.QVGA.getSize();

    webcam = Webcam.getWebcams().get(0);
    webcam.setViewSize(size);

    panel = new WebcamPanel(webcam);
    panel.setPreferredSize(size);
    panel.pause();
    panel.setVisible(false);
  }

  public void cancelScan() {
    if (currentThread != null && getQRCode.isRunning()) {
      getQRCode.cancel();
    }
  }

  // Begin looking for QR codes
  public void startScan(boolean blockTimeOut) {
    this.blockTimeOut = blockTimeOut;
    currentThread = new Thread(getQRCode);
    currentThread.setDaemon(true); // Make this thread low priority
    currentThread.start(); // Begin thread
  }

  // Override these methods in UI controller
  protected void onScanSucceed(String result) {}

  protected void onScanFail() {}

  // Attempts to get a QR code
  private String fetchQRCode() {

    Result result = null;
    BufferedImage image;

    if (webcam.isOpen()) {

      if ((image = webcam.getImage()) == null) {
        return ERR_CODE;
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
    return ERR_CODE;
  }
}
