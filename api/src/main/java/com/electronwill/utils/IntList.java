package com.electronwill.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author TheElectronWill
 */
public final class IntList implements Cloneable {
    private int[] values;
    private int size = 0;

    public IntList() {
        this(10);
    }

    public IntList(int initialCapacity) {
        this.values = new int[initialCapacity];
    }

    public IntList(int[] initialValues) {
        this.values = initialValues;
        this.size = initialValues.length;
    }

    public int get(int index) {
        return values[index];
    }

    public void set(int index, int value) {
        values[index] = value;
    }

    public void add(int value) {
        if (values.length < size + 1) {
            values = Arrays.copyOf(values, values.length * 3 / 2 + 1);
        }
        values[size++] = value;
    }

    public void addAll(IntList list) {
        addAll(list.values);
    }

    public void addAll(IntList list, int offset, int length) {
        addAll(list.values, offset, length);
    }

    public void addAll(int[] array) {
        addAll(array, 0, array.length);
    }

    public void addAll(int[] array, int offset, int length) {
        if (values.length < size + length) {
            values = Arrays.copyOf(values, Math.max(values.length * 3 / 2, values.length + length));
        }
        System.arraycopy(array, offset, values, size, length);
        size += length;
    }

    public void remove(int index) {
        int lastIndex = size - 1;
        if (index != lastIndex) {
            System.arraycopy(values, index + 1, values, index, lastIndex - index);
        }
        size--;
    }

    public int indexOf(int value) {
        for (int i = 0; i < size; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(int value) {
        return indexOf(value) != -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }

    public void compact() {
        if (values.length > size) {
            values = Arrays.copyOf(values, size);
        }
    }

    public int[] values() {
        return values;
    }

    public int[] toArray() {
        return Arrays.copyOf(values, size);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IntList of size ").append(size).append(" : [");
        for (int i = 0; i < size; i++) {
            sb.append(i);
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            result = 27 * result + values[i];
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (!(obj instanceof IntList)) { return false; }
        return Arrays.equals(values, ((IntList)obj).values);
    }

    @Override
    public IntList clone() {
        return new IntList(toArray());
    }
}