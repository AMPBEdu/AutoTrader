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

/**
 * <p>
 * A generic packet.
 * </p>
 * <p>
 * Started Mar 2, 2011
 * </p>
 * 
 * @author Furyhunter
 */
public class Packet implements Parsable {
    
	public final static int FAILURE = 0; 
	public final static int CANCELTRADE = 1; 
	public final static int USEPORTAL = 3; 
	public final static int INVRESULT = 4; 
	public final static int JOINGUILD = 5; 
	public final static int PING = 6; 
	public final static int MOVE = 7; 
	public final static int GUILDINVITE = 8; 
	public final static int GLOBAL_NOTIFICATION = 9;
	public final static int SETCONDITION = 10; 
	public final static int UPDATEACK = 11; 
	public final static int TRADEDONE = 12; 
	public final static int SHOOT = 13; 
	public final static int GOTOACK = 14; 
	public final static int CREATEGUILD = 15; 
	public final static int PONG = 16; 
	public final static int HELLO = 17; 
	public final static int TRADEACCEPTED = 18; 
	public final static int SHOOTMULTI = 19; 
	public final static int NAMERESULT = 20; 
	public final static int REQUESTTRADE = 21; 
	public final static int SHOOTACK = 22; 
	public final static int TRADECHANGED = 23; 
	public final static int PLAYERHIT = 24; 
	public final static int TEXT = 25; 
	public final static int UPDATE = 26; 
	public final static int BUYRESULT = 27; 
	public final static int PIC = 28; 
	public final static int USEITEM = 30; 
	public final static int CREATE_SUCCESS = 31; 
	public final static int CHOOSENAME = 33; 
	public final static int QUESTOBJID = 34; 
	public final static int INVDROP = 35; 
	public final static int CREATE = 36; 
	public final static int CHANGETRADE = 37; 
	public final static int PLAYERSHOOT = 38; 
	public final static int RECONNECT = 39; 
	public final static int CHANGEGUILDRANK = 40; 
	public final static int DEATH = 41; 
	public final static int ESCAPE = 42; 
	public final static int PLAYSOUND = 44; 
	public final static int LOAD = 45; 
	public final static int ACCOUNTLIST = 46; 
	public final static int DAMAGE = 47; 
	public final static int CHECKCREDITS = 48; 
	public final static int TELEPORT = 49; 
	public final static int BUY = 50; 
	public final static int SQUAREHIT = 51; 
	public final static int GOTO = 52; 
	public final static int EDITACCOUNTLIST = 53; 
	public final static int CLIENTSTAT_FILE = 55; 
	public final static int SHOW_EFFECT = 56; 
	public final static int ACCEPTTRADE = 57; 
	public final static int CREATEGUILDRESULT = 58; 
	public final static int AOEACK = 59; 
	public final static int MAPINFO = 60; 
	public final static int TRADEREQUESTED = 61; 
	public final static int NEW_TICK = 62; 
	public final static int NOTIFICATION = 63; 
	public final static int GROUNDDAMAGE = 64; 
	public final static int INVSWAP = 65; 
	public final static int OTHERHIT = 66; 
	public final static int TRADESTART = 67; 
	public final static int AOE = 68; 
	public final static int PLAYERTEXT = 69; 
	public final static int ALLYSHOOT = 74; 
	public final static int CLIENTSTAT = 75; 
	public final static int ENEMYHIT = 76; 
	public final static int INVITEDTOGUILD = 77; 
	public final static int GUILDREMOVE = 78;
    
    public int type;
    
    private byte[] data;
    
    protected Packet() {
        
    }
    
    protected Packet(int type, byte[] data) {
        this.type = type;
        this.data = data.clone();
    }
    
    public String toString() {
        return "Unknown type " + type + " " + Arrays.toString(data);
    }
    
    @Override
    public void writeToDataOutput(DataOutput out) throws IOException {
        out.write(data);
    }
    
    @Override
    public void parseFromDataInput(DataInput in) throws IOException {
        in.readFully(data);
    }
    
