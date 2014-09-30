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
import java.nio.charset.Charset;
import java.util.*;

import com.trader.net.data.Parsable;

public class MapInfoPacket extends Packet implements Parsable {

    public int a;
    public int b;
    public String c;
    private String h;
    private int i;
    public int d;
    public int e;
    public Boolean f;
    public Boolean g;
    private ArrayList j;
    private ArrayList k;

    public MapInfoPacket(DataInput in) {
        type = Packet.MAPINFO;
        parseFromDataInput(in);
    }

    public MapInfoPacket() {
        type = Packet.MAPINFO;
    }

    @Override
    public final void parseFromDataInput(DataInput in)
    {
        try{
        a = in.readInt();
        b = in.readInt();
        c = in.readUTF();
        h = in.readUTF();
        i = in.readInt();
        d = in.readInt();
        e = in.readInt();
        f = Boolean.valueOf(in.readBoolean());
        g = Boolean.valueOf(in.readBoolean());


        int m = in.readUnsignedShort();

        j = new ArrayList(m);
        if(m>0)
        for (int n = 0; n < m; n++)
        {
            int i1 = in.readInt();
            byte[] localObject = new byte[i1];
            in.readFully(localObject);
            String newStr = new String(localObject, Charset.forName("UTF-8"));
            j.add(newStr);
        }
        int n = in.readUnsignedShort();
        k = new ArrayList(n);
        if(n>0)
        for (int i1 = 0; i1 < n; i1++)
        {
            int i2 = in.readInt();
            byte[] arrayOfByte = new byte[i2];
            in.readFully(arrayOfByte);
            String str = new String(arrayOfByte, Charset.forName("UTF-8"));
            k.add(str);
        }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public final void writeToDataOutput(DataOutput out)
    {
        try{
        out.writeInt(a);
        out.writeInt(b);
        out.writeUTF(c);
        out.writeUTF(h);
        out.writeInt(i);
        out.writeInt(d);
        out.writeInt(e);
        out.writeBoolean(f.booleanValue());
        out.writeBoolean(g.booleanValue());
        out.writeShort(j.size());
        Iterator localIterator = j.iterator();
        String str;
        while (localIterator.hasNext())
        {
            str = (String)localIterator.next();
            out.writeBytes(str);
        }
        out.writeShort(k.size());
        localIterator = k.iterator();
        while (localIterator.hasNext())
        {
            str = (String)localIterator.next();
            out.writeBytes(str);
        }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public final int a()
    {
        return 60;
    }

    @Override
    public final String toString()
    {
        byte[] bytes;

        /*
        bytes = ByteBuffer.allocate(4).putInt(a).array();
        System.out.println("Bytes: a="+Arrays.toString(bytes));
        bytes = ByteBuffer.allocate(4).putInt(b).array();
        System.out.println("Bytes: b="+Arrays.toString(bytes));

        System.out.println("Bytes: c="+Arrays.toString(c.getBytes()));
        System.out.println("Bytes: h="+Arrays.toString(h.getBytes()));

        bytes = ByteBuffer.allocate(4).putInt(i).array();
        System.out.println("Bytes: i="+Arrays.toString(bytes));
        bytes = ByteBuffer.allocate(4).putInt(d).array();
        System.out.println("Bytes: d="+Arrays.toString(bytes));
        bytes = ByteBuffer.allocate(4).putInt(e).array();
        System.out.println("Bytes: e="+Arrays.toString(bytes));
        */

        return "MAPINFO_PACKET: [a=" + a + " , b=" + b + " , c=" + c + " , h=" + h + " , i=" + i + " , nothing_here" + " , d=" + d + " , e=" + e + " , f=" + f + " , g=" + g + " , j=" + j + " , k=" + k + "]";
    }
}
