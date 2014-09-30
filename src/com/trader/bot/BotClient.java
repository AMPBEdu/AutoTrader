package com.trader.bot;

import com.trader.util.ObjectInfo;
import com.trader.net.*;
import com.trader.net.data.Item;
import com.trader.net.data.Location;
import com.trader.util.GUID;
import com.trader.util.HexValueParser;
import com.trader.util.RC4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Karlis Purens
 * Date: 12.6.12
 * Time: 22:00
 */
public class BotClient extends Thread{

    private Socket socket;
    public String proxyAddress = "46.137.187.86"; //initial IP


    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private long startTime;
    private long ticker;
    private int tickId;

    private int newTickCounter;

    private long tradeStarted = System.currentTimeMillis();
    private long lastSentRequestTime = 0;
    private long lastCheckTime = 0;
    public long lastTeleportTimer = 0;
    public boolean tradeOpen = false;

    public int[] inventoryItems;
    public int[] chestItems;
    private int[] traderItems;

    public int itemIDBuy = 2671;
    public int itemIDSell = 2672;

    public int itemAmountBuy = 1;
    public int itemAmountSell = 1;

    public String login;
    public String pass;
    public List<String>  itemNameList;
    public List<Integer> itemIdList;
    public List<Integer> alreadyTradedList = new Vector<Integer>();
    public String[] itemNameArray;
    public Integer[] itemIdArray;
    public String textToSend;
    public Integer textInterval;
    public Integer timeOut;
    public boolean botStarted;

    public static byte[] CLIENTKEY = (new HexValueParser()).fromHexString("311F80691451C71B09A13A2A6E");
    public static byte[] SERVERKEY = (new HexValueParser()).fromHexString("72C5583CAFB6818995CBD74B80");

    public RC4 cipherClient = new RC4(SERVERKEY);
    public RC4 cipherServer = new RC4(CLIENTKEY);

    private ObjectInfo objectInfo = new ObjectInfo();

    public int tradesDone;
    public int tradesFailed;
    public int traderId;

    public Location currentPos = new Location(0,0);
    public Location targetPos = new Location(0,0);
    public boolean onlyOncePos = true;
    Random randomMovementGenerator = new Random();
    int randomMovementNum=0;
    public boolean randomMovement;
    public int charID =0;
    public int location =-1;
    public BotGui parentGui;
    public int nextGameId=0;

    public boolean[] playerOffer = new boolean[12];
    public boolean[] traderOffer = new boolean[12];

    public boolean exchangeLock;
    public boolean reconnecting;
    public Random random = new Random();

    public BotClient(BotGui parentGui){
        this.parentGui = parentGui;
    }

