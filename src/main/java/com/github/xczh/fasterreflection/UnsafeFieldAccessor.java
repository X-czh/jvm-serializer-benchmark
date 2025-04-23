package com.github.xczh.fasterreflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * For simplicity, we only implement methods for non-static, non-final, and non-volatile fields.
 */
public class UnsafeFieldAccessor {

	private static final Unsafe UNSAFE = UnsafeUtils.UNSAFE;

	private final Class<?> type;
	private final long fieldOffset;

	public UnsafeFieldAccessor(Field field) {
		type = field.getType();
		fieldOffset = UNSAFE.objectFieldOffset(field);
	}

	public Object get(Object object) {
		if (type == Boolean.TYPE) {
			return UNSAFE.getBoolean(object, fieldOffset);
		} else if (type == Byte.TYPE) {
			return UNSAFE.getByte(object, fieldOffset);
		} else if (type == Short.TYPE) {
			return UNSAFE.getShort(object, fieldOffset);
		} else if (type == Character.TYPE) {
			return UNSAFE.getChar(object, fieldOffset);
		} else if (type == Integer.TYPE) {
			return UNSAFE.getInt(object, fieldOffset);
		} else if (type == Long.TYPE) {
			return UNSAFE.getLong(object, fieldOffset);
		} else if (type == Float.TYPE) {
			return UNSAFE.getFloat(object, fieldOffset);
		} else if (type == Double.TYPE) {
			return UNSAFE.getDouble(object, fieldOffset);
		} else {
			return UNSAFE.getObject(object, fieldOffset);
		}
	}

	public void set(Object object, Object value) {
		// TODO
	}
}
