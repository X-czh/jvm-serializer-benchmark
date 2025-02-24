package com.github.xczh;

import com.github.xczh.fury.FurySerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
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
    @Param({"10", "100", "1000"})
    public int mapSize;

    public Map<String, String> testMap;

    @Setup(Level.Trial)
    public void setUp() {
      testMap = getTestMap(mapSize);
    }
  }

  @SuppressWarnings("unchecked")
  public TypeSerializer<Map<String, String>> furySerializer = new FurySerializer<>((Class<Map<String, String>>) (Class<?>)Map.class);
  public TypeSerializer<Map<String, String>> flinkPojoSerializer =
          new MapTypeInfo<>(String.class, String.class).createSerializer(new SerializerConfigImpl());
  @SuppressWarnings("unchecked")
  public TypeSerializer<Map<String, String>> kryoSerializer =
      new KryoSerializer<>((Class<Map<String, String>>) (Class<?>)Map.class, new SerializerConfigImpl());

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }

  @Setup(Level.Trial)
  public void setUp() {

  }

  @Benchmark
  public void testFury(BenchmarkState state) throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    furySerializer.serialize(state.testMap, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    furySerializer.deserialize(in);
  }

  @Benchmark
  public void testFlinkPojo(BenchmarkState state) throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    flinkPojoSerializer.serialize(state.testMap, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    flinkPojoSerializer.deserialize(in);
  }

  @Benchmark
  public void testKryo(BenchmarkState state) throws IOException {
    DataOutputSerializer out = new DataOutputSerializer(64);
    kryoSerializer.serialize(state.testMap, out);
    DataInputView in = new DataInputDeserializer(out.wrapAsByteBuffer());
    kryoSerializer.deserialize(in);
  }

  private static Map<String, String> getTestMap(int size) {
    Map<String, String> map = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      map.put("abcdefg" + i, i + "opqrstuvwxyz测试");
    }
    return map;
  }
}
