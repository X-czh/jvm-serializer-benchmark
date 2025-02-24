package com.github.xczh;

import java.util.Objects;

public class TestPojo {
  public int i1;
  public int i2;
  public long l3;
  public long l4;
  public boolean b5;
  public boolean b6;
  public float f7;
  public float f8;
  public double d9;
  public double d10;
  public String s11;
  public String s12;

  public static TestPojo getInstance() {
    TestPojo pojo = new TestPojo();
    pojo.i1 = 1;
    pojo.i2 = 12345678;
    pojo.l3 = 123456789L;
    pojo.l4 = Long.MAX_VALUE;
    pojo.b5 = true;
    pojo.b6 = false;
    pojo.f7 = 1.0f;
    pojo.f8 = 12345.6789f;
    pojo.d9 = 3.5f;
    pojo.d10 = 123456789.12345f;
    pojo.s11 = "abcdef";
    pojo.s12 = "abcdef测试123456";
    return pojo;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestPojo testPoJo = (TestPojo) o;
    return i1 == testPoJo.i1
        && i2 == testPoJo.i2
        && l3 == testPoJo.l3
        && l4 == testPoJo.l4
        && b5 == testPoJo.b5
        && b6 == testPoJo.b6
        && Float.compare(f7, testPoJo.f7) == 0
        && Float.compare(f8, testPoJo.f8) == 0
        && Double.compare(d9, testPoJo.d9) == 0
        && Double.compare(d10, testPoJo.d10) == 0
        && Objects.equals(s11, testPoJo.s11)
        && Objects.equals(s12, testPoJo.s12);
  }

  @Override
  public int hashCode() {
    return Objects.hash(i1, i2, l3, l4, b5, b6, f7, f8, d9, d10, s11, s12);
  }
}
