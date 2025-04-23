package com.github.xczh.fasterreflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
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
public class GetterBenchmark {

  private MyPojo myPojo;
  private Field field;
  private Method method;
  private MethodHandle getterMethodHandle;
  private long fieldOffset;
  private UnsafeFieldAccessor accessor;
  private Function<Object, Object> function;

  @Setup()
  public void setup() throws Exception {
    myPojo = new MyPojo();
    myPojo.setS("s");
    field = MyPojo.class.getDeclaredField("s");
    field.setAccessible(true);
    method = MyPojo.class.getDeclaredMethod("getS");
    method.setAccessible(true);
    getterMethodHandle = MethodHandles.lookup().unreflectGetter(field);
    function = LambdaFactory.produceGetter(field);
    fieldOffset = UnsafeUtils.getClassFieldOffset(field);
    accessor = new UnsafeFieldAccessor(field);
  }

  @Benchmark
  public void directGet() {
    myPojo.getS();
  }

  @Benchmark
  public void fieldReflectionGet() throws IllegalAccessException {
    field.get(myPojo);
  }

  @Benchmark
  public void methodReflectionGet() throws InvocationTargetException, IllegalAccessException {
    method.invoke(myPojo);
  }

  @Benchmark
  public void methodHandleGet() throws Throwable {
    getterMethodHandle.invoke(myPojo);
  }

  @Benchmark
  public void lambdaGet() {
    function.apply(myPojo);
  }

  @Benchmark
  public void directUnsafeGet() {
    UnsafeUtils.UNSAFE.getObject(myPojo, fieldOffset);
  }

  @Benchmark
  public void unsafeAccessorGet() {
    accessor.get(myPojo);
  }
}
