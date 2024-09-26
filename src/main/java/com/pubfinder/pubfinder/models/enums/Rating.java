package com.pubfinder.pubfinder.models.enums;

public enum Rating {
  HALF(0.5),
  ONE(1),
  ONE_AND_HALF(1.5),
  TWO(2),
  TWO_AND_HALF(2.5),
  THREE(3),
  THREE_AND_HALF(3.5),
  FOUR(4),
  FOUR_AND_HALF(4.5),
  FIVE(5);

  private final double value;

  Rating(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }
}
