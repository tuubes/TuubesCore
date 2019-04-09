package com.electronwill.util

/**
 * A `CompactStorage` is a fixed-length container that stores its value in a contiguous way,
 * across the bytes boundaries. There are ''no bit'' between two adjacent values.
 *
 * While a general implementation is provided, better performance is obtained with exactly
 * 4, 8 or 16 bits per value.
 *
 * @author TheElectronWill
 */
object CompactStorage {
  /**
   * Creates a new `CompactStorage`. The most optimized optimisation is chosen automatically.
   *
   * @param bitsPerValue how many bits in one value, at most 32
   * @param size the total number of values
   * @return an instance of CompactStorage with the given settings
   */
  def apply(bitsPerValue: Int, size: Int): CompactStorage = {
    require(bitsPerValue > 0 && bitsPerValue < 32, "it is required that 0 < bitsPerValue < 32")
    val arraySize = byteSize(bitsPerValue, size)
    val byteArray = new Array[Byte](arraySize)
    bitsPerValue match {
      case 4  => new CompactStorage4(size, byteArray)
      case 8  => new CompactStorage8(size, byteArray)
      case 16 => new CompactStorage16(size, byteArray)
      case _  => new CompactStorageN(bitsPerValue, size, byteArray)
    }
  }

  /**
   * Creates a new `CompactStorage` backed by the given byte array. Every modification of the
   * storage is reflected to the array and vice-versa. Depending on `bitsPerValue`, one element of
   * the storage may not correspond to one byte of the array.
   *
   * The most optimized optimisation is chosen automatically.
   *
   * @param bitsPerValue how many bits in one value, at most 32
   * @param byteArray the array to use
   * @return an instance of CompactStorage backed by the given array
   */
  def apply(bitsPerValue: Int, byteArray: Array[Byte]): CompactStorage = {
    val size = byteArray.length * 8 / bitsPerValue
    bitsPerValue match {
      case 4  => new CompactStorage4(size, byteArray)
      case 8  => new CompactStorage8(size, byteArray)
      case 16 => new CompactStorage16(size, byteArray)
      case _  => new CompactStorageN(bitsPerValue, size, byteArray)
    }
  }

  /**
   * Calculates the number of bytes needed to store n values that takes b ''bits'' each.
   *
   * @param bitsPerValue the number of bits (b)
   * @param numberOfValues the number of values (n)
   * @return the minimal number of bytes needed to store the values
   */
  def byteSize(bitsPerValue: Int, numberOfValues: Int): Int = {
    Math.ceil(bitsPerValue * numberOfValues / 8).toInt
  }
}
/**
 * A `CompactStorage` is a fixed-length container that stores its value in a contiguous way,
 * across the bytes boundaries. There are ''no bit'' between two adjacent values.
 *
 * While a general implementation is provided, better performance is obtained with exactly
 * 4, 8 or 16 bits per value. Use [[CompactStorage$.apply]] to use the most optimised implementation
 * for your case.
 *
 * @author TheElectronWill
 */
sealed abstract class CompactStorage(final val size: Int, final val bytes: Array[Byte]) {
  final def byteSize: Int = bytes.length
  def bitsPerValue: Int

  /**
   * Gets a value from the storage.
   * @return the value at index idx; 0 is the first value, 1 is the second value, and so on
   */
  def apply(idx: Int): Int

  /**
   * Sets the value at the given index.
   * @param idx the index
   * @param value the new value to set
   */
  def update(idx: Int, value: Int): Unit

  /**
   * Replaces all values by the given one.
   * @param value the new value
   */
  final def fill(value: Int): Unit = {
      var i = 0
      while (i < size) {
        this(i) = value
        i += 1
      }
  }

  /**
   * Replaces each occurence of a value by another one.
   * @param value the value to replace
   * @param replacement the replacement to use
   */
  final def replace(value: Int, replacement: Int): Unit = {
    var i = 0
    while (i < size) {
      val v = this(i)
      if (v == value) {
        this(i) = replacement
      }
      i += 1
    }
  }

