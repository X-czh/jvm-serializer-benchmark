package com.github.xczh;

import com.github.xczh.fury.FurySerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.NullableMapTypeInfo;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputSerializer;
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

@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Threads(1)
@Fork(value = 1)
@State(Scope.Benchmark)
public class MapBenchmark {

  @State(Scope.Benchmark)
  public static class BenchmarkState {
    @Param({"10", "100", "1000", "10000"})
    public int mapSize;

    public Map<String, String> testMap;
    public DataOutputSerializer out;

    @Setup(Level.Trial)
    public void setUp() {
      testMap = getTestMap(mapSize);
    }

    @Setup(Level.Invocation)
    public void setUpPerInvocation() {
      out = new DataOutputSerializer(128);
    }
  }

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

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }

  @Benchmark
  public void testFury(BenchmarkState state) throws IOException {
    furySerializer.serialize(state.testMap, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    furySerializer.deserialize(in);
  }

  @Benchmark
  public void testFlinkPojo(BenchmarkState state) throws IOException {
    flinkPojoSerializer.serialize(state.testMap, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    flinkPojoSerializer.deserialize(in);
  }

  @Benchmark
  public void testKryo(BenchmarkState state) throws IOException {
    kryoSerializer.serialize(state.testMap, state.out);
    DataInputView in = new DataInputDeserializer(state.out.wrapAsByteBuffer());
    kryoSerializer.deserialize(in);
  }

  public static Map<String, String> getTestMap(int size) {
    Map<String, String> map = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      map.put("abcdefg" + i, i + "uvwxyz@123456.com");
    }
    return map;
  }
}
