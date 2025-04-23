package com.github.xczh.fasterreflection;

import java.lang.reflect.Field;

public class UnsafeUtils {

	public static final sun.misc.Unsafe UNSAFE = getUnsafe();

	public static sun.misc.Unsafe getUnsafe() {
		try {
			Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			return (sun.misc.Unsafe) unsafeField.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static long getClassFieldOffset(Field field) {
		try {
			return UNSAFE.objectFieldOffset(field);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
