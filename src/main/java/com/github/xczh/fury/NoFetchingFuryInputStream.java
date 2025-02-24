package com.github.xczh.fury;

import java.io.IOException;
import java.io.InputStream;
import org.apache.fury.io.FuryInputStream;
import org.apache.fury.memory.MemoryBuffer;

/**
 * {@link FuryInputStream} will try to buffer data inside, the data read from original stream won't
 * be the data you expected. This is a wrapper for it that does not prefetch data.
 */
public class NoFetchingFuryInputStream extends FuryInputStream {

  public NoFetchingFuryInputStream(InputStream stream) {
    super(stream);
  }

  /**
   * Read stream and fill the data to underlying buffer by "exactly" minFillSize. Thus, it will only
   * load the required data and never prefetch data.
   */
  @Override
  public int fillBuffer(int minFillSize) {
    MemoryBuffer buffer = getBuffer();
    byte[] heapMemory = buffer.getHeapMemory();
    int offset = buffer.size();
    if (offset + minFillSize > heapMemory.length) {
      heapMemory = growBuffer(minFillSize, buffer);
    }

    try {
      InputStream stream = getStream();

      int read;
      int newRead;
      for (read = stream.read(heapMemory, offset, minFillSize);
          read < minFillSize;
          read += newRead) {
        newRead = stream.read(heapMemory, offset + read, minFillSize - read);
        if (newRead < 0) {
          throw new IndexOutOfBoundsException("No enough data in the stream " + stream);
        }
      }

      buffer.increaseSize(read);
      return read;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] growBuffer(int minFillSize, MemoryBuffer buffer) {
    int newSize;
    int size = buffer.size();
    int targetSize = size + minFillSize;
    newSize =
        targetSize < MemoryBuffer.BUFFER_GROW_STEP_THRESHOLD
            ? targetSize << 2
            : (int) Math.min(targetSize * 1.5d, Integer.MAX_VALUE - 8);
    byte[] newBuffer = new byte[newSize];
    byte[] heapMemory = buffer.getHeapMemory();
    System.arraycopy(heapMemory, 0, newBuffer, 0, size);
    buffer.initHeapBuffer(newBuffer, 0, size);
    heapMemory = newBuffer;
    return heapMemory;
  }
}