  /**
   * Copies this storage in a new one with a greater number of bits per value.
   * @param increasePerValue the additional number of bits per value, should be > 0
   * @return a new `CompactStorage` that contains the same values as this one but has more bits per value
   */
  final def expand(increasePerValue: Int): CompactStorage = {
    val newStorage = CompactStorage(bitsPerValue + increasePerValue, size)
    var i = 0
    while (i < size) {
      newStorage(i) = this(i)
      i += 1
    }
    newStorage
  }
}

/**
 * General implementation of [[com.electronwill.util.CompactStorage]] that works for any number of
 * bits per value.
 *
 * @param bitsPerValue how many bits in each value
 * @param s number of values
 * @param b byte array that stores the values
 */
final class CompactStorageN private[util] (val bitsPerValue: Int, s: Int, b: Array[Byte])
    extends CompactStorage(s, b) {
  override def apply(idx: Int): Int = {
    var value = 0
    var i = 0
    while (i < bitsPerValue) {
      val bitIdx = idx * bitsPerValue + i
      val byteIdx = bitIdx >> 3 // (>> 3) divides by 8
      val bitInByte = bitIdx & 7 // (x & 7) is the same as (x % 8)
      if ((bytes(byteIdx) & (1 << bitInByte)) != 0) { // if bit is 1 then update the result
        value |= (1 << i)
      }
      i += 1
    }
    value
  }
  override def update(idx: Int, value: Int): Unit = {
    var i = 0
    while (i < bitsPerValue) {
      val bitIdx = idx * bitsPerValue + i
      val byteIdx = bitIdx >> 3
      val bitInByte = bitIdx & 7
      val mask = 1 << i
      if ((value & mask) != 0) {
        bytes(byteIdx) = (bytes(byteIdx) | (1 << bitInByte)).toByte // set bit to 1
      } else {
        bytes(byteIdx) = (bytes(byteIdx) & ~mask).toByte // set bit to 0
      }
      i += 1
    }
  }
}

/**
 * An implementation of [[com.electronwill.util.CompactStorage]] optimized for 4-bits values.
 * @param s number of values
 * @param b byte array that stores the values
 */
final class CompactStorage4 private[util] (s: Int, b: Array[Byte]) extends CompactStorage(s, b) {
  override def bitsPerValue: Int = 4
  override def apply(idx: Int): Int = {
    val byteIdx = idx >> 1 // (>> 1) divides by 2
    if ((byteIdx & 1) == 0) { // (x & 1) is the same as (x % 2)
      bytes(byteIdx) >> 4
    } else {
      bytes(byteIdx) & 0xf
    }
  }
  override def update(idx: Int, value: Int): Unit = {
    val byteIdx = idx >> 1
    if ((byteIdx & 1) == 0) {
      bytes(byteIdx) = (((value & 0xf) << 4) | bytes(byteIdx) & 0xf).toByte
    } else {
      bytes(byteIdx) = ((value & 0xf) | (bytes(byteIdx) & 0xf0)).toByte
    }
  }
}

/**
 * An implementation of [[com.electronwill.util.CompactStorage]] optimized for 8-bits values (i.e.
 * one value = one byte in the byte array).
 *
 * @param s number of values
 * @param b byte array that stores the values
 */
final class CompactStorage8 private[util] (s: Int, b: Array[Byte]) extends CompactStorage(s, b) {
  override def bitsPerValue: Int = 8
  override def apply(idx: Int): Int = {
    bytes(idx)
  }
  override def update(idx: Int, value: Int): Unit = {
    bytes(idx) = value.toByte
  }
}

/**
 * An implementation of [[com.electronwill.util.CompactStorage]] optimized for 16-bits values (i.e.
 * one value = two bytes in the byte array).
 *
 * @param s number of values
 * @param b byte array that stores the values
 */
final class CompactStorage16 private[util] (s: Int, b: Array[Byte]) extends CompactStorage(s, b) {
  override def bitsPerValue: Int = 16
  override def apply(idx: Int): Int = {
    val firstIdx = idx << 1 // (<< 1) multiplies by 2
    val secondIdx = firstIdx + 1
    bytes(firstIdx) << 8 | bytes(secondIdx)
  }
  override def update(idx: Int, value: Int): Unit = {
    val firstIdx = idx << 1
    val secondIdx = firstIdx + 1
    bytes(firstIdx) = (value >> 8).toByte
    bytes(secondIdx) = (value & 0xff).toByte
  }
}
