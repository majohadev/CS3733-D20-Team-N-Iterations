package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.views.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MapLocationSearchController implements Controller {
  App mainApp;

  @FXML TextField txt_firstLocation;
  @FXML TextField txt_secondLocation;
  @FXML ListView lst_fuzzySearch;

  TextField activeText;

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void onSearchLocation(KeyEvent e) throws DBException {
    activeText = (TextField) e.getSource();
    NewMapDisplayController.fuzzyLocationSearch(activeText, lst_fuzzySearch);
  }

  public void onItemSelected(MouseEvent e) {
    try {
      ListView lst = (ListView) e.getSource();
      activeText.setText(lst.getSelectionModel().getSelectedItem().toString());
    } catch (NullPointerException ex) {
      return;
    }
  }

  public void onSearchButtonClicked(MouseEvent e) {

  }
}
