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
 * An interface for writing network data.
 */
public interface NetOutput {
    /**
     * Writes a boolean.
     *
     * @param b Boolean to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeBoolean(boolean b) throws IOException;

    /**
     * Writes a byte.
     *
     * @param b Byte to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeByte(int b) throws IOException;

    /**
     * Writes a short.
     *
     * @param s Short to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeShort(int s) throws IOException;

    /**
     * Writes a char.
     *
     * @param c Char to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeChar(int c) throws IOException;

    /**
     * Writes a integer.
     *
     * @param i Integer to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeInt(int i) throws IOException;

    /**
     * Writes a varint. A varint is a form of integer where only necessary bytes are written. This is done to save bandwidth.
     *
     * @return i Varint to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeVarInt(int i) throws IOException;

    /**
     * Writes a long.
     *
     * @param l Long to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeLong(long l) throws IOException;

    /**
     * Writes a varlong. A varlong is a form of long where only necessary bytes are written. This is done to save bandwidth.
     *
     * @return l Varlong to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeVarLong(long l) throws IOException;

    /**
     * Writes a float.
     *
     * @param f Float to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeFloat(float f) throws IOException;

    /**
     * Writes a double.
     *
     * @param d Double to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeDouble(double d) throws IOException;

    /**
     * Writes a byte array.
     *
     * @param b Byte array to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeBytes(byte b[]) throws IOException;

    /**
     * Writes a byte array, using the given amount of bytes.
     *
     * @param b      Byte array to write.
     * @param length Bytes to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeBytes(byte b[], int length) throws IOException;

    /**
     * Writes a short array.
     *
     * @param s Short array to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeShorts(short s[]) throws IOException;

    /**
     * Writes a short array, using the given amount of bytes.
     *
     * @param s      Short array to write.
     * @param length Shorts to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeShorts(short s[], int length) throws IOException;

    /**
     * Writes an int array.
     *
     * @param i Int array to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeInts(int i[]) throws IOException;

    /**
     * Writes an int array, using the given amount of bytes.
     *
     * @param i      Int array to write.
     * @param length Ints to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeInts(int i[], int length) throws IOException;

    /**
     * Writes a long array.
     *
     * @param l Long array to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeLongs(long l[]) throws IOException;

    /**
     * Writes a long array, using the given amount of bytes.
     *
     * @param l      Long array to write.
     * @param length Longs to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeLongs(long l[], int length) throws IOException;

    /**
     * Writes a string.
     *
     * @param s String to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeString(String s) throws IOException;

    /**
     * Writes a UUID.
     *
     * @param uuid UUID to write.
     * @throws IOException If an I/O error occurs.
     */
    void writeUUID(UUID uuid) throws IOException;

    /**
     * Flushes the output.
     *
     * @throws IOException If an I/O error occurs.
     */
    void flush() throws IOException;
}
