package com.pubfinder.pubfinder.models.enums;

public enum Volume {
  QUITE, PLEASANT, AVERAGE, LOUD, VERY_LOUD;

  public int getOrdinal() {
    return this.ordinal();
  }

  public static Volume fromValue(Integer volume) {
    if (volume == null) {
      return null;
    }

    if (0 <= volume && volume <= 20) {
      return Volume.QUITE;
    } else if (20 < volume && volume <= 40) {
      return Volume.PLEASANT;
    } else if (40 < volume && volume <= 60) {
      return Volume.AVERAGE;
    } else if (60 < volume && volume <= 80) {
      return Volume.LOUD;
    } else {
      return Volume.VERY_LOUD;
    }
  }
}
