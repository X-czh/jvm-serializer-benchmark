package com.github.xczh;

import com.github.xczh.fury.FurySerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.NullableListTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputSerializer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Threads(1)
@Fork(value = 1)
@State(Scope.Benchmark)
public class PojoBatchBenchmark {
  public static final TestPojo POJO = TestPojo.getInstance();
  public static final List<TestPojo> BATCH;
  public static final int SIZE = 128;

  static {
    BATCH = new ArrayList<>(SIZE);
    for (int i = 0; i < SIZE; i++) {
      BATCH.add(TestPojo.getInstance());
    }
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    public DataOutputSerializer out;

    @Setup(Level.Invocation)
    public void setUpPerInvocation() {
      out = new DataOutputSerializer(128);
    }
  }

  public TypeSerializer<TestPojo> flinkPojoSerializer =
      TypeExtractor.createTypeInfo(TestPojo.class).createSerializer(new SerializerConfigImpl());

  public TypeSerializer<List<TestPojo>> flinkListSerializer =
      new NullableListTypeInfo<>(TestPojo.class).createSerializer(new SerializerConfigImpl());

  @SuppressWarnings("unchecked")
  public TypeSerializer<List<TestPojo>> furySerializer =
      new FurySerializer<>((Class<List<TestPojo>>) (Class<?>) List.class);

  @SuppressWarnings("unchecked")
  public TypeSerializer<List<TestPojo>> kryoSerializer =
      new KryoSerializer<>(
          (Class<List<TestPojo>>) (Class<?>) List.class, new SerializerConfigImpl());

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }

  @Benchmark
  public void testFlinkPojoLoop(BenchmarkState state) throws IOException {
    for (int i = 0; i < SIZE; i++) {
      flinkPojoSerializer.serialize(POJO, state.out);
    }
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    for (int i = 0; i < SIZE; i++) {
      flinkPojoSerializer.deserialize(in);
    }
  }

  @Benchmark
  public void testFlinkPojoList(BenchmarkState state) throws IOException {
    flinkListSerializer.serialize(BATCH, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    flinkListSerializer.deserialize(in);
  }

  @Benchmark
  public void testFury(BenchmarkState state) throws IOException {
    furySerializer.serialize(BATCH, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    furySerializer.deserialize(in);
  }

  @Benchmark
  public void testKryo(BenchmarkState state) throws IOException {
    kryoSerializer.serialize(BATCH, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    kryoSerializer.deserialize(in);
  }
}
