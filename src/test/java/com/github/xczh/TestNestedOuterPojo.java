package com.github.xczh;

import java.util.Objects;

public class TestNestedOuterPojo {
  public TestNestedInnerPojo i1;
  public TestNestedInnerPojo i2;
  public TestNestedInnerPojo i3;
  public TestNestedInnerPojo i4;

  public static TestNestedOuterPojo getInstance() {
    TestNestedOuterPojo pojo = new TestNestedOuterPojo();
    pojo.i1 = new TestNestedInnerPojo();
    pojo.i2 = new TestNestedInnerPojo();
    pojo.i3 = new TestNestedInnerPojo();
    pojo.i4 = new TestNestedInnerPojo();
    return pojo;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestNestedOuterPojo that = (TestNestedOuterPojo) o;
    return Objects.equals(i1, that.i1)
        && Objects.equals(i2, that.i2)
        && Objects.equals(i3, that.i3)
        && Objects.equals(i4, that.i4);
  }

  @Override
  public int hashCode() {
    return Objects.hash(i1, i2, i3, i4);
  }
}
