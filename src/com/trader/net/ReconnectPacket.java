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
import java.util.Arrays;

public class ReconnectPacket extends Packet implements Parsable {
	public String name;
	public String host;
	public int port; //int
	public int gameId; //int
	public int keyTime; //int
	public byte[] key;
	
	public ReconnectPacket(DataInput in) {
		try {
			type = Packet.RECONNECT;
            resetPlayerData();
			parseFromDataInput(in);

		} catch (IOException e) {
			
		}
	}
	
	public ReconnectPacket() {
		type = Packet.RECONNECT;
        resetPlayerData();
	}

    public void resetPlayerData(){
        //parentProxy.playerData = new Vector<ObjectStatusData>();
    }
	
	@Override
	public void parseFromDataInput(DataInput in) throws IOException {

		name = in.readUTF();
        host = in.readUTF();
		port = in.readInt();
		gameId = in.readInt();
		keyTime = in.readInt();
		int size = in.readShort();
		key = new byte[size];
		in.readFully(key);

        System.out.println("RECONNECT PACKET: ");
        System.out.println("name: "+name);
        System.out.println("host: "+host);
        System.out.println("port: "+port);
        System.out.println("gameId: "+gameId);
        System.out.println("keyTime: "+keyTime);
        System.out.println("size: "+size);
        System.out.println("key: "+Arrays.toString(key));

        //HAS TO BE HERE! to check if name is vault/nexus

        /*
        if(host.length()>3)
        if(!name.contains("Vault") && !name.contains("Nexus")){
            parentProxy.writeToFile(host);

            parentProxy.lastIP=parentProxy.nextIP;
            parentProxy.lastIPBig=parentProxy.nextIPBig;
            parentProxy.nextIPBig=host;
            parentProxy.nextIP=(new ipSwitch()).shorten(host);

            System.out.println("parentProxy.lastIP: |"+parentProxy.lastIP+"|");
            System.out.println("parentProxy.lastIPBig: |"+parentProxy.lastIPBig+"|");
            System.out.println("parentProxy.nextIP: |"+parentProxy.nextIP+"|");
            System.out.println("parentProxy.nextIPBig: |"+parentProxy.nextIPBig+"|");

        }

        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        if(host.length()>3)
        name = "";//can be an issue? unknown if allowed in all servers/realms to be null
        */

	}
	
	@Override
	public void writeToDataOutput(DataOutput out) throws IOException {


		out.writeUTF(name);
		out.writeUTF(host);
		out.writeInt(port);
		out.writeInt(gameId);
		out.writeInt(keyTime);
		out.writeShort(key.length);
		out.write(key);
	}
	
	@Override
	public String toString() {
		return "RECONNECT " + name + " " + host + ":" + port + " " + gameId + " " + keyTime + " " + Arrays.toString(key);
	}
}
