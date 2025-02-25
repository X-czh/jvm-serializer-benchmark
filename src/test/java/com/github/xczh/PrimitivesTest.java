package com.github.xczh;

import com.github.xczh.fury.FurySerializer;
import java.io.IOException;
import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.junit.jupiter.api.Test;

class PrimitivesTest {
  public TypeSerializer<Integer> furySerializer = new FurySerializer<>(Integer.class);
  public TypeSerializer<Integer> flinkPojoSerializer =
      TypeExtractor.createTypeInfo(Integer.class).createSerializer(new SerializerConfigImpl());
  public TypeSerializer<Integer> kryoSerializer =
      new KryoSerializer<>(Integer.class, new SerializerConfigImpl());

  @Test
  void test() throws IOException {
    int[] arr = new int[] {1, 128, 10000, Integer.MAX_VALUE};
    for (int i : arr) {
      System.out.println(i);

      DataOutputSerializer out1 = new DataOutputSerializer(8);
      furySerializer.serialize(i, out1);
      System.out.println("Fury:" + out1.getCopyOfBuffer().length);

      DataOutputSerializer out2 = new DataOutputSerializer(8);
      flinkPojoSerializer.serialize(i, out2);
      System.out.println("Flink:" + out2.getCopyOfBuffer().length);

      DataOutputSerializer out3 = new DataOutputSerializer(8);
      kryoSerializer.serialize(i, out3);
      System.out.println("Kryo:" + out3.getCopyOfBuffer().length);
    }
  }
}
