package com.trader.util;

import com.trader.net.NewTickPacket;
import com.trader.net.UpdatePacket;
import com.trader.net.data.Location;
import com.trader.net.data.ObjectStatus;
import com.trader.net.data.ObjectStatusData;
import com.trader.net.data.StatData;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: PC
 * Date: 12.15.12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */

/**
 * Automatically update all objects/players/monsters & their stats/items
 * That come/leave from game
 */
public class ObjectInfo {
    public List<ObjectStatusData> playerData = new Vector<ObjectStatusData>();
    public int playerId = -1;
    public int traderId = -1;

    public void update(UpdatePacket pktCopy){
        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);

        if(pktCopy.tiles==null && pktCopy.newobjs==null && pktCopy.drops!=null){
            //deleting an object from array...
            //System.out.println("drops.length: "+drops.length);
            for(int drop : pktCopy.drops){
                for(ObjectStatusData osd : playerDataCopy){
                    if(osd!=null)
                        if(osd.objectId==drop){
                            playerData.remove(osd);
                        }
                }
            }
        }else
        if(pktCopy.newobjs!=null)
            for(ObjectStatus os : pktCopy.newobjs){
                boolean alreadyThere = false;
                if(os!=null)
                    if(true){
                        for(ObjectStatusData osd : playerDataCopy)
                            if(osd!=null)
                                if(os.data.objectId==osd.objectId){alreadyThere=true; break;}

                        if(!alreadyThere)
                            playerData.add(os.data);
                    }
            }
    }

    public void remove(int thingToKill){

        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);

        for(ObjectStatusData osd : playerDataCopy){
            if(osd!=null){
                if(osd.objectId==thingToKill){
                    playerData.remove(osd);
                }
            }
        }
    }

    public Location newTick(NewTickPacket pktCopy){
        Location playerLocation = null;
        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);

        for(ObjectStatusData osd : pktCopy.statuses){
            boolean alreadyThere = false;
            if(osd!=null){
                for(ObjectStatusData parentOSD : playerDataCopy)
                    if(parentOSD!=null)
                        if(osd.objectId==parentOSD.objectId){
                            if(osd.objectId==playerId){
                                //System.out.println("NewTickLocation..."+osd.pos.toString());
                                playerLocation = osd.pos;
                            }

                            parentOSD.pos = osd.pos;

                            for(StatData oldStatData : parentOSD.stats){
                                for(StatData newStatData : osd.stats){
                                    if(oldStatData.type == newStatData.type){
                                        oldStatData.value = newStatData.value;
                                        oldStatData.valueString = newStatData.valueString;

                                        //remove the previous one, add the new one.
                                    }
                                }
                            }

                            remove(parentOSD.objectId);
                            playerData.add(parentOSD);
                        }

            }
        }

        return playerLocation;
    }

    public ObjectStatusData getPlayerObject(){
        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);

        for(ObjectStatusData osd : playerDataCopy){
            if(osd.objectId==playerId)
                return  osd;
        }

        return null;
    }

    public int playerNameToId(String name){
        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);
        for(ObjectStatusData osd : playerDataCopy){
            List<StatData> stats = osd.stats;
            for(StatData stat : stats){
                if(stat.type==31){
                    if(stat.valueString.equals(name))
                    return osd.objectId;
                }
            }
        }

        return -1;
    }

    public int[] inventoryFromId(int id){

        int[] inventoryList = new int[8];

        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);
        for(ObjectStatusData osd : playerDataCopy){
            List<StatData> stats = osd.stats;
            if(osd.objectId==id)
            for(StatData stat : stats){
                if(stat.type==12)inventoryList[0]=stat.value;
                if(stat.type==13)inventoryList[1]=stat.value;
                if(stat.type==14)inventoryList[2]=stat.value;
                if(stat.type==15)inventoryList[3]=stat.value;
                if(stat.type==16)inventoryList[4]=stat.value;
                if(stat.type==17)inventoryList[5]=stat.value;
                if(stat.type==18)inventoryList[6]=stat.value;
                if(stat.type==19)inventoryList[7]=stat.value;
            }
        }

        return inventoryList;
    }

    public int[] chestFromId(int id){

        int[] inventoryList = new int[8];

        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);
        for(ObjectStatusData osd : playerDataCopy){
            List<StatData> stats = osd.stats;
            if(osd.objectId==id)
                for(StatData stat : stats){
                    if(stat.type==8)inventoryList[0]=stat.value;
                    if(stat.type==9)inventoryList[1]=stat.value;
                    if(stat.type==10)inventoryList[2]=stat.value;
                    if(stat.type==11)inventoryList[3]=stat.value;
                    if(stat.type==12)inventoryList[4]=stat.value;
                    if(stat.type==13)inventoryList[5]=stat.value;
                    if(stat.type==14)inventoryList[6]=stat.value;
                    if(stat.type==15)inventoryList[7]=stat.value;
                }
        }

        return inventoryList;
    }

    public Location locationFromId(int id){
        List<ObjectStatusData> playerDataCopy = new ArrayList<ObjectStatusData>(playerData);
        for(ObjectStatusData osd : playerDataCopy){
            if(osd.objectId==id){
                return osd.pos;
            }
        }
        return null;
    }

}
