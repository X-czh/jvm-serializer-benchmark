package com.github.xczh;

import com.github.xczh.fury.FurySerializer;
import java.io.IOException;
import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.junit.jupiter.api.Test;

class PojoTest {
  public static final TestPojo POJO = TestPojo.getInstance();

  public TypeSerializer<TestPojo> furySerializer = new FurySerializer<>(TestPojo.class);
  public TypeSerializer<TestPojo> flinkPojoSerializer =
      TypeExtractor.createTypeInfo(TestPojo.class).createSerializer(new SerializerConfigImpl());
  public TypeSerializer<TestPojo> kryoSerializer =
      new KryoSerializer<>(TestPojo.class, new SerializerConfigImpl());

  @Test
  void test() throws IOException {
    DataOutputSerializer out1 = new DataOutputSerializer(128);
    furySerializer.serialize(POJO, out1);
    System.out.println("Fury:" + out1.getCopyOfBuffer().length);

    DataOutputSerializer out2 = new DataOutputSerializer(128);
    flinkPojoSerializer.serialize(POJO, out2);
    System.out.println("Flink:" + out2.getCopyOfBuffer().length);

    DataOutputSerializer out3 = new DataOutputSerializer(128);
    kryoSerializer.serialize(POJO, out3);
    System.out.println("Kryo:" + out3.getCopyOfBuffer().length);
  }
}
