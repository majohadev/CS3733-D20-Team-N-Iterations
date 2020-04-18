package edu.wpi.N.qrcontrol;

/*
Original project:
https://github.com/sarxos/webcam-capture/tree/master/webcam-capture-examples/webcam-capture-qrcode
webcam-capture repo by sarxos, example project using ZXing barcode library
*/

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.Dimension;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class QRBase implements Runnable, ThreadFactory {

    private static final long serialVersionUID = 6441489157408381878L;

    private Executor executor = Executors.newSingleThreadExecutor(this);

    private Webcam webcam;
    private WebcamPanel panel;
    private String lastScanned = "";
    private int maxReaderCycles;  // Number of times reader thread should run before timing out
    private int threadSleepDelay = 100;  // Thread sleep duration in milliseconds

    public QRBase() {
        super();

        Dimension size = WebcamResolution.QVGA.getSize();

        webcam = Webcam.getWebcams().get(0);
        webcam.setViewSize(size);

        panel = new WebcamPanel(webcam);
        panel.setPreferredSize(size);
        //panel.setFPSDisplayed(true);

    }

    public WebcamPanel getWebcamView () {
        return panel;
    }

    public String startScan(int timeout) {
        maxReaderCycles = timeout/threadSleepDelay;
        executor.execute(this);
        return lastScanned;
    }

    public String startScan() {
        maxReaderCycles = 900000/threadSleepDelay; // 900000 ms = 15 minutes
        executor.execute(this);
        return lastScanned;
    }

    @Override
    public void run() {

        Result result = null;
        BufferedImage image;
        int cycles = 0;
        lastScanned = "";

        do {
            try {
                Thread.sleep(threadSleepDelay);
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
                    // fall thru, it means there is no QR code in image
                }
            }

            if (result != null) {
                lastScanned = result.getText();  // Exit if code is scanned
                return;
            }

        } while (cycles < maxReaderCycles);  // Exit if timed out
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "example-runner");
        t.setDaemon(true);
        return t;
    }

}
