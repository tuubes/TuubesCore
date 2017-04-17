/*
Copyright (C) 2013-2015 Steveice10

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.mcphoton.network;

import java.io.IOException;
import java.util.UUID;

/**
 * An interface for reading network data.
 */
public interface NetInput {
    /**
     * Reads the next boolean.
     *
     * @return The next boolean.
     * @throws IOException If an I/O error occurs.
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads the next byte.
     *
     * @return The next byte.
     * @throws IOException If an I/O error occurs.
     */
    byte readByte() throws IOException;

    /**
     * Reads the next unsigned byte.
     *
     * @return The next unsigned byte.
     * @throws IOException If an I/O error occurs.
     */
    int readUnsignedByte() throws IOException;

    /**
     * Reads the next short.
     *
     * @return The next short.
     * @throws IOException If an I/O error occurs.
     */
    short readShort() throws IOException;

    /**
     * Reads the next unsigned short.
     *
     * @return The next unsigned short.
     * @throws IOException If an I/O error occurs.
     */
    int readUnsignedShort() throws IOException;

    /**
     * Reads the next char.
     *
     * @return The next char.
     * @throws IOException If an I/O error occurs.
     */
    char readChar() throws IOException;

    /**
     * Reads the next integer.
     *
     * @return The next integer.
     * @throws IOException If an I/O error occurs.
     */
    int readInt() throws IOException;

    /**
     * Reads the next varint. A varint is a form of integer where only necessary bytes are written. This is done to save bandwidth.
     *
     * @return The next varint.
     * @throws IOException If an I/O error occurs.
     */
    int readVarInt() throws IOException;

    /**
     * Reads the next long.
     *
     * @return The next long.
     * @throws IOException If an I/O error occurs.
     */
    long readLong() throws IOException;

    /**
     * Reads the next varlong. A varlong is a form of long where only necessary bytes are written. This is done to save bandwidth.
     *
     * @return The next varlong.
     * @throws IOException If an I/O error occurs.
     */
    long readVarLong() throws IOException;

    /**
     * Reads the next float.
     *
     * @return The next float.
     * @throws IOException If an I/O error occurs.
     */
    float readFloat() throws IOException;

    /**
     * Reads the next double.
     *
     * @return The next double.
     * @throws IOException If an I/O error occurs.
     */
    double readDouble() throws IOException;

    /**
     * Reads the next byte array.
     *
     * @param length The length of the byte array.
     * @return The next byte array.
     * @throws IOException If an I/O error occurs.
     */
    byte[] readBytes(int length) throws IOException;

    /**
     * Reads as much data as possible into the given byte array.
     *
     * @param b Byte array to read to.
     * @return The amount of bytes read, or -1 if no bytes could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readBytes(byte b[]) throws IOException;

    /**
     * Reads the given amount of bytes into the given array at the given offset.
     *
     * @param b      Byte array to read to.
     * @param offset Offset of the array to read to.
     * @param length Length of bytes to read.
     * @return The amount of bytes read, or -1 if no bytes could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readBytes(byte b[], int offset, int length) throws IOException;

    /**
     * Reads the next short array.
     *
     * @param length The length of the short array.
     * @return The next short array.
     * @throws IOException If an I/O error occurs.
     */
    short[] readShorts(int length) throws IOException;

    /**
     * Reads as much data as possible into the given short array.
     *
     * @param s Short array to read to.
     * @return The amount of shorts read, or -1 if no shorts could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readShorts(short s[]) throws IOException;

    /**
     * Reads the given amount of shorts into the given array at the given offset.
     *
     * @param s      Short array to read to.
     * @param offset Offset of the array to read to.
     * @param length Length of bytes to read.
     * @return The amount of shorts read, or -1 if no shorts could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readShorts(short s[], int offset, int length) throws IOException;

    /**
     * Reads the next int array.
     *
     * @param length The length of the int array.
     * @return The next int array.
     * @throws IOException If an I/O error occurs.
     */
    int[] readInts(int length) throws IOException;

    /**
     * Reads as much data as possible into the given int array.
     *
     * @param i Int array to read to.
     * @return The amount of ints read, or -1 if no ints could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readInts(int i[]) throws IOException;

    /**
     * Reads the given amount of ints into the given array at the given offset.
     *
     * @param i      Int array to read to.
     * @param offset Offset of the array to read to.
     * @param length Length of bytes to read.
     * @return The amount of ints read, or -1 if no ints could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readInts(int i[], int offset, int length) throws IOException;

    /**
     * Reads the next long array.
     *
     * @param length The length of the long array.
     * @return The next long array.
     * @throws IOException If an I/O error occurs.
     */
    long[] readLongs(int length) throws IOException;

    /**
     * Reads as much data as possible into the given long array.
     *
     * @param l Long array to read to.
     * @return The amount of longs read, or -1 if no longs could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readLongs(long l[]) throws IOException;

    /**
     * Reads the given amount of longs into the given array at the given offset.
     *
     * @param l      Long array to read to.
     * @param offset Offset of the array to read to.
     * @param length Length of bytes to read.
     * @return The amount of longs read, or -1 if no longs could be read.
     * @throws IOException If an I/O error occurs.
     */
    int readLongs(long l[], int offset, int length) throws IOException;

    /**
     * Reads the next string.
     *
     * @return The next string.
     * @throws IOException If an I/O error occurs.
     */
    String readString() throws IOException;

    /**
     * Reads the next UUID.
     *
     * @return The next UUID.
     * @throws IOException If an I/O error occurs.
     */
    UUID readUUID() throws IOException;

    /**
     * Gets the number of available bytes.
     *
     * @return The number of available bytes.
     */
    int available() throws IOException;
}
