package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.views.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class MapLocationSearchController implements Controller {
  App mainApp;

  @FXML TextField txt_firstLocation;
  @FXML TextField txt_secondLocation;
  @FXML ListView lst_fuzzySearch;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onSearchLocation(KeyEvent e) throws DBException {
    NewMapDisplayController.fuzzyLocationSearch((TextField) e.getSource(), lst_fuzzySearch);
  }
}
