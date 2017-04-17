/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.electronwill.utils;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A thread-safe variant of {@link IndexMap}. It is optimized for read operations.
 * <p>
 * The array containing the elements of the map is marked as volatile. Read operations are implemented as a
 * simple "get" on this array, that's why they're fast. Write operations use a volatile "set" of the array, to
 * ensure visibility by the get (and contains) methods. And to achieve complete thread-safety and atomicity,
 * the critical parts of the write operations are guarded by synchronized blocks.
 * </p>
 * <p>
 * About null values: the ConcurrentIndexMap does not support null values. A null value is considered as "no
 * value set".
 * </p>
 *
 * @author TheElectronWill
 * @param <E>
 */
public final class ConcurrentIndexMap<E> extends AbstractMap<Integer, E> implements ConcurrentMap<Integer, E> {

	/**
	 * The array that contains the values. Indexes are the keys.
	 */
	private volatile Object[] array;

	/**
	 * The number of values in the map.
	 */
	private volatile int size;

	private final ValueCollection values = new ValueCollection();
	private final EntrySet entrySet = new EntrySet();

	/**
	 * Creates a new IndexMap with an initial capacity of 10.
	 */
	public ConcurrentIndexMap() {
		this.array = new Object[10];
	}

	/**
	 * Creates a new IndexMap with the given initial capacity.
	 *
	 * @param initialCapacity
	 */
	public ConcurrentIndexMap(int initialCapacity) {
		this.array = new Object[initialCapacity];
	}

	/**
	 * Creates a new IndexMap with the given underlying array. Any change to this array is reflected in the
	 * map and vice-versa.
	 *
	 * @param array
	 */
	public ConcurrentIndexMap(Object[] array) {
		this.array = array;
	}

	/**
	 * Creates a new IntArrayMap that contains the keys-values pairs of the given map.
	 *
	 * @param map
	 */
	public ConcurrentIndexMap(Map<Integer, E> map) {
		this.array = new Object[map.size()];
		putAll(map);
	}

	@Override
	public void clear() {
		synchronized (this) {
			Object[] arr = array;
			Arrays.fill(arr, null);
			array = arr;
			size = 0;
		}
	}

