package edu.wpi.N.views.mapDisplay;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class GoogleMapController
    implements Controller,
        MapComponentInitializedListener,
        DirectionsServiceCallback,
        Initializable {
  private StateSingleton singleton;
  private App mainApp;

  protected DirectionsService directionsService;
  protected DirectionsPane directionsPane;

  protected StringProperty from = new SimpleStringProperty();
  protected StringProperty to = new SimpleStringProperty();

  @FXML
  protected GoogleMapView mapView =
      new GoogleMapView("en-US", "AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q");

  @FXML protected TextField fromTextField;

  @FXML protected TextField toTextField;

  public GoogleMapController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @FXML
  private void toTextFieldAction(ActionEvent event) {
    DirectionsRequest request = new DirectionsRequest(from.get(), to.get(), TravelModes.DRIVING);
    directionsService.getRoute(
        request, this, new DirectionsRenderer(true, mapView.getMap(), directionsPane));
  }

  @Override
  public void directionsReceived(DirectionsResult results, DirectionStatus status) {}

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    mapView.addMapInializedListener(this);
    to.bindBidirectional(toTextField.textProperty());
    from.bindBidirectional(fromTextField.textProperty());
  }

  @Override
  public void mapInitialized() {
    MapOptions options = new MapOptions();

    options
        .center(new LatLong(47.606189, -122.335842))
        .zoomControl(true)
        .zoom(12)
        .overviewMapControl(false)
        .mapType(MapTypeIdEnum.ROADMAP);
    GoogleMap map = mapView.createMap(options);
    directionsService = new DirectionsService();
    directionsPane = mapView.getDirec();
  }

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
