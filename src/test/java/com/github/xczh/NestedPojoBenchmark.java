package com.github.xczh;

import com.github.xczh.fury.FurySerializer;
import java.io.IOException;
import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Threads(1)
@Fork(value = 1)
@State(Scope.Benchmark)
public class NestedPojoBenchmark {
  public static final TestNestedPojo POJO = TestNestedPojo.getInstance();

  public TypeSerializer<TestNestedPojo> furySerializer = new FurySerializer<>(TestNestedPojo.class);
  public TypeSerializer<TestNestedPojo> flinkPojoSerializer =
      TypeExtractor.createTypeInfo(TestNestedPojo.class)
          .createSerializer(new SerializerConfigImpl());
  public TypeSerializer<TestNestedPojo> kryoSerializer =
      new KryoSerializer<>(TestNestedPojo.class, new SerializerConfigImpl());

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }

  @Benchmark
  public void testFury() throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    furySerializer.serialize(POJO, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    furySerializer.deserialize(in);
  }

  @Benchmark
  public void testFlinkPojo() throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    flinkPojoSerializer.serialize(POJO, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    flinkPojoSerializer.deserialize(in);
  }

  @Benchmark
  public void testKryo() throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    kryoSerializer.serialize(POJO, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    kryoSerializer.deserialize(in);
  }
}
