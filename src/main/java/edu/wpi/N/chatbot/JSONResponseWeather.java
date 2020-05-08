package edu.wpi.N.chatbot;

import java.util.List;
import java.util.Map;

public class JSONResponseWeather {

  private Map<String, Double> main;
  private List<WeatherProperty> weather;

  public void setMain(Map<String, Double> visibility) {
    this.main = visibility;
  }

  public Map<String, Double> getMain() {
    return this.main;
  }

  public void setWeather(List<WeatherProperty> weather) {
    this.weather = weather;
  }

  public List<WeatherProperty> getWeather() {
    return this.weather;
  }
}
