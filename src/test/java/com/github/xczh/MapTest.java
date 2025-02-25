package com.github.xczh;

import com.github.xczh.fury.FurySerializer;
import java.io.IOException;
import java.util.Map;
import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.NullableMapTypeInfo;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.junit.jupiter.api.Test;

class MapTest {
  @SuppressWarnings("unchecked")
  public TypeSerializer<Map<String, String>> furySerializer =
      new FurySerializer<>((Class<Map<String, String>>) (Class<?>) Map.class);

  public TypeSerializer<Map<String, String>> flinkPojoSerializer =
      new NullableMapTypeInfo<>(String.class, String.class)
          .createSerializer(new SerializerConfigImpl());

  @SuppressWarnings("unchecked")
  public TypeSerializer<Map<String, String>> kryoSerializer =
      new KryoSerializer<>(
          (Class<Map<String, String>>) (Class<?>) Map.class, new SerializerConfigImpl());

  @Test
  void test() throws IOException {
    int[] arr = new int[] {10, 100, 1000, 10000};
    for (int i : arr) {
      System.out.println(i);
      Map<String, String> map = MapBenchmark.getTestMap(i);

      DataOutputSerializer out1 = new DataOutputSerializer(128);
      furySerializer.serialize(map, out1);
      System.out.println("Fury:" + out1.getCopyOfBuffer().length);

      DataOutputSerializer out2 = new DataOutputSerializer(128);
      flinkPojoSerializer.serialize(map, out2);
      System.out.println("Flink:" + out2.getCopyOfBuffer().length);

      DataOutputSerializer out3 = new DataOutputSerializer(128);
      kryoSerializer.serialize(map, out3);
      System.out.println("Kryo:" + out3.getCopyOfBuffer().length);
    }
  }
}
