/*
 * Copyright (C) 2011 Furyhunter <furyhunter600@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the creator nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.trader.net;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * <p>
 * Implements data input on a byte array.
 * </p>
 * <p>
 * Started Mar 3, 2011
 * </p>
 * 
 * @author Furyhunter
 */
public class ByteArrayDataInput implements DataInput {
    
    private int position;
    private byte[] data;
    private ByteBuffer buffer;
    
    public ByteArrayDataInput(byte[] src) {
        if (src == null) {
            throw new NullPointerException();
        }
        data = src;
        position = 0;
        buffer = ByteBuffer.wrap(data);
        //System.out.println("BUFFER: "+buffer.toString());
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readFully(byte[])
     */
    @Override
    public void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readFully(byte[], int, int)
     */
    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
//        if (b == null) {
//            throw new NullPointerException();
//        }
//        if (off < 0 || len < 0 || b.length < off+len) {
//            throw new ArrayIndexOutOfBoundsException();
//        }
//        try {
//            for (int i = 0; i < len; i++) {
//                b[off+i] = data[position];
//                position += 1;
//            }
//            buffer.get(b, 0, len);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	buffer.get(b, 0, len);
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        } catch (IndexOutOfBoundsException e) {
        	throw new IllegalArgumentException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#skipBytes(int)
     */
    @Override
    public int skipBytes(int n) throws EOFException {
//        if (position + n > data.length - 1) {
//            int overage = -(data.length - (position + n));
//            position = data.length - 1;
//            return n - overage;
//        }
        buffer.position(buffer.position() + n);
        return n;
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readBoolean()
     */
    @Override
    public boolean readBoolean() throws EOFException {
//        try {
//            int value = data[position] & 0xFF;
//            position += 1;
//            if (value > 0)
//                return true;
//            else
//                return false;
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	int value = buffer.get();
        	if (value > 0)
        		return true;
        	else
        		return false;
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readByte()
     */
    @Override
    public byte readByte() throws EOFException {
//        position += 1;
//        try {
//            return data[position - 1];
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	return buffer.get();
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readUnsignedByte()
     */
    @Override
    public int readUnsignedByte() throws EOFException {
//        try {
//            position += 1;
//            return data[position - 1] & 0xFF;
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	int value = buffer.get();
        	return value & 0xFF;
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readShort()
     */
    @Override
    public short readShort() throws EOFException {
//        try {
//            int b1 = data[position];
//            position += 1;
//            int b2 = data[position];
//            position += 1;
//            return (short) ((b1 << 8) + (b2 << 0));
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	return buffer.getShort();
        } catch (BufferUnderflowException e) {
            //System.exit(-666);

            System.err.println("BufferUnderflowException -666");
            System.exit(-666);
            //return -666;
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readUnsignedShort()
     */
    @Override
    public int readUnsignedShort() throws EOFException {
//        try {
//            int b1 = data[position];
//            position += 1;
//            int b2 = data[position];
//            position += 1;
//            return (((b1 & 0xff) << 8) | (b2 & 0xff));
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	int value = buffer.getShort();
        	return value & 0xFFFF;
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readChar()
     */
    @Override
    public char readChar() throws EOFException {
//        try {
//            int b1 = data[position];
//            position += 1;
//            int b2 = data[position];
//            position += 1;
//            return (char) ((b1 << 8) + (b2 << 0));
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	return buffer.getChar();
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readInt()
     */
    @Override
    public int readInt() throws EOFException {
//        try {
//            int b1 = data[position];
//            position += 1;
//            int b2 = data[position];
//            position += 1;
//            int b3 = data[position];
//            position += 1;
//            int b4 = data[position];
//            position += 1;
//            return ((b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0));
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new EOFException();
//        }
        
        try {
        	return buffer.getInt();
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readLong()
     */
    @Override
    public long readLong() throws EOFException {
        
        try {
        	return buffer.getLong();
        } catch (BufferUnderflowException e) {
        	throw new EOFException();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readFloat()
     */
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readDouble()
     */
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readUTF()
     */
    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }
    
    /*
     * (non-Javadoc)
     * @see java.io.DataInput#readLine()
     */
    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException(
                "don't you ever let me catch you using this again");
    }
    
}
