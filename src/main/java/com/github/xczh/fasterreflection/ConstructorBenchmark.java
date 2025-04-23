package com.github.xczh.fasterreflection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Threads(1)
@Fork(value = 1)
@State(Scope.Benchmark)
public class ConstructorBenchmark {

  private Class<MyPojo> clazz;
  private Constructor<MyPojo> constructor;
  private MethodHandle constructorMethodHandle;

  @Setup()
  public void setup() throws Exception {
    clazz = MyPojo.class;
    constructor = clazz.getDeclaredConstructor();
    constructor.setAccessible(true);
    constructorMethodHandle = MethodHandles.lookup().findConstructor(clazz, MethodType.methodType(void.class));
  }

  @Benchmark
  public void directConstruct() {
    new MyPojo();
  }

  @Benchmark
  public void classNewInstance() throws Exception {
    clazz.newInstance();
  }

  @Benchmark
  public void constructorNewInstance() throws Exception {
    constructor.newInstance();
  }

  @Benchmark
  public void constructorMethodHandleNewInstance() throws Throwable {
    constructorMethodHandle.invoke();
  }
}
