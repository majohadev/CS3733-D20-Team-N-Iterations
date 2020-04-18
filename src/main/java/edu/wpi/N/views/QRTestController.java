package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.qrcontrol.QRBase;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.swing.*;

public class QRTestController {

    @FXML
    JFXButton btn_scanButton;
    @FXML
    Label lbl_output;
    @FXML
    SwingNode pane_swingNode;

    private QRBase qrBase;

    @FXML
    public void initialize () {
        qrBase = new QRBase();
        createAndSetSwingContent(pane_swingNode);
    }

    @FXML
    public void onScanClicked() {

        if (qrBase != null) {
            String dataOut = qrBase.startScan(); // Start scan with default timeout
            if (dataOut != "") {
                lbl_output.setText("Key: " + dataOut);
                onScanSuccess(dataOut);
            } else lbl_output.setText("No code found");
        } else {
            lbl_output.setText("QR reader not initialized");
        }
    }

    private void onScanSuccess(String key) {
        System.out.println("Scan successful! Key: " + key);
    }

    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(qrBase.getWebcamView());
            }
        });
    }

}
