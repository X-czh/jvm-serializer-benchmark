package com.github.xczh;

import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputSerializer;

import com.github.xczh.fury.FurySerializer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.util.Map;

@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Threads(1)
@Fork(value = 1)
@State(Scope.Benchmark)
public class NestedMapPojoBenchmark {

  @State(Scope.Benchmark)
  public static class BenchmarkState {
    @Param({"10", "100", "1000"})
    public int mapSize;

    public TestMapPojo testPojo;

    @Setup(Level.Trial)
    public void setUp() {
      testPojo = TestMapPojo.getInstance(mapSize);
    }
  }

  public TypeSerializer<TestMapPojo> furySerializer = new FurySerializer<>(TestMapPojo.class);
  public TypeSerializer<TestMapPojo> flinkPojoSerializer =
      TypeExtractor.createTypeInfo(TestMapPojo.class)
          .createSerializer(new SerializerConfigImpl());
  public TypeSerializer<TestMapPojo> kryoSerializer =
      new KryoSerializer<>(TestMapPojo.class, new SerializerConfigImpl());

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }

  @Benchmark
  public void testFury(BenchmarkState state) throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    furySerializer.serialize(state.testPojo, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    furySerializer.deserialize(in);
  }

  @Benchmark
  public void testFlinkPojo(BenchmarkState state) throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    flinkPojoSerializer.serialize(state.testPojo, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    flinkPojoSerializer.deserialize(in);
  }

  @Benchmark
  public void testKryo(BenchmarkState state) throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    kryoSerializer.serialize(state.testPojo, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    kryoSerializer.deserialize(in);
  }
}
