package com.github.xczh;

import java.util.Objects;

public class TestNestedInnerPojo {
  public TestPojo p1;
  public TestPojo p2;

  public static TestNestedInnerPojo getInstance() {
    TestNestedInnerPojo pojo = new TestNestedInnerPojo();
    pojo.p1 = TestPojo.getInstance();
    pojo.p2 = TestPojo.getInstance();
    return pojo;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestNestedInnerPojo testPoJo = (TestNestedInnerPojo) o;
    return Objects.equals(p1, testPoJo.p1) && Objects.equals(p2, testPoJo.p2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(p1, p2);
  }
}
