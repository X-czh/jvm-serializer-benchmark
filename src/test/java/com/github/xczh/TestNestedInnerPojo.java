package com.github.xczh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestNestedInnerPojo {
  public int i1;
  public int i2;
  public boolean b3;
  public boolean b4;
  public Map<String, String> m5;
  public List<Long> l6;

  public static TestNestedInnerPojo getInstance() {
    TestNestedInnerPojo pojo = new TestNestedInnerPojo();
    pojo.i1 = 1;
    pojo.i2 = 12345678;
    pojo.b3 = true;
    pojo.b4 = false;
    pojo.m5 = new HashMap<>(20);
    for (int i = 1; i < 20; i++) {
      pojo.m5.put("key" + i, "value" + i);
    }
    pojo.l6 = new ArrayList<>(20);
    for (int i = 1; i < 20; i++) {
      pojo.l6.add(Long.MAX_VALUE);
    }
    return pojo;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestNestedInnerPojo testPoJo = (TestNestedInnerPojo) o;
    return i1 == testPoJo.i1
        && i2 == testPoJo.i2
        && b3 == testPoJo.b3
        && b4 == testPoJo.b4
        && Objects.equals(m5, testPoJo.m5)
        && Objects.equals(l6, testPoJo.l6);
  }

  @Override
  public int hashCode() {
    return Objects.hash(i1, i2, b3, b4, m5, l6);
  }
}
