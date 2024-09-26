package com.pubfinder.pubfinder.models.enums;

public enum Volume {
  QUITE, PLEASANT, AVERAGE, LOUD, VERY_LOUD;

  public int getOrdinal() {
    return this.ordinal();
  }
}