	/**
	 * Starts to run a new AutoTrader thread
	 * Intercepts all packets between client and server
	 * And allows to create new packets
	 */
    public void run(){
        try{
            startTime = System.currentTimeMillis();
            ticker = System.currentTimeMillis();
            log("connecting bot socket");
            socket = new Socket(proxyAddress, 2050);
            log("connected to " + proxyAddress);

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            sayHello();

            while(true){

				//Only continue if we have recieved data from server
                if(dataInputStream.available()>0){
                    int length = dataInputStream.readInt();
                    int type = dataInputStream.readByte();
                    byte[] buf = new byte[length - 5];
                    dataInputStream.readFully(buf);
					
                    byte[] decr = cipherClient.rc4(buf);
                    Packet pkt = Packet.parse(type, decr);

					
                    if(pkt.type==Packet.FAILURE){
						//Failure packet - server has not accepted one or more reasons
                        FailurePacket pktCopy = (FailurePacket)pkt;

                        if(!pktCopy.errorDescription.contains("Account in use")){
                            parentGui.locationString = "Logged Out";
                            log("Failure: " + pktCopy.errorId + " " + pktCopy.errorDescription);
                            dataInputStream.close();
                            dataOutputStream.close();
                            parentGui.eStartButton.setEnabled(true);
                            parentGui.startButton.setEnabled(true);
                            parentGui.stopButton.setEnabled(false);
                            parentGui.loginButton.setEnabled(true);
                            parentGui.logoutButton.setEnabled(false);
                            break;
                        }else{
                            //log("Reconnecting because recieved 'Account in use' packet");
                        }
                    }else
                    if(pkt.type==Packet.UPDATE){
						//Update packet - is sent every frame by the server to synchronize
                        UpdatePacket pktCopy = (UpdatePacket)pkt;
                        objectInfo.update(pktCopy);
                        respondToUpdate();
                    }else
                    if(pkt.type==Packet.PING){
						//Ping packet - server checks if the connection is still active
                        if(parentGui.locationString.equals("Vault"))targetPos = objectInfo.locationFromId(104);
                        checkValidity();
                        PingPacket pktCopy = new PingPacket();
                        pktCopy = (PingPacket)pkt;
                        respondToPing(pktCopy.serial);
                    }else
                    if(pkt.type==Packet.MAPINFO){
						//Mapinfo packet - server sends information about the current map
                        MapInfoPacket pktCopy = new MapInfoPacket();
                        pktCopy = (MapInfoPacket)pkt;
                        log(pktCopy.toString());
                        parentGui.locationString = pktCopy.c;
                        respondToMap();
                    }else
                    if(pkt.type==Packet.NEW_TICK){
						//NewTick packet - server sends information regarding movement
                        NewTickPacket pktCopy = (NewTickPacket)pkt;
                        Location newPos = objectInfo.newTick(pktCopy);
                        if(newPos!=null){
                            currentPos = newPos;
                        }

                        int[] chestItemsNew = objectInfo.chestFromId(104);
                        int[] inventoryItemsNew = objectInfo.inventoryFromId(objectInfo.playerId);
                        if(!Arrays.toString(chestItemsNew).equals(Arrays.toString(chestItems))){
                            chestItems = chestItemsNew;
                            parentGui.resetTimer();
                        }else
                        if(!Arrays.toString(inventoryItemsNew).equals(Arrays.toString(inventoryItems))){
                            inventoryItems = inventoryItemsNew;
                            parentGui.resetTimer();
                        }

                        if(random.nextInt(100)>50){
                            if(parentGui.invSwapTest.isSelected())invSwapSlots();
                        }else{
                            if(parentGui.vaultSwap.isSelected())vaultSwap();
                        }

                        if(random.nextInt(100)>50)
                        if(parentGui.invVaultSwap.isSelected())invVaultSwap();

                        tickId = pktCopy.tickId;
                        respondToNewTick();
                    }else
                    if(pkt.type==Packet.CREATE_SUCCESS){
						//CreateSuccess packet - log in has been succesful
                        CreateSuccessPacket pktCopy = (CreateSuccessPacket)pkt;
                        objectInfo.playerId = pktCopy.objectId;
                        log("playerId = " + pktCopy.objectId);
                    }else
                    if(pkt.type==Packet.GOTO){
						//Goto packet - server acknowledges our request for movement
                        respondToGOTO();
                    }else
                    if(pkt.type==Packet.TEXT){
						//Text packet - a player has written a text message
                        TextPacket pktCopy = (TextPacket)pkt;
                        respondToText(pktCopy);
                    }else
                    if(pkt.type==Packet.RECONNECT){
						//Reconnect packet - trying to reconnect after a disconnection
                        ReconnectPacket pktCopy = (ReconnectPacket)pkt;

                        dataInputStream.close();
                        dataOutputStream.close();

                        log(pktCopy.toString());

                        if(pktCopy.name.equals("Vault"))
                        parentGui.clickLogin(-5);else
                        parentGui.clickLogin(-12);

                    }else
                    if(botStarted || exchangeLock==true){
						//These packets are related to trading.
						//Can only be sent if we are not in exchange mode

                        if(pkt.type==Packet.TRADEREQUESTED && tradeOpen == false && exchangeLock == false){
							//TradeRequest packet - sent a trade request to a player
                            parentGui.updateCaret();
                            TradeRequestedPacket pktCopy = (TradeRequestedPacket)pkt;
                            log(pktCopy.toString());
                            startTrade(pktCopy.name);
                        }else
                        if(pkt.type==Packet.TRADECHANGED){
							//TradeChanged packet - current trade has been changed
                            TradeChangedPacket pktCopy = (TradeChangedPacket)pkt;
                            log(pktCopy.toString());
                            if(checkTrade(pktCopy.offer))acceptTrade();
                        }else
                        if(pkt.type==Packet.TRADESTART){
							//TradeStart packet - trade request has been accepted
                            tradeOpen = true;
                            playerOffer = new boolean[12];
                            traderOffer = new boolean[12];
                            TradeStartPacket pktCopy = (TradeStartPacket)pkt;
                            log(pktCopy.toString());
                            selectMyItems();
                        }else
                        if(pkt.type==Packet.TRADEDONE){
							//TradeDone packet - current trade has finished

                            tradeOpen = false;
                            if(exchangeLock){
                                exchangeLock = false; log("Exchange completed - you can trade normal people now");
                                parentGui.eStartButton.setEnabled(true);
                                parentGui.startButton.setEnabled(true);
                                parentGui.stopButton.setEnabled(false);
                            }
                            playerOffer = new boolean[12];
                            traderOffer = new boolean[12];
                            TradeDonePacket pktCopy = (TradeDonePacket)pkt;
                            log(pktCopy.toString());
                            if(pktCopy.code == 0){
                                if(parentGui.soundCheckBox.isSelected())parentGui.sound.playSound(1);
                                log("Trade Succesful!");
                                if(alreadyTradedList.contains(traderId))alreadyTradedList.remove(alreadyTradedList.indexOf(traderId));
                                tradesDone++;
                            }else
                            if(pktCopy.code >= 1){
                                log("Trade Cancelled!");
                                tradesFailed++;
                            }
                            checkValidity();
                        }

                        if(!exchangeLock && tradeOpen == true && System.currentTimeMillis()-tradeStarted > timeOut){
							//Cancelling trade if open for too long
                            log("Trade opened for a long time, cancelling... ");
                            tradesFailed++;
                            tradeOpen = false;
                            cancelTrade();
                        }

                        if(botStarted){
                            long timeSinceLastTick = System.currentTimeMillis()-ticker;
                            if(textInterval<10000)textInterval = 10000;
                            if(textInterval>20000)textInterval = 20000;
                            if(timeSinceLastTick>textInterval && tradeOpen==false){
								//sends a text message after an interval
                                ticker = System.currentTimeMillis();
                                sendText();
                            }
                        }
                    }

                }
            }

        } catch (UnknownHostException e) {
            log("Unknown host: "+proxyAddress);
            logout();
        } catch  (IOException e) {
            log("No I/O "+proxyAddress);
            if(!reconnecting)
            logout();
        }
    }

