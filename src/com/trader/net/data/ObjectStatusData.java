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

package com.trader.net.data;

import com.trader.util.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class ObjectStatusData implements Parsable {
	
	public int objectId; //int
	public Location pos;
	public List<StatData> stats;
	
	public ObjectStatusData() {
		this(0, new Location(), new Vector<StatData>());
	}
	
	public ObjectStatusData(int id, Location l, List<StatData> stat) {
		objectId = id;
		pos = l.clone();
		stats = stat;
	}
	
	public ObjectStatusData(DataInput in) {
		try {
			parseFromDataInput(in);
			
		} catch (IOException e) {
			objectId = 0;
			pos = new Location();
			stats = new Vector<StatData>();
		}
	}
	
	@Override
	public void writeToDataOutput(DataOutput out) throws IOException {
		out.writeInt(objectId);
		pos.writeToDataOutput(out);
		Serializer.writeArray(out, stats.toArray(new Parsable[stats.size()]));
	}

	@Override
	public void parseFromDataInput(DataInput in) throws IOException {
		objectId = in.readInt();
		pos = new Location(in);
		
		int size = in.readShort();
		stats = new Vector<StatData>();
		for (int i = 0; i < size; i++) {
			StatData inp = new StatData(in,objectId);
			stats.add(inp);
		}
	}
	
	public void updateStat(StatData in) {
		for (StatData myStat : stats) {
			if (in.type == myStat.type) {
				myStat.value = in.value;
				myStat.valueString = in.valueString;
				return;
			}
		}
		stats.add(in);
	}
	
	public StatData getStat(int type) {
		StatData ret = null;
		for (StatData s : stats) {
			if (s.type == type) {
				ret = s;
			}
		}
		return ret;
	}

    public void printAll(){
         for(StatData stat : stats){
             System.out.println("Type: "+stat.type+" ValInt: "+stat.value+" ValString: "+stat.valueString);
         }
    }
	
	@Override
	public String toString() {
		return "ObjectStatusData {" + objectId + " " + pos + " " + stats + "}";
	}

}