    public static Packet parse(int type, byte[] data) {

        DataInput in = new ByteArrayDataInput(data);
        switch (type) {
            case FAILURE:
                return new FailurePacket(in);
            case CREATE_SUCCESS:
                return new CreateSuccessPacket(in);//@@@
            case CREATE:
                return new CreatePacket(in);
            case PLAYERSHOOT:
                return new PlayerShootPacket(in);
            case MOVE:
                return new MovePacket(in);
            case PLAYERTEXT:
                return new PlayerTextPacket(in);
            case TEXT:
                return new TextPacket(in);
            case SHOOT:
                return new ShootPacket(in);
            case HELLO:
                return new HelloPacket(in);
            case DAMAGE:
            	return new DamagePacket(in);
            case UPDATE:
            	return new UpdatePacket(in); //@@@
            case UPDATEACK:
            	return new UpdateAckPacket(in);
            case NOTIFICATION:
            	return new NotificationPacket(in);
            case NEW_TICK:
            	return new NewTickPacket(in); //@@@
            case INVSWAP:
            	return new InvSwapPacket(in);
            case USEITEM:
            	return new UseItemPacket(in);
            case SHOW_EFFECT:
            	return new ShowEffectPacket(in);
            case GOTO:
            	return new GotoPacket(in);
            case INVDROP:
            	return new InvDropPacket(in);
            case INVRESULT:
            	return new InvResultPacket(in);
            case RECONNECT:
            	return new ReconnectPacket(in); //@@@
            case PING:
            	return new PingPacket(in);
            case PONG:
            	return new PongPacket(in);
            case MAPINFO:
            	return new MapInfoPacket(in);
            case LOAD:
            	return new LoadPacket(in);
            case PIC:
            	return new PicPacket(in);
            case SETCONDITION:
            	return new SetConditionPacket(in);
            case TELEPORT:
            	return new TeleportPacket(in);
            case USEPORTAL:
            	return new UsePortalPacket(in);
            case DEATH:
            	return new DeathPacket(in);
            case BUY:
            	return new BuyPacket(in);
            case BUYRESULT:
            	return new BuyResultPacket(in);
            case AOE:
            	return new AOEPacket(in);
            case GROUNDDAMAGE:
            	return new GroundDamagePacket(in);
            case PLAYERHIT:
            	return new PlayerHitPacket(in);
            case ENEMYHIT:
            	return new EnemyHitPacket(in);
            case AOEACK:
            	return new AOEAckPacket(in);
            case SHOOTACK:
            	return new ShootAckPacket(in);
            case OTHERHIT:
            	return new OtherHitPacket(in);
            case SQUAREHIT:
            	return new SquareHitPacket(in);
            case GOTOACK:
            	return new GotoAckPacket(in);
            case EDITACCOUNTLIST:
            	return new EditAccountListPacket(in);
            case ACCOUNTLIST:
            	return new AccountListPacket(in);
            case QUESTOBJID:
            	return new QuestObjIDPacket(in);
            case CHOOSENAME:
            	return new ChooseNamePacket(in);
            case NAMERESULT:
            	return new NameResultPacket(in);
            case CREATEGUILD:
            	return new CreateGuildPacket(in);
            case CREATEGUILDRESULT:
            	return new CreateGuildResultPacket(in);
            case GUILDREMOVE:
            	return new GuildRemovePacket(in);
            case GUILDINVITE:
            	return new GuildInvitePacket(in);
            case ALLYSHOOT:
            	return new AllyShootPacket(in);
            case SHOOTMULTI:
            	return new EnemyShootPacket(in);
            case REQUESTTRADE:
            	return new RequestTradePacket(in);
            case TRADEREQUESTED:
            	return new TradeRequestedPacket(in);
            case TRADESTART:
            	return new TradeStartPacket(in);
            case CHANGETRADE:
            	return new ChangeTradePacket(in);
            case TRADECHANGED:
            	return new TradeChangedPacket(in);
            case ACCEPTTRADE:
            	return new AcceptTradePacket(in);
            case CANCELTRADE:
            	return new CancelTradePacket(in);
            case TRADEDONE:
            	return new TradeDonePacket(in);
            case TRADEACCEPTED:
            	return new TradeAcceptedPacket(in);
            case CLIENTSTAT:
            	return new ClientStatPacket(in);
            case CHECKCREDITS:
            	return new CheckCreditsPacket(in);
            case ESCAPE:
            	return new EscapePacket(in); //@@@
            case CLIENTSTAT_FILE:
            	return new FilePacket(in);
            case INVITEDTOGUILD:
            	return new InvitedToGuildPacket(in);
            case JOINGUILD:
            	return new JoinGuildPacket(in);
            case CHANGEGUILDRANK:
            	return new ChangeGuildRankPacket(in);
            default:
                return new Packet(type, data);
        }
    }
}
