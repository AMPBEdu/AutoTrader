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
import java.io.DataOutput;
import java.io.IOException;

import com.trader.net.data.Parsable;

public class NotificationPacket extends Packet implements Parsable {

	public int objectId; //int
	public String text; //UTF
	public int color; //int
	
	public NotificationPacket() {
		this(0, "", 0);
	}
	
	public NotificationPacket(int id, String txt, int col) {
		type = Packet.NOTIFICATION;
		objectId = id;
		text = new String(txt);
		color = col;
	}
	
	public NotificationPacket(DataInput in) {
		try {
			type = Packet.NOTIFICATION;
			parseFromDataInput(in);
		} catch (IOException e) {
			
		}
	}
	
	@Override
	public void writeToDataOutput(DataOutput out) throws IOException {
		out.writeInt(objectId);
		out.writeUTF(text);
		out.writeInt(color);
	}
	
	@Override
	public void parseFromDataInput(DataInput in) throws IOException {
		objectId = in.readInt();
		text = in.readUTF();
		color = in.readInt();
	}
	
	@Override
	public String toString() {
		return "NOTIFICATION " + objectId + " " + text + " " + color;
	}
}
