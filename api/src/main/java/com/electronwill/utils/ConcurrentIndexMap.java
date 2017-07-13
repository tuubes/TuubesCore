package com.electronwill.utils;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A thread-safe variant of {@link IndexMap}. It is optimized for read operations.
 * <p>
 * The array containing the elements of the map is marked as volatile. Read operations are
 * implemented as a simple "get" on this array, that's why they're fast. Write operations use a
 * volatile "set" of the array, to ensure visibility by the get (and contains) methods. And to
 * achieve complete thread-safety and atomicity, the critical parts of the write operations are
 * guarded by synchronized blocks.
 * </p>
 * <p>
 * About null values: the ConcurrentIndexMap does not support null values. A null value is
 * considered as "no value set".
 * </p>
 *
 * @author TheElectronWill
 */
public final class ConcurrentIndexMap<V>
		implements Map<Integer, V>, ConcurrentMap<Integer, V>, Compactable {
	/**
	 * The array that contains the values. Indexes are the keys.
	 */
	private volatile Object[] array;

	/**
	 * The number of values in the map.
	 */
	private volatile int size;

	// These collections are lazy initialized:
	private volatile Collection<V> values;
	private volatile Set<Integer> keys;
	private volatile Set<Entry<Integer, V>> entries;

	/**
	 * Creates a new IndexMap with an initial capacity of 10.
	 */
	public ConcurrentIndexMap() {
		this.array = new Object[10];
	}

	/**
	 * Creates a new IndexMap with the given initial capacity.
	 */
	public ConcurrentIndexMap(int initialCapacity) {
		this.array = new Object[initialCapacity];
	}

	/**
	 * Creates a new IndexMap with the given underlying array. Any change to this array is reflected
	 * in the map and vice-versa.
	 */
	public ConcurrentIndexMap(Object[] array) {
		this.array = array;
	}

	/**
	 * Creates a new IntArrayMap that contains the keys-values pairs of the given map.
	 */
	public ConcurrentIndexMap(Map<Integer, V> map) {
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

	@Override
	public void compact() {
		synchronized (this) {
			Object[] arr = array;
			int idealLength = getMaxIndex(arr) + 1;
			if (arr.length != idealLength) {
				array = Arrays.copyOf(arr, idealLength);
			}
		}
	}

	private int getMaxIndex(Object[] arr) {
		for (int i = arr.length - 1; i >= 0; i--) {
			if (arr[i] != null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns true if this map contains a mapping for the specified key. There can be at most one
	 * mapping per key.
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
			return containsKey((int)key);
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
	public V compute(Integer key,
					 BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		synchronized (this) {
			V oldValue = get(key.intValue());
			V newValue = remappingFunction.apply(key, oldValue);
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
	public V computeIfAbsent(Integer key, Function<? super Integer, ? extends V> mappingFunction) {
		V oldValue = get(key.intValue());
		if (oldValue != null) {
			return oldValue;
		}
		synchronized (this) {
			oldValue = get(key.intValue());
			if (oldValue == null) {
				V newValue = mappingFunction.apply(key);
				if (newValue != null) {
					put(key.intValue(), newValue);
					return newValue;
				}
			}
			return oldValue;
		}
	}

	@Override
	public V computeIfPresent(Integer key,
							  BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		V oldValue = get(key.intValue());
		if (oldValue == null) {
			return null;
		}
		synchronized (this) {
			oldValue = get(key.intValue());
			if (oldValue != null) {
				V newValue = remappingFunction.apply(key, oldValue);
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
	public void forEach(BiConsumer<? super Integer, ? super V> action) {
		EntryIterator it = new EntryIterator();
		while (it.hasNext()) {
			IndexEntry entry = it.next();
			action.accept(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no
	 * mapping for the key.
	 *
	 * @param key the key.
	 * @return the associated value, or null if there is no mapping for that key.
	 */
	@Override
	public V get(Object key) {
		if (key instanceof Integer) {
			return get((int)key);
		}
		return null;
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no
	 * mapping for the key.
	 *
	 * @param key the key.
	 * @return the associated value, or null if there is no mapping for that key.
	 */
	public V get(int key) {
		final Object[] arr = array;//volatile read
		return key < arr.length ? (V)arr[key] : null;
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		if (key instanceof Integer) {
			return getOrDefault((int)key, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Returns the value to which the specified key is mapped, or defaultValue if this map contains
	 * no mapping for the key.
	 *
	 * @param key          the key.
	 * @param defaultValue the value to be returned if this map contains no mapping for the key.
	 * @return the value to which the specified key is mapped, or defaultValue if this map contains
	 * no mapping for the key.
	 */
	public V getOrDefault(int key, V defaultValue) {
		final V value = get(key);
		return value == null ? defaultValue : value;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public V merge(Integer key, V value,
				   BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		if (value == null || remappingFunction == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			V oldValue = get(key.intValue());
			V newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
			if (newValue == null) {
				remove(key.intValue());
			} else {
				put(key.intValue(), newValue);
			}
			return newValue;
		}
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map previously
	 * contained a mapping for the key, the old value is replaced by the specified value.
	 *
	 * @param key   the key.
	 * @param value the associated value.
	 * @return the previous value if there is one, or null if there is none.
	 */
	@Override
	public V put(Integer key, V value) {
		return put(key.intValue(), value);
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map previously
	 * contained a mapping for the key, the old value is replaced by the specified value.
	 *
	 * @param key   the key.
	 * @param value the associated value.
	 * @return the previous value if there is one, or null if there is none.
	 */
	public V put(int key, V value) {
		if (value == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			Object[] arr = array;
			V prev;
			if (key < arr.length) {
				prev = (V)arr[key];
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
	public void putAll(Map<? extends Integer, ? extends V> m) {
		synchronized (this) {
			Object[] arr = array;
			int newSize = size;
			for (Entry<? extends Integer, ? extends V> e : m.entrySet()) {
				int key = e.getKey();
				V value = e.getValue();
				V prev;
				if (key < arr.length) {
					prev = (V)arr[key];
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
	 * If the specified key is not already associated with a value associates it with the given
	 * value and returns null, else returns the current value.
	 *
	 * @param key   the key.
	 * @param value value to be associated with the key.
	 * @return the previous value associated with the key, or null if there was no mapping for the
	 * key.
	 */
	@Override
	public V putIfAbsent(Integer key, V value) {
		return put(key.intValue(), value);
	}

	/**
	 * If the specified key is not already associated with a value associates it with the given
	 * value and returns null, else returns the current value.
	 *
	 * @param key   the key.
	 * @param value value to be associated with the key.
	 * @return the previous value associated with the key, or null if there was no mapping for the
	 * key.
	 */
	public V putIfAbsent(int key, V value) {
		if (value == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			Object[] arr = array;
			V prev;
			if (arr.length <= key) {
				arr = Arrays.copyOf(arr, key * 3 / 2 + 1);
				prev = null;
			} else {
				prev = (V)arr[key];
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
	 * <p>
	 * Returns the value to which this map previously associated the key, or null if the map
	 * contained no mapping for the key.
	 *
	 * @param key the key.
	 * @return the previous value if there is one or null if there is none.
	 */
	@Override
	public V remove(Object key) {
		if (key instanceof Integer) {
			return remove((int)key);
		}
		return null;
	}

	/**
	 * Removes the mapping for a key from this map if it is present.
	 * <p>
	 * Returns the value to which this map previously associated the key, or null if the map
	 * contained no mapping for the key.
	 *
	 * @param key the key.
	 * @return the previous value if there is one or null if there is none.
	 */
	public V remove(int key) {
		synchronized (this) {
			Object[] arr = array;
			if (arr.length <= key) {
				return null;
			}
			V prev = (V)arr[key];
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
			remove((int)key, value);
		}
		return false;
	}

	/**
	 * Removes the entry for the specified key only if it is currently mapped to the specified
	 * value.
	 *
	 * @param key   the key.
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
			V prev = (V)arr[key];
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
	public boolean replace(Integer key, V oldValue, V newValue) {
		return replace(key.intValue(), oldValue, newValue);
	}

	/**
	 * Replaces the entry for the specified key only if currently mapped to the specified value.
	 *
	 * @param key      key with which the specified value is associated oldValue.
	 * @param oldValue value expected to be associated with the specified key.
	 * @param newValue value to be associated with the specified key.
	 * @return true if the value was replaced.
	 */
	public boolean replace(int key, V oldValue, V newValue) {
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
	public V replace(Integer key, V value) {
		return replace(key.intValue(), value);
	}

	/**
	 * Replaces the entry for the specified key only if it is currently mapped to some value.
	 *
	 * @param key   key with which the specified value is associated value.
	 * @param value value to be associated with the specified key.
	 * @return the previous value associated with the specified key, or null if there was no mapping
	 * for the key.
	 */
	public V replace(int key, V value) {
		if (value == null) {
			return get(key);
		}
		synchronized (this) {
			Object[] arr = array;
			if (arr.length <= key) {
				return null;
			}
			V prev = (V)arr[key];
			if (prev != null) {
				arr[key] = value;
				array = arr;
				size++;
			}
			return prev;
		}

	}

	@Override
	public void replaceAll(BiFunction<? super Integer, ? super V, ? extends V> function) {
		synchronized (this) {
			Object[] arr = array;
			for (int key = 0; key < arr.length; key++) {
				Object value = arr[key];
				if (value != null) {//if there is a value associated to this key
					arr[key] = function.apply(key, (V)value);
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
	 * Returns a Set view of the mappings contained in this map. The set is backed by the map, so
	 * changes to the map are reflected in the set, and vice-versa. The view's iterators and
	 * spliterators are weakly consistent.
	 * <p>
	 * The returned set's spliterator has the following characteristics:
	 * {@link Spliterator#CONCURRENT}, {@link Spliterator#NONNULL}, {@link Spliterator#DISTINCT}
	 */
	@Override
	public Set<Entry<Integer, V>> entrySet() {
		if (entries == null) {
			entries = new EntrySet();
			/* The initialization may be done multiple times under high contention, but this isn't
			 a problem because multiple EntrySets can coexist. The same reasoning applies to
			 keySet() and values().
			*/
		}
		return entries;
	}

	/**
	 * Returns a Set view of the keys contained in this map. The set is backed by the map, so
	 * changes to the map are reflected in the set, and vice-versa. The view's iterators and
	 * spliterators are weakly consistent.
	 * <p>
	 * The returned set's spliterator has the following characteristics:
	 * {@link Spliterator#CONCURRENT}, {@link Spliterator#NONNULL}, {@link Spliterator#DISTINCT}
	 */
	@Override
	public Set<Integer> keySet() {
		if (keys == null) {
			keys = new KeySet();
		}
		return keys;
	}

	/**
	 * Returns a Collection view of the values contained in this map. The collection is backed by
	 * the map, so changes to the map are reflected in the collection, and vice-versa. The view's
	 * iterators and spliterators are weakly consistent.
	 * <p>
	 * The returned collection's spliterator has the following characteristics:
	 * {@link Spliterator#CONCURRENT}, {@link Spliterator#NONNULL}
	 */
	@Override
	public Collection<V> values() {
		if (values == null) {
			values = new ValueCollection();
		}
		return values;
	}

	private final class KeySet extends AbstractSet<Integer> {
		@Override
		public Iterator<Integer> iterator() {
			return new Iterator<Integer>() {
				final EntryIterator entryIterator = new EntryIterator();

				@Override
				public boolean hasNext() {
					return entryIterator.hasNext();
				}

				@Override
				public Integer next() {
					return entryIterator.next().key;
				}

				@Override
				public void remove() {
					entryIterator.remove();
				}
			};
		}

		@Override
		public Spliterator<Integer> spliterator() {
			return Spliterators.spliterator(iterator(), size(), EntrySet.SPLITERATOR_CHARACS);
		}

		@Override
		public int size() {
			return ConcurrentIndexMap.this.size();
		}
	}

	private final class ValueCollection extends AbstractCollection<V> {
		private static final int SPLITERATOR_CHARACS = Spliterator.CONCURRENT | Spliterator.NONNULL;

		@Override
		public Iterator<V> iterator() {
			return new Iterator<V>() {
				final EntryIterator entryIterator = new EntryIterator();

				@Override
				public boolean hasNext() {
					return entryIterator.hasNext();
				}

				@Override
				public V next() {
					return entryIterator.next().value;
				}

				@Override
				public void remove() {
					entryIterator.remove();
				}
			};
		}

		@Override
		public Spliterator<V> spliterator() {
			return Spliterators.spliterator(iterator(), size(), SPLITERATOR_CHARACS);
		}

		@Override
		public int size() {
			return ConcurrentIndexMap.this.size();
		}
	}

	private final class EntrySet extends AbstractSet<Entry<Integer, V>> {
		private static final int SPLITERATOR_CHARACS = Spliterator.CONCURRENT
													   | Spliterator.NONNULL
													   | Spliterator.DISTINCT;

		@Override
		public EntryIterator iterator() {
			return new EntryIterator();
		}

		@Override
		public Spliterator<Entry<Integer, V>> spliterator() {
			return Spliterators.spliterator(iterator(), size(), SPLITERATOR_CHARACS);
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Entry) {
				Entry<Integer, V> entry = (Entry)o;
				Object value = get(entry.getKey());
				return value != null && entry.getValue().equals(value);
			}
			return false;
		}

		@Override
		public boolean add(Entry<Integer, V> e) {
			put(e.getKey(), e.getValue());
			return true;
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Entry) {
				Entry<Integer, V> entry = (Entry)o;
				return ConcurrentIndexMap.this.remove(entry.getKey(), entry.getValue());
			}
			return false;
		}
	}

	/**
	 * Weakly consistent iterator. An iterator is to be used by only one thread, therefore this
	 * class isn't thread-safe.
	 */
	public final class EntryIterator implements Iterator<Entry<Integer, V>> {
		private final Object[] arr = array;
		private int cursor = -1, lastCursor = -1;
		private V lastValue;
		private IndexEntry next;

		@Override
		public boolean hasNext() {
			if (next == null && cursor < arr.length - 1) {
				searchNextElement();
			}
			return next != null;
		}

		@Override
		public IndexEntry next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			IndexEntry result = next;
			next = null;// to search the element the next time next() or hasNext() is called
			lastCursor = cursor;// to make remove() work
			lastValue = result.value;// to make remove() work
			return result;
		}

		private void searchNextElement() {
			while (cursor < arr.length - 1) {
				V value = (V)arr[++cursor];
				if (value != null) {
					next = new IndexEntry(cursor, value);
				}
			}
		}

		@Override
		public void remove() {
			if (lastCursor == -1) {
				throw new IllegalStateException();
			}
			ConcurrentIndexMap.this.remove(lastCursor, lastValue);
			lastCursor = -1;//discards lastCursor
			lastValue = null;//discards lastValue
		}
	}

	/**
	 * An entry in the map. The key never changes, the value changes only because of setValue, it
	 * doesn't track the modifications of the IndexMap.
	 */
	private final class IndexEntry implements Entry<Integer, V> {
		private final int key;
		private volatile V value;

		IndexEntry(int key, V value) {
			if (value == null) {
				throw new NullPointerException("Null values aren't supported");
			}
			this.key = key;
			this.value = value;
		}

		@Override
		public Integer getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			this.value = value;
			return put(key, value);
		}

		@Override
		public boolean equals(Object o) {
			if ((o instanceof Map.Entry)) {
				Entry<?, ?> e = (Entry)o;
				return (e.getKey() instanceof Integer)
					   && (key == (Integer)e.getKey())
					   && value.equals(e.getValue());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return 31 * key + value.hashCode();
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}
}