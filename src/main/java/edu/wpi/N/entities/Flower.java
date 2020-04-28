package edu.wpi.N.entities;

public class Flower {

  String flowerName;
  int price;

  public Flower(String name, int price) {
    this.flowerName = name;
    this.price =
        price; // can convert it using "$" + price/100 + String.format(".%02d", price % 100)
  }

  public String getFlowerName() {
    return flowerName;
  }

  public int getPrice() {
    return price;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Flower)) {
      return false;
    }

    Flower other = (Flower) obj;

    return flowerName.equals(other.flowerName) && price == other.price;
  }
}
