package edu.wpi.N.entities;

public class Flower {
  public String getFlowerName() {
    return flowerName;
  }

  public void setFlowerName(String flowerName) {
    this.flowerName = flowerName;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  String flowerName;
  int price;

  public Flower(String name, int price) {
    this.flowerName = name;
    this.price =
        price; // can convert it using "$" + price/100 + String.format(".%02d", price % 100)
  }

  public double getPrice() {
    return price;
  }
}
