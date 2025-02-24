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
public class PojoBenchmark {
  public static final TestPojo POJO = TestPojo.getInstance();

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    public DataOutputSerializer out;

    @Setup(Level.Invocation)
    public void setUpPerInvocation() {
      out = new DataOutputSerializer(128);
    }
  }

  public TypeSerializer<TestPojo> furySerializer = new FurySerializer<>(TestPojo.class);
  public TypeSerializer<TestPojo> flinkPojoSerializer =
      TypeExtractor.createTypeInfo(TestPojo.class).createSerializer(new SerializerConfigImpl());
  public TypeSerializer<TestPojo> kryoSerializer =
      new KryoSerializer<>(TestPojo.class, new SerializerConfigImpl());

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }

  @Benchmark
  public void testFury(BenchmarkState state) throws IOException {
    furySerializer.serialize(POJO, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    furySerializer.deserialize(in);
  }

  @Benchmark
  public void testFlinkPojo(BenchmarkState state) throws IOException {
    flinkPojoSerializer.serialize(POJO, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    flinkPojoSerializer.deserialize(in);
  }

  @Benchmark
  public void testKryo(BenchmarkState state) throws IOException {
    kryoSerializer.serialize(POJO, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    kryoSerializer.deserialize(in);
  }
}
