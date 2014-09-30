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

import com.trader.net.data.Parsable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * <p>
 * Sent by client to ensure it is up to date.
 * </p>
 * <p>
 * Started Mar 2, 2011
 * </p>
 * 
 * @author Furyhunter
 */
public class HelloPacket extends Packet implements Parsable {
    
    public String buildVersion;
    public int gameId; //int
    public String guid;
    public String password;
    public String secret;
    public int keyTime; //int
    public byte[] key;
    public String unkStr;
    
    public String pk = ""; // String
	public String Tq = ""; // String
	public String H = ""; // String
	public String playPlatform = ""; // String
    public int idk;
    public byte[] idkey;

    
    public HelloPacket(DataInput read) {
        try {
            type = Packet.HELLO;
            parseFromDataInput(read);
        } catch (IOException e) {
            
        }
    }
    
    public HelloPacket() {
    	type = Packet.HELLO;
	}

	@Override
    public void parseFromDataInput(DataInput read) throws IOException {
        this.buildVersion = read.readUTF();
        this.gameId = read.readInt();
        this.guid = read.readUTF();
        this.password = read.readUTF();
        secret = read.readUTF();
        keyTime = read.readInt();
        int size1 = read.readUnsignedShort();
        if (size1 > 0) {
            key = new byte[size1];
            read.readFully(key);
        }

        int size2 = read.readInt();
        if (size2 > 0) {
            byte[] buf = new byte[size2];
            read.readFully(buf);
            unkStr = new String(buf, Charset.forName("UTF-8"));
        }

        this.pk = read.readUTF();
        this.Tq = read.readUTF();
        this.H = read.readUTF();
        this.playPlatform = read.readUTF();
        this.idk = read.readUnsignedShort();
        if (idk > 0) {
            idkey = new byte[idk];
            read.readFully(idkey);
        }

        byte[] b = null;
        System.out.println("HELLO PACKET INFO:");

        b = buildVersion.getBytes("UTF-8");
        System.out.println("buildVersion: "+Arrays.toString(b));
        System.out.println("buildVersion: "+buildVersion);

        b = ByteBuffer.allocate(4).putInt(gameId).array();
        System.out.println("gameId: "+Arrays.toString(b));
        System.out.println("gameId: "+gameId);

        b = guid.getBytes("UTF-8");
        System.out.println("guid: "+Arrays.toString(b));
        System.out.println("guid: "+guid);

        b = password.getBytes("UTF-8");
        System.out.println("password: "+Arrays.toString(b));
        System.out.println("password: "+password);

        b = secret.getBytes("UTF-8");
        System.out.println("secret: "+Arrays.toString(b));
        System.out.println("secret: "+secret);

        b = ByteBuffer.allocate(4).putInt(keyTime).array();
        System.out.println("keyTime: "+Arrays.toString(b));
        System.out.println("keyTime: "+keyTime);

        b = ByteBuffer.allocate(4).putInt(size1).array();
        System.out.println("size1 (IS UNSIGNED!): "+Arrays.toString(b));
        System.out.println("size1: "+size1);

        if(size1>0)System.out.println("key: "+Arrays.toString(key));
        if(size1>0)System.out.println("key: "+key);

        b = ByteBuffer.allocate(4).putInt(size2).array();
        System.out.println("size2: "+Arrays.toString(b));
        System.out.println("size2: "+size2);

        if(size2>0){
            b = unkStr.getBytes("UTF-8");
            System.out.println("unkStr: "+Arrays.toString(b));
            System.out.println("unkStr: "+unkStr);

        }

        b = pk.getBytes("UTF-8");
        System.out.println("pk: "+Arrays.toString(b));
        System.out.println("pk: "+pk);

        b = Tq.getBytes("UTF-8");
        System.out.println("Tq: "+Arrays.toString(b));
        System.out.println("Tq: "+Tq);

        b = H.getBytes("UTF-8");
        System.out.println("H: "+Arrays.toString(b));
        System.out.println("H: "+H);

        b = playPlatform.getBytes("UTF-8");
        System.out.println("playPlatform: "+Arrays.toString(b));
        System.out.println("playPlatform: "+playPlatform);

        b = ByteBuffer.allocate(4).putInt(idk).array();
        System.out.println("idk (IS UNSIGNED!): "+Arrays.toString(b));
        System.out.println("idk: "+idk);

        if(idk>0)System.out.println("idkey: "+Arrays.toString(idkey));
        if(idk>0)System.out.println("idkey: "+idkey);

    }
    
    @Override
    public String toString() {

        return "HELLO_PACKET: buildVersion=" + buildVersion + " gameId=" + gameId + " guid=" + "---" + " pw="
                + "---" + " secret=" + "---" + " keyTime=" + keyTime + "  key=" + Arrays.toString(key) + " unkStr="
                + unkStr + " pk="+ pk+ " Tq="+ Tq+ " H="+ H+ " playPlatform="+ playPlatform+ " idk="+ idk+ " idkey="+ idkey;
    }
    
    @Override
    public void writeToDataOutput(DataOutput write) throws IOException {
        write.writeUTF(buildVersion);
        write.writeInt(gameId);
        write.writeUTF(guid);
        write.writeUTF(password);
        write.writeUTF(secret);
        write.writeInt(keyTime);

        if (key != null) {
            write.writeShort(key.length);
            write.write(key);
        } else {
            write.writeShort(0);
        }

        if (unkStr != null) {
            byte[] buf = unkStr.getBytes("UTF-8");
            write.writeInt(buf.length);
            write.write(buf);
        } else {
            write.writeInt(0);
        }

        write.writeUTF(pk);
        write.writeUTF(Tq);
        write.writeUTF(H);
        write.writeUTF(playPlatform);
        if (idkey != null) {
            write.writeShort(idkey.length);
            write.write(idkey);
        } else {
            write.writeShort(0);
        }
    }
}

