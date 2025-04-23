package com.github.xczh.fasterreflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * For simplicity, we only implement methods for non-static, non-final, and non-volatile fields.
 */
public class LambdaFactory {

	private static final Unsafe UNSAFE = UnsafeUtils.UNSAFE;

	public static Function<Object, Object> produceGetter(Field field) {
		final Class<?> type = field.getType();
		final long fieldOffset = UNSAFE.objectFieldOffset(field);

		if (type == Boolean.TYPE) {
			return (object) -> UNSAFE.getBoolean(object, fieldOffset);
		} else if (type == Byte.TYPE) {
			return (object) -> UNSAFE.getByte(object, fieldOffset);
		} else if (type == Short.TYPE) {
			return (object) -> UNSAFE.getShort(object, fieldOffset);
		} else if (type == Character.TYPE) {
			return (object) -> UNSAFE.getChar(object, fieldOffset);
		} else if (type == Integer.TYPE) {
			return (object) -> UNSAFE.getInt(object, fieldOffset);
		} else if (type == Long.TYPE) {
			return (object) -> UNSAFE.getLong(object, fieldOffset);
		} else if (type == Float.TYPE) {
			return (object) -> UNSAFE.getFloat(object, fieldOffset);
		} else if (type == Double.TYPE) {
			return (object) -> UNSAFE.getDouble(object, fieldOffset);
		} else {
			return (object) -> UNSAFE.getObject(object, fieldOffset);
		}
	}

	public static BiConsumer<Object, Object> produceSetter(Field field) {
		final Class<?> type = field.getType();
		final long fieldOffset = UNSAFE.objectFieldOffset(field);
		// TODO
		return null;
	}
}
