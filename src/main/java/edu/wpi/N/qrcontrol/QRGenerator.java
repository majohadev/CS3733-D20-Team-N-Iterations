package edu.wpi.N.qrcontrol;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;


/*
  Basic QR code generation based on ZXing tutorial:
    https://www.callicoder.com/generate-qr-code-in-java-using-zxing/

  Every UI controller that uses QR generating functions should EXTEND this class!

 */

public abstract class QRGenerator {

    // Path to generate QR code image to
    private final String QR_CODE_IMAGE_PATH = "./MyQRCode.png";
    // Number of characters after which QR codes get unreasonably large
    private final int MAX_CHARS = 500;
    // How large to make the exported image
    private final int IMAGE_SIZE = 350;

    // Generate QR code as JavaFX Image using concatenated list of strings, optionally storing the result
    protected Image generateImage(ArrayList<String> linesToEncode, boolean storeImage) {

        StringBuilder sb = new StringBuilder();

        for (String s : linesToEncode)
        {
            sb.append(s);
            sb.append("\n");
        }

        if (sb.length() > MAX_CHARS) {
            System.out.println("Too many characters in QR code!");
            return null;
        }

        try {
            if (storeImage) {
                return generateQRCodeToPath(sb.toString(), IMAGE_SIZE, IMAGE_SIZE);
            } else {
                return generateQRCodeToStream(sb.toString(), IMAGE_SIZE, IMAGE_SIZE);
            }
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }
        return null;
    }

    // Generate QR code from string, storing the result and loading it into a JavaFX Image
    private Image generateQRCodeToPath(String text, int width, int height)
            throws WriterException, IOException {

        BitMatrix bm = encodeToBitMatrix(text, width, height);
        Path path = FileSystems.getDefault().getPath(QR_CODE_IMAGE_PATH);
        MatrixToImageWriter.writeToPath(bm, "PNG", path);
        return new Image(path.toString());
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