    public String getVersion(){

        byte[] ver1 = new byte[]{56,46,48};
        String str = null;
        try {
            str = new String(ver1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return str;
    }

	/**
	 * Connect to a server
	 */
    public void sayHello(){
        HelloPacket HPacket = new HelloPacket();
        HPacket.gameId = nextGameId;
        HPacket.buildVersion = parentGui.versionField.getText();
        HPacket.guid = GUID.encrypt(login);
        HPacket.password = GUID.encrypt(pass);
        HPacket.secret = "";
        HPacket.keyTime = -1;
        HPacket.key = new byte[0];
        HPacket.unkStr = "";
        HPacket.pk = ""; // String
        HPacket.Tq = "rotmg"; // String
        HPacket.H = ""; // String
        HPacket.playPlatform = "rotmg"; // String
        HPacket.idk = 0;
        HPacket.idkey = new byte[0];

        commencePacket(HPacket);
    }

	/**
	 * Use a portal in current area with specific number
	 */
    public void usePortal(int num){
        UsePortalPacket UPacket = new UsePortalPacket();
        log("usePortal..." + num);
        UPacket.objectId = num;
        commencePacket(UPacket);
    }

	/**
	 * Escape from the current area
	 */
    public void escape(){
        EscapePacket EPacket = new EscapePacket();
        commencePacket(EPacket);
    }

	/**
	 * Check validity of the current trade parameters
	 */
    public void checkValidity(){
        if(parentGui.locationString.equals("Nexus") && botStarted && !parentGui.dontValidateIfHaveTest.isSelected()){
            inventoryItems = objectInfo.inventoryFromId(objectInfo.playerId);
            int foundMine = 0;
            for(int i = 0; i < 8; i++){
                if(inventoryItems[i]==itemIDSell)foundMine++;
            }

            if(itemIDSell<=0 || (foundMine == 0 || foundMine<itemAmountSell)){
                log("Bot does not have enough items to sell! logging out...");
                logout();
            }
        }
    }

	/**
	 * Respond to text packet
	 */
    public void respondToText(TextPacket TPacket){

        //if name is Akashic && text contains i like pie
        //send message here, have some pie
        if(!exchangeLock && !tradeOpen && botStarted && parentGui.sendTradeRequestsCheckBox.isSelected()){
            if(System.currentTimeMillis()-lastCheckTime>200){
                if(System.currentTimeMillis()-lastSentRequestTime>3000){
                    if(TPacket.text.contains("s>") || TPacket.text.contains("b>") || TPacket.text.contains("sell") || TPacket.text.contains("buy") || TPacket.text.contains("S>") || TPacket.text.contains("B>") || TPacket.text.contains("Sell") || TPacket.text.contains("Buy")){
                        lastCheckTime = System.currentTimeMillis();
                        if(!alreadyTradedList.contains(TPacket.objectId)){
                            int[] strangerItems = objectInfo.inventoryFromId(TPacket.objectId);
                            int wantedAmount = itemAmountBuy;
                            int wantedId = itemIDBuy;
                            for(int i = 0; i < strangerItems.length; i++){
                                if(strangerItems[i]==wantedId)wantedAmount--;
                            }
                            if(wantedAmount<=0){
                                startTrade(TPacket.name);
                                alreadyTradedList.add(TPacket.objectId);
                                lastSentRequestTime = System.currentTimeMillis();
                            }
                        }else{

                        }
                    }
                }
            }
        }
    }

	/**
	 * Start an exchange of items (no the same as a trade)
	 */
    public void startTradeExternal(String name){//starting exchange

        traderId = objectInfo.playerNameToId(name);
        if(traderId>0 && !tradeOpen){
            log("Exchange lock is activated! Bot will not do normal trades until you finish exchange or logout!");
            exchangeLock = true;
            startTrade(name);
        }else{
            log("No such player on this server! or a trade is already open.");
            parentGui.eStartButton.setEnabled(true);
            parentGui.startButton.setEnabled(true);
        }
    }

	/**
	 * Start a trade
	 */
    public void startTrade(String name){
        RequestTradePacket RPacket = new RequestTradePacket();
        RPacket.name = name;
        traderId = objectInfo.playerNameToId(name);
        log("TraderId is: " + traderId);
        objectInfo.traderId = traderId;

        if(traderId>0){

            inventoryItems = objectInfo.inventoryFromId(objectInfo.playerId);
            traderItems = objectInfo.inventoryFromId(objectInfo.traderId);

            int wantedItemCount = 0;
            for(int i = 0; i<traderItems.length;i++){
                if(traderItems[i]==itemIDBuy)wantedItemCount++;
            }

            if(wantedItemCount>=itemAmountBuy || exchangeLock==true){
                log("Sending trade request to: " + name);
                tradeStarted = System.currentTimeMillis();
                commencePacket(RPacket);
            }else{
                log("Not accepting the trade request - didn't find wanted item{s}.");
            }
        }else{
            log("No such player on this server! is the name entered correctly?");
        }
    }

	/**
	 * Check if current trade is acceptable
	 */
    public boolean checkTrade(boolean[] booleans){
	
        int selectedAmount = 0;

        if(booleans[4]==true)if(traderItems[0]==itemIDBuy)selectedAmount++;
        if(booleans[5]==true)if(traderItems[1]==itemIDBuy)selectedAmount++;
        if(booleans[6]==true)if(traderItems[2]==itemIDBuy)selectedAmount++;
        if(booleans[7]==true)if(traderItems[3]==itemIDBuy)selectedAmount++;
        if(booleans[8]==true)if(traderItems[4]==itemIDBuy)selectedAmount++;
        if(booleans[9]==true)if(traderItems[5]==itemIDBuy)selectedAmount++;
        if(booleans[10]==true)if(traderItems[6]==itemIDBuy)selectedAmount++;
        if(booleans[11]==true)if(traderItems[7]==itemIDBuy)selectedAmount++;

        traderOffer = booleans;

        if(selectedAmount>=itemAmountBuy || exchangeLock == true){
            return true;
            //acceptTrade();
        }
        return false;
    }

	/**
	 * Select items we wish to trade
	 */
    public void selectMyItems(){

        ChangeTradePacket CPacket = new ChangeTradePacket();

        if(exchangeLock == false){

            int selectedAmount = 0;
            if(inventoryItems[0]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[4]=true; selectedAmount++;}
            if(inventoryItems[1]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[5]=true; selectedAmount++;}
            if(inventoryItems[2]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[6]=true; selectedAmount++;}
            if(inventoryItems[3]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[7]=true; selectedAmount++;}
            if(inventoryItems[4]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[8]=true; selectedAmount++;}
            if(inventoryItems[5]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[9]=true; selectedAmount++;}
            if(inventoryItems[6]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[10]=true; selectedAmount++;}
            if(inventoryItems[7]==itemIDSell)if(selectedAmount<itemAmountSell){playerOffer[11]=true; selectedAmount++;}
            log("selectedAmount..."+selectedAmount);
        }else{
            if(parentGui.eCheckBox1.isSelected()){playerOffer[4]=true;  }
            if(parentGui.eCheckBox2.isSelected()){playerOffer[5]=true; }
            if(parentGui.eCheckBox3.isSelected()){playerOffer[6]=true; }
            if(parentGui.eCheckBox4.isSelected()){playerOffer[7]=true; }
            if(parentGui.eCheckBox5.isSelected()){playerOffer[8]=true; }
            if(parentGui.eCheckBox6.isSelected()){playerOffer[9]=true; }
            if(parentGui.eCheckBox7.isSelected()){playerOffer[10]=true; }
            if(parentGui.eCheckBox8.isSelected()){playerOffer[11]=true; }
        }

        CPacket.offer = playerOffer;
        commencePacket(CPacket);
    }

	/**
	 * Deselect all trade items
	 */
    public void deselectAll(){
        playerOffer = new boolean[12];
        ChangeTradePacket CPacket = new ChangeTradePacket();
        boolean[] booleans = new boolean[12];
        CPacket.offer = booleans;
        commencePacket(CPacket);
    }

	/**
	 * Cancel the current trade
	 */
    public void cancelTrade(){
        try {
            sendPacket(Packet.CANCELTRADE, new byte[]{});
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	/**
	 * Accept the current trade
	 */
    public void acceptTrade(){

       AcceptTradePacket APacket = new AcceptTradePacket();
       APacket.myOffer = playerOffer;
       APacket.yourOffer = traderOffer;

       log("Accepting trade: "+APacket.toString());

       commencePacket(APacket);
    }

	/**
	 * Testing a drop packet
	 */
    public void drop(){
        System.err.println("Dropping free party hats!");
        InvDropPacket IPacket = new InvDropPacket();
        Item dropItem = new Item();
        dropItem.objectId = objectInfo.playerId;
        dropItem.slotId = 4;
        dropItem.itemType = inventoryItems[0];
        IPacket.item = dropItem;
        commencePacket(IPacket);
    }

	/**
	 * Initiating the swap of two inventory items
	 */
    public void invSwapSlots(){

        if(inventoryItems!=null)
        for(int i = 0; i < 1; i++){
            int s1 = random.nextInt(8)+4;
            int s2 = random.nextInt(8)+4;

            Item i1 = new Item();
            i1.objectId = objectInfo.playerId;
            i1.slotId = s1;
            i1.itemType = inventoryItems[s1-4];

            Item i2 = new Item();
            i2.objectId = objectInfo.playerId;
            i2.slotId = s2;
            i2.itemType = inventoryItems[s2-4];

            invSwap(i1,i2);
        }
    }

	/**
	 * Swapping two vault items
	 */
    public void vaultSwap(){
        if(chestItems!=null){
                int s1 = random.nextInt(8);
                int s2 = random.nextInt(8);

                Item i1 = new Item();
                i1.objectId = 104;
                i1.slotId = s1;
                i1.itemType = chestItems[s1];

                Item i2 = new Item();
                i2.objectId = 104;
                i2.slotId = s2;
                i2.itemType = chestItems[s2];

                invSwap(i1,i2);
            }
    }

	/**
	 * Swapping two vault items
	 */
    public void invVaultSwap(){
        int i1 = random.nextInt(8);
        int i2 = random.nextInt(8);
        takeItem(i1);
        putItem(i2);
    }

	/**
	 * Swapping two items
	 */
    public void invSwap(Item i1, Item i2){
        log("invSwap..." + i1.toString() + "..." + i2.toString());

        InvSwapPacket IPacket=new InvSwapPacket();
        long notVeryLong = System.currentTimeMillis()-startTime;
        IPacket.time = (int)notVeryLong;
        IPacket.loc = objectInfo.locationFromId(objectInfo.playerId);
        IPacket.item1 = i1;
        IPacket.item2 = i2;
        commencePacket(IPacket);
    }

	/**
	 * Respond to update packet
	 */
    public void respondToUpdate(){
        UpdateAckPacket UACK = new UpdateAckPacket();
        commencePacket(UACK);
    }

	/**
	 * Respond to ping packet
	 */
    public void respondToPing(int num){
        PongPacket Pong = new PongPacket();
        Pong.serial = num;
        long notVeryLong = System.currentTimeMillis()-startTime;
        Pong.time = (int)notVeryLong;
        commencePacket(Pong);
    }

	/**
	 * Respond to map load packet
	 */
    public void respondToMap(){
        LoadPacket Load = new LoadPacket();
        Load.charId = charID;
        commencePacket(Load);
    }

	/**
	 * Respond to new tick packet
	 */
    public void respondToNewTick(){
        if(onlyOncePos == true){
            targetPos.x = currentPos.x;
            targetPos.y = currentPos.y;
            onlyOncePos = false;
        }

        newTickCounter++;
        if(newTickCounter>randomMovementNum && randomMovement==true){
            log("Doing random movement");
            randomMovementNum = randomMovementGenerator.nextInt(999);
            targetPos.x+=randomMovementGenerator.nextDouble()*1.5;
            targetPos.x-=randomMovementGenerator.nextDouble()*1.5;
            targetPos.y+=randomMovementGenerator.nextDouble()*1.5;
            targetPos.y-=randomMovementGenerator.nextDouble()*1.5;
            newTickCounter=0;
        }

        double movementAmount = 0.4;

        if(Math.abs(targetPos.x - currentPos.x)>movementAmount){
            if(targetPos.x > currentPos.x)currentPos.x+=movementAmount; else
            if(targetPos.x < currentPos.x)currentPos.x-=movementAmount;
        }

        if(Math.abs(targetPos.y - currentPos.y)>movementAmount){
            if(targetPos.y > currentPos.y)currentPos.y+=movementAmount; else
            if(targetPos.y < currentPos.y)currentPos.y-=movementAmount;
        }
        long notVeryLong = System.currentTimeMillis()-startTime;
        MovePacket MPacket = new MovePacket();
        MPacket.tickId = tickId;
        MPacket.time = (int)notVeryLong;
        MPacket.newPosition = currentPos;
        commencePacket(MPacket);
    }

	/**
	 * Respond to GOTO packet
	 */
    public void respondToGOTO(){

        GotoAckPacket GPacket = new GotoAckPacket();
        long notVeryLong = System.currentTimeMillis()-startTime;
        GPacket.time = (int)notVeryLong;
        commencePacket(GPacket);
    }

	/**
	 * Take item from vault check
	 */
    public void takeItem(int index){
        boolean hasFreeSpace=false;
        Item i1 = new Item();
        i1.objectId = 104;
        i1.slotId = index;
        i1.itemType = chestItems[index];

        Item i2 = new Item();
        i2.objectId = 103;
        for(int temp = 0; temp<inventoryItems.length; temp++){
            if(inventoryItems[temp]<=0){
                hasFreeSpace = true;
                log("There is a free inventory slot - using it..." + temp);
                i2.slotId = temp+4;
                i2.itemType = -1;
                break;
            }
        }

        if(hasFreeSpace)
        invSwap(i1, i2);
        else
        log("No free inventory space!");
    }

	/**
	 * Put item in vault chest
	 */
    public void putItem(int index){
        boolean hasFreeSpace=false;
        Item i1 = new Item();
        i1.objectId = 103;
        i1.slotId = index+4;
        i1.itemType = inventoryItems[index];

        Item i2 = new Item();
        i2.objectId = 104;
        for(int temp = 0; temp<chestItems.length; temp++){
            if(chestItems[temp]<=0){
                hasFreeSpace = true;
                log("There is a free vault slot - using it..." + temp);
                i2.slotId = temp;
                i2.itemType = -1;
                break;
            }
        }

        if(hasFreeSpace)
        invSwap(i1, i2);
        else
        log("No free vault space!");
    }



	/**
	 * Send text to the global chat system
	 */
    public void sendText(){

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(16);

        if(this.textToSend!=null && this.textToSend!="" && this.textToSend.length()>3){
            PlayerTextPacket PPacket = new PlayerTextPacket();
            PPacket.text = this.textToSend;
            log("Sending AutoTalker text");
            if(randomInt==0)PPacket.text += "";else
            if(randomInt==1)PPacket.text += " ";else
            if(randomInt==2)PPacket.text += "  ";else
            if(randomInt==3)PPacket.text += ".";else
            if(randomInt==4)PPacket.text += ". ";else
            if(randomInt==5)PPacket.text += ".  ";else
            if(randomInt==6)PPacket.text += "    ";else
            if(randomInt==7)PPacket.text += "!";else
            if(randomInt==8)PPacket.text += "! ";else
            if(randomInt==9)PPacket.text += "!  ";else
            if(randomInt==10)PPacket.text = " "+PPacket.text;else
            if(randomInt==11)PPacket.text = "  "+PPacket.text;else
            if(randomInt==12)PPacket.text = "."+PPacket.text;else
            if(randomInt==13)PPacket.text = ". "+PPacket.text;else
            if(randomInt==14)PPacket.text += "-";else
            if(randomInt==15)PPacket.text = "-"+PPacket.text;

            commencePacket(PPacket);
        }
    }

	/**
	 * Teleport to a player with specific name
	 */
    public void teleportToName(String name){
        TeleportPacket TPacket = new TeleportPacket();
        TPacket.objectId = objectInfo.playerNameToId(name);
        if(TPacket.objectId>0){
            log("Teleporting to player with id: "+TPacket.objectId+"");
            targetPos = objectInfo.locationFromId(TPacket.objectId);
            lastTeleportTimer = System.currentTimeMillis();
            commencePacket(TPacket);
        }else{
            log("No such player on this server! is the name entered correctly?");
        }
    }

	/**
	 * Logout from the game
	 */
    public void logout(){
        try {
            exchangeLock=false;
            if(parentGui.soundCheckBox.isSelected())parentGui.sound.playSound(2);
            parentGui.locationString = "Logged Out";
            log("Logged out succesfully...");
            dataInputStream.close();
            dataOutputStream.close();
            parentGui.stopButton.setEnabled(false);
            parentGui.startButton.setEnabled(true);
            parentGui.eStartButton.setEnabled(true);
            parentGui.loginButton.setEnabled(true);
            parentGui.logoutButton.setEnabled(false);

            inventoryItems = objectInfo.inventoryFromId(objectInfo.playerId);
            int foundMine = 0;
            for(int i = 0; i < 8; i++){
                if(inventoryItems[i]==itemIDSell)foundMine++;
            }

            if(itemIDSell<=0 || (foundMine == 0 || foundMine<itemAmountSell)){
                //player doesn't have any more items to sell - do nothing
            }else{
                //player still has items to sell - re-logging him in!
                parentGui.previousPos = currentPos;
                parentGui.continueTrade=true;
                parentGui.clickLogin(nextGameId);
            }

        } catch (IOException e) {
            log("Loggout error...");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	/**
	 * Encrypt and prepare a specific packet
	 */
    private void commencePacket(Packet pkt){
        try{
            byte[] notcryptedBytePacket;
            ByteArrayDataOutput bado = new ByteArrayDataOutput(99999);
            pkt.writeToDataOutput(bado);
            notcryptedBytePacket = bado.getArray();
            sendPacket(pkt.type, notcryptedBytePacket);
        }catch (Exception e){
            log("commencePacket error: " + e.getCause());
            //e.printStackTrace();
        }
    }

	/**
	 * Send a specific packet
	 */
    private void sendPacket(int type, byte[] paramArrayOfByte) throws IOException {

        paramArrayOfByte = cipherServer.rc4(paramArrayOfByte);

        //System.out.println("crypted: "+ Arrays.toString(paramArrayOfByte));

        dataOutputStream.writeInt(paramArrayOfByte.length + 5);
        dataOutputStream.writeByte(type);
        dataOutputStream.write(paramArrayOfByte);
        dataOutputStream.flush();
        
    }

	/**
	 * Log info in console
	 */
    private void log(String str){
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        System.out.println(timeFormat.format(now) + ": " + str);
    }
}
