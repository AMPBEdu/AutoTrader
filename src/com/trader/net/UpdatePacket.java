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

import com.trader.net.data.ObjectStatus;
import com.trader.net.data.Parsable;
import com.trader.net.data.Tile;
import com.trader.util.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class UpdatePacket extends Packet implements Parsable {

	public List<Tile> tiles;
	public List<ObjectStatus> newobjs;
	public int[] drops; //int array
	
	public UpdatePacket(DataInput in) {
		try {
			this.type = Packet.UPDATE;
			parseFromDataInput(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public UpdatePacket() {
		type = Packet.UPDATE;
	}
	
	@Override
	public void writeToDataOutput(DataOutput out) throws IOException {
		if (tiles != null && tiles.size() > 0) {
			Serializer.writeArray(out, tiles.toArray(new Parsable[tiles.size()]));
		} else {
			out.writeShort(0);
		}
		
		if (newobjs != null && newobjs.size() > 0) {
			Serializer.writeArray(out, newobjs.toArray(new Parsable[newobjs.size()]));
		} else {
			out.writeShort(0);
		}
		
		if (drops != null) {
			Serializer.writeArray(out, drops, Serializer.AS_INT);
		} else {
			out.writeShort(0);
		}
	}

	@Override
	public void parseFromDataInput(DataInput in) throws IOException {

		int size = in.readShort();
		if (size > 0) {
			tiles = new Vector<Tile>();
			for (int i = 0; i < size; i++) {
				tiles.add(new Tile(in));
			}
		} else {
			tiles = null;
		}
		
		size = in.readShort();
		if (size > 0) {
			newobjs = new Vector<ObjectStatus>(size);
			for (int i = 0; i < size; i++) {
				newobjs.add(new ObjectStatus(in));
			}
		} else {
			newobjs = null;
		}
		
		size = in.readShort();
		if (size > 0) {
			drops = new int[size];
			for (int i = 0; i < size; i++) {
				drops[i] = in.readInt();
			}
		} else {
			drops = null;
		}
        /*
        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(parentProxy.playerData);

        if(tiles==null && newobjs==null){
            //deleting an object from array...
            //System.out.println("drops.length: "+drops.length);
            for(int drop : drops){
                for(ObjectStatusData osd : playerDataCopy){
                    if(osd!=null)
                    if(osd.objectId==drop){
                         parentProxy.playerData.remove(osd);
                    }
                }
            }
        }else
        if(newobjs!=null)
        for(ObjectStatus os : newobjs){
            boolean alreadyThere = false;
            if(os!=null)
            if(true){
                for(ObjectStatusData osd : playerDataCopy)
                if(osd!=null)
                if(os.data.objectId==osd.objectId){alreadyThere=true; break;}

                if(!alreadyThere)
                parentProxy.playerData.add(os.data);
            }
        }
        */
	}
	
	@Override
	public String toString() {
		return "UPDATE " + tiles + " " + newobjs + " " + Arrays.toString(drops);
	}

}
