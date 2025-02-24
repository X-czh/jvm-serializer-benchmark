package com.github.xczh;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestMapPojo {
  public Map<String, String> m;

  public static TestMapPojo getInstance(int size) {
    TestMapPojo pojo = new TestMapPojo();
    pojo.m = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      pojo.m.put("abcdefg" + i, i + "opqrstuvwxyz测试");
    }
    return pojo;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestMapPojo testPoJo = (TestMapPojo) o;
    return Objects.equals(m, testPoJo.m);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m);
  }
}