	/**
	 * Returns true if this map contains a mapping for the specified key. There can be at most one mapping per
	 * key.
	 *
	 * @param key the key
	 * @return true if this Map contains a mapping for that key, false if it does not.
	 */
	public boolean containsKey(int key) {
		final Object[] arr = array;
		return key > -1 && key < arr.length && arr[key] != null;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof Integer) {
			return containsKey((int) key);
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		final Object[] arr = array;
		for (Object o : arr) {
			if (o.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public E compute(Integer key, BiFunction<? super Integer, ? super E, ? extends E> remappingFunction) {
		synchronized (this) {
			E oldValue = get(key.intValue());
			E newValue = remappingFunction.apply(key, oldValue);
			if (newValue == null) {
				remove(key.intValue());
				return null;
			} else {
				put(key.intValue(), newValue);
				return newValue;
			}
		}
	}

	@Override
	public E computeIfAbsent(Integer key, Function<? super Integer, ? extends E> mappingFunction) {
		synchronized (this) {
			E oldValue = get(key.intValue());
			if (oldValue == null) {
				E newValue = mappingFunction.apply(key);
				if (newValue != null) {
					put(key.intValue(), newValue);
					return newValue;
				}
			}
			return oldValue;
		}
	}

	@Override
	public E computeIfPresent(Integer key, BiFunction<? super Integer, ? super E, ? extends E> remappingFunction) {
		synchronized (this) {
			E oldValue = get(key.intValue());
			if (oldValue != null) {
				E newValue = remappingFunction.apply(key, oldValue);
				if (newValue != null) {
					put(key.intValue(), newValue);
					return newValue;
				} else {
					remove(key.intValue());
					return null;
				}
			} else {
				return null;
			}
		}
	}

	@Override
	public void forEach(BiConsumer<? super Integer, ? super E> action) {
		EntryIterator it = new EntryIterator();
		while (it.hasNext()) {
			IndexEntry entry = it.next();
			action.accept(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns a Set view of the mappings contained in this map. The set is backed by the map, so changes to
	 * the map are reflected in the set, and vice-versa. The view's iterators and spliterators are weakly
	 * consistent.
	 *
	 * @return a set view of the mappings contained in this map.
	 */
	@Override
	public EntrySet entrySet() {
		return entrySet;
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the
	 * key.
	 *
	 * @param key the key.
	 * @return the associated value, or null if there is no mapping for that key.
	 */
	@Override
	public E get(Object key) {
		if (key instanceof Integer) {
			return get((int) key);
		}
		return null;
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the
	 * key.
	 *
	 * @param key the key.
	 * @return the associated value, or null if there is no mapping for that key.
	 */
	public E get(int key) {
		final Object[] arr = array;//volatile read
		return key < arr.length ? (E) arr[key] : null;
	}

	@Override
	public E getOrDefault(Object key, E defaultValue) {
		if (key instanceof Integer) {
			return getOrDefault((int) key, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Returns the value to which the specified key is mapped, or defaultValue if this map contains no mapping
	 * for the key.
	 *
	 * @param key the key.
	 * @param defaultValue the value to be returned if this map contains no mapping for the key.
	 * @return the value to which the specified key is mapped, or defaultValue if this map contains no mapping
	 * for the key.
	 */
	public E getOrDefault(int key, E defaultValue) {
		final E value = get(key);
		return value == null ? defaultValue : value;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public E merge(Integer key, E value, BiFunction<? super E, ? super E, ? extends E> remappingFunction) {
		if (value == null || remappingFunction == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			E oldValue = get(key.intValue());
			E newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
			if (newValue == null) {
				remove(key.intValue());
			} else {
				put(key.intValue(), newValue);
			}
			return newValue;
		}
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map previously contained a
	 * mapping for the key, the old value is replaced by the specified value.
	 *
	 * @param key the key.
	 * @param value the associated value.
	 * @return the previous value if there is one, or null if there is none.
	 */
	@Override
	public E put(Integer key, E value) {
		return put(key.intValue(), value);
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map previously contained a
	 * mapping for the key, the old value is replaced by the specified value.
	 *
	 * @param key the key.
	 * @param value the associated value.
	 * @return the previous value if there is one, or null if there is none.
	 */
	public E put(int key, E value) {
		if (value == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			Object[] arr = array;
			E prev;
			if (key < arr.length) {
				prev = (E) arr[key];
			} else {
				arr = Arrays.copyOf(arr, key * 3 / 2 + 1);
				prev = null;
			}
			if (prev == null) {
				size++;
			}
			arr[key] = value;
			array = arr;
			return prev;
		}
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends E> m) {
		synchronized (this) {
			Object[] arr = array;
			int newSize = size;
			for (Entry<? extends Integer, ? extends E> e : m.entrySet()) {
				int key = e.getKey();
				E value = e.getValue();
				E prev;
				if (key < arr.length) {
					prev = (E) arr[key];
				} else {
					arr = Arrays.copyOf(arr, key * 3 / 2 + 1);
					prev = null;
				}
				if (prev == null) {
					size++;
				}
				arr[key] = value;
				newSize++;
			}
			array = arr;
			size = newSize;
		}
	}

	/**
	 * If the specified key is not already associated with a value associates it with the given value and
	 * returns null, else returns the current value.
	 *
	 * @param key the key.
	 * @param value value to be associated with the key.
	 * @return the previous value associated with the key, or null if there was no mapping for the key.
	 */
	@Override
	public E putIfAbsent(Integer key, E value) {
		return put(key.intValue(), value);
	}

	/**
	 * If the specified key is not already associated with a value associates it with the given value and
	 * returns null, else returns the current value.
	 *
	 * @param key the key.
	 * @param value value to be associated with the key.
	 * @return the previous value associated with the key, or null if there was no mapping for the key.
	 */
	public E putIfAbsent(int key, E value) {
		if (value == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			Object[] arr = array;
			E prev;
			if (arr.length <= key) {
				arr = Arrays.copyOf(arr, key * 3 / 2 + 1);
				prev = null;
			} else {
				prev = (E) arr[key];
			}
			if (prev == null) {
				arr[key] = value;
				array = arr;
				size++;
			}
			return prev;
		}
	}

	/**
	 * Removes the mapping for a key from this map if it is present.
	 * Returns the value to which this map previously associated the key, or null if the map contained no
	 * mapping for the key.
	 *
	 * @param key the key.
	 * @return the previous value if there is one or null if there is none.
	 */
	@Override
	public E remove(Object key) {
		if (key instanceof Integer) {
			return remove((int) key);
		}
		return null;
	}

	/**
	 * Removes the mapping for a key from this map if it is present.
	 * Returns the value to which this map previously associated the key, or null if the map contained no
	 * mapping for the key.
	 *
	 * @param key the key.
	 * @return the previous value if there is one or null if there is none.
	 */
	public E remove(int key) {
		synchronized (this) {
			Object[] arr = array;
			if (arr.length <= key) {
				return null;
			}
			E prev = (E) arr[key];
			if (prev != null) {
				arr[key] = null;
				array = arr;
				size--;
			}
			return prev;
		}
	}

	@Override
	public boolean remove(Object key, Object value) {
		if (key instanceof Integer) {
			remove((int) key, value);
		}
		return false;
	}

	/**
	 * Removes the entry for the specified key only if it is currently mapped to the specified value.
	 *
	 * @param key the key.
	 * @param value the associated value.
	 * @return true if it was removed, false if it doesn't exist.
	 */
	public boolean remove(int key, Object value) {
		if (value == null) {
			return false;
		}
		synchronized (this) {
			Object[] arr = array;
			if (arr.length <= key) {
				return false;
			}
			E prev = (E) arr[key];
			if (prev.equals(value)) {
				arr[key] = null;
				array = arr;
				size--;
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean replace(Integer key, E oldValue, E newValue) {
		return replace(key.intValue(), oldValue, newValue);
	}

	/**
	 * Replaces the entry for the specified key only if currently mapped to the specified value.
	 *
	 * @param key key with which the specified value is associated oldValue.
	 * @param oldValue value expected to be associated with the specified key.
	 * @param newValue value to be associated with the specified key.
	 * @return true if the value was replaced.
	 */
	public boolean replace(int key, E oldValue, E newValue) {
		if (oldValue == null || newValue == null) {
			return false;
		}
		synchronized (this) {
			Object[] arr = array;
			if (arr.length <= key) {
				return false;
			}
			Object prev = arr[key];
			if (prev.equals(oldValue)) {
				arr[key] = newValue;
				array = arr;
				size++;
				return true;
			}
		}
		return false;
	}

	@Override
	public E replace(Integer key, E value) {
		return replace(key.intValue(), value);
	}

	/**
	 * Replaces the entry for the specified key only if it is currently mapped to some value.
	 *
	 * @param key key with which the specified value is associated value.
	 * @param value value to be associated with the specified key.
	 * @return the previous value associated with the specified key, or null if there was no mapping for the
	 * key.
	 */
	public E replace(int key, E value) {
		if (value == null) {
			return get(key);
		}
		synchronized (this) {
			Object[] arr = array;
			if (arr.length <= key) {
				return null;
			}
			E prev = (E) arr[key];
			if (prev != null) {
				arr[key] = value;
				array = arr;
				size++;
			}
			return prev;
		}

	}

	@Override
	public void replaceAll(BiFunction<? super Integer, ? super E, ? extends E> function) {
		synchronized (this) {
			Object[] arr = array;
			for (int key = 0; key < arr.length; key++) {
				Object value = arr[key];
				if (value != null) {//if there is a value associated to this key
					arr[key] = function.apply(key, (E) value);
				}
			}
			array = arr;
		}
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns a Collection view of the values contained in this map. The collection is backed by the map, so
	 * changes to the map are reflected in the collection, and vice-versa. The view's iterators and
	 * spliterators are weakly consistent.
	 */
	@Override
	public ValueCollection values() {
		return values;
	}

	/**
	 * A weakly consistent entry iterator. It is not guaranteed to reflect the latest state of the map.
	 */
	public final class EntryIterator implements Iterator<Entry<Integer, E>> {

		private final Object[] arr = array;
		private int cursor = -1;
		private IndexEntry next;

		@Override
		public boolean hasNext() {
			while (cursor < arr.length - 1) {
				Object value = arr[++cursor];
				if (value != null) {
					next = new IndexEntry(cursor, (E) value);
					return true;
				}
			}
			return false;
		}

		@Override
		public IndexEntry next() {
			return next;
		}

		@Override
		public void remove() {
			ConcurrentIndexMap.this.remove(cursor, arr[cursor]);
		}

	}

	/**
	 * A weakily consistent value iterator. It is not guaranteed to reflect the latest state of the map.
	 */
	public final class ValueIterator implements Iterator<E> {

		private final Object[] arr = array;
		private int cursor = -1;
		private E next;

		@Override
		public boolean hasNext() {
			while (cursor < arr.length - 1) {
				Object value = arr[++cursor];
				if (value != null) {
					next = (E) value;
					return true;
				}
			}
			return false;
		}

		@Override
		public E next() {
			return next;
		}

		@Override
		public void remove() {
			ConcurrentIndexMap.this.remove(cursor);
		}

	}

	/**
	 * An entry set with a weakly consistent iterator. The set is guaranteed to reflect the latest state of
	 * the map, but its iterator isn't. The {@link #remove(java.lang.Object)} method is not guaranteed to be
	 * performed atomically on the latest state of the map, because it internally uses the iterator.
	 */
	public final class ValueCollection extends AbstractCollection<E> {

		private ValueCollection() {
		}

		@Override
		public void clear() {
			ConcurrentIndexMap.this.clear();
		}

		@Override
		public boolean remove(Object o) {
			boolean removed = false;
			ValueIterator it = new ValueIterator();
			while (it.hasNext()) {
				E next = it.next();
				if (o.equals(next)) {
					it.remove();
					removed = true;
				}
			}
			return removed;
		}

		@Override
		public boolean contains(Object v) {
			return ConcurrentIndexMap.this.containsValue(v);
		}

		@Override
		public boolean isEmpty() {
			return ConcurrentIndexMap.this.isEmpty();
		}

		@Override
		public Iterator<E> iterator() {
			return new ValueIterator();
		}

		@Override
		public int size() {
			return ConcurrentIndexMap.this.size();
		}
	}

	/**
	 * An entry set with a weakly consistent iterator. The set is guaranteed to reflect the latest state of
	 * the map, but its iterator isn't.
	 */
	public final class EntrySet extends AbstractSet<Entry<Integer, E>> {

		private EntrySet() {
		}

		@Override
		public EntryIterator iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Entry) {
				Entry<Integer, E> entry = (Entry) o;
				Object value = ConcurrentIndexMap.this.get(entry.getKey());
				return value != null && entry.getValue().equals(value);
			}
			return false;
		}

		@Override
		public boolean add(Entry<Integer, E> e) {
			putIfAbsent(e.getKey(), e.getValue());
			return true;
		}

		@Override
		public void clear() {
			ConcurrentIndexMap.this.clear();
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Entry) {
				Entry<Integer, E> entry = (Entry) o;
				return ConcurrentIndexMap.this.remove(entry.getKey(), entry.getValue());
			}
			return false;
		}

	}

	/**
	 * Represents a key-value entry of this map. The key is an integer. The key and the value aren't null.
	 */
	public final class IndexEntry implements Entry<Integer, E> {

		private final int key;
		private E value;

		public IndexEntry(int key, E value) {
			this.key = key;
			this.value = value;
		}

		public int getIntKey() {
			return key;
		}

		@Override
		public Integer getKey() {
			return key;
		}

		@Override
		public E getValue() {
			return value;
		}

		@Override
		public E setValue(E value) {
			this.value = value;
			return put(key, value);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Entry) {
				Entry entry = (Entry) obj;
				return entry.getKey() != null && getKey().equals(entry.getKey())
						&& entry.getValue() != null && getValue().equals(entry.getValue());
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 43 * hash + key;
			hash = 43 * hash + value.hashCode();
			return hash;
		}

	}

}
