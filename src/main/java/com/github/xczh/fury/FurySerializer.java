package com.github.xczh.fury;

import java.io.IOException;
import java.util.Objects;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.api.java.typeutils.runtime.DataInputViewStream;
import org.apache.flink.api.java.typeutils.runtime.DataOutputViewStream;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.fury.config.Language;
import org.apache.fury.io.FuryInputStream;

public class FurySerializer<T> extends TypeSerializer<T> {

  private final Class<T> type;

  private transient org.apache.fury.Fury fury;

  private transient DataInputView previousIn;
  private transient DataOutputView previousOut;

  private transient FuryInputStream inputStream;
  private transient DataOutputViewStream outputStream;

  public FurySerializer(Class<T> type) {
    this.type = type;
  }

  @Override
  public boolean isImmutableType() {
    return false;
  }

  @Override
  public FurySerializer<T> duplicate() {
    return new FurySerializer<>(type);
  }

  @Override
  public T createInstance() {
    return null;
  }

  @Override
  public T copy(T from) {
    checkFuryInitialized();
    return fury.copy(from);
  }

  @Override
  public T copy(T from, T reuse) {
    return copy(from);
  }

  @Override
  public int getLength() {
    return -1;
  }

  @Override
  public void serialize(T record, DataOutputView target) throws IOException {
    checkFuryInitialized();
    if (target != previousOut) {
      outputStream = new DataOutputViewStream(target);
      previousOut = target;
    }
    fury.serialize(outputStream, record);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(DataInputView source) throws IOException {
    checkFuryInitialized();
    if (source != previousIn) {
      inputStream = new NoFetchingFuryInputStream(new DataInputViewStream(source));
      previousIn = source;
    }
    return (T) fury.deserialize(inputStream);
  }

  @Override
  public T deserialize(T reuse, DataInputView source) throws IOException {
    return deserialize(source);
  }

  @Override
  public void copy(DataInputView source, DataOutputView target) throws IOException {
    serialize(deserialize(source), target);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FurySerializer) {
      FurySerializer<?> other = (FurySerializer<?>) obj;
      return Objects.equals(type, other.type);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public TypeSerializerSnapshot<T> snapshotConfiguration() {
    return null;
  }

  protected Class<T> getType() {
    return type;
  }

  private void checkFuryInitialized() {
    if (fury == null) {
      fury =
          org.apache.fury.Fury.builder()
              .withLanguage(Language.JAVA)
              .requireClassRegistration(false)
              // Disable it otherwise will throw exception if Guava not in classpath
              .registerGuavaTypes(false)
              // Good choice in most cases
              .withIntCompressed(true)
              // If a number is long type, it can't be represented by smaller bytes mostly,
              // the compression won't get good enough result, not worthy compared to performance
              // cost
              .withLongCompressed(false)
              // Large strings can be quite space-consuming
              .withStringCompressed(true)
              // Disable it for performance, but can cause StackOverflowError when serializing
              // objects
              // with recursive objects, see FLINK-3762
              .withRefTracking(false)
              // Compile in background to speed up first-time serialization
              .withAsyncCompilation(false)
              .build();
      fury.register(type);
    }
  }
}
