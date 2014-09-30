package com.trader.bot;

import com.trader.net.data.Location;
import com.trader.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Karlis Purens
 * Date: 12.22.12
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
public class BotGui {
    public JPanel panel1;
    public JTabbedPane tabbedPane1;
    public JFormattedTextField loginField;
    public JPasswordField passwordField;
    public JComboBox serverBox;
    public JComboBox sellingBox;
    public JSpinner sellingAmount;
    public JComboBox buyingBox;
    public JSpinner buyingAmount;
    public JSpinner textInterval;
    public JButton startButton;
    public JButton stopButton;
    public JButton loginButton;
    public JButton logoutButton;
    public JTextArea talkerText;
    public JTextArea consoleOutput;
    public JLabel tradesComplete;
    public JLabel timeWasted;
    public JLabel tradesCancelled;
    public JButton northButton;
    public JButton southButton;
    public JButton eastButton;
    public JButton westButton;
    public JLabel cPos;
    public JLabel tPos;
    public JCheckBox randomMovementCheckBox;
    public JComboBox takeComboBox;
    public JButton takeButton;
    public JComboBox putComboBox;
    public JButton putButton;
    public JButton goToVaultButton;
    public JButton goToNexusButton;
    public JLabel locationJLabel;
    public JCheckBox sendTradeRequestsCheckBox;
    public JCheckBox eCheckBox1;
    public JCheckBox eCheckBox2;
    public JCheckBox eCheckBox3;
    public JCheckBox eCheckBox4;
    public JCheckBox eCheckBox5;
    public JCheckBox eCheckBox6;
    public JCheckBox eCheckBox7;
    public JCheckBox eCheckBox8;
    public JFormattedTextField secondAccNameField;
    public JButton teleportToAccButton;
    public JButton eStartButton;
    public JFormattedTextField customPosX;
    public JFormattedTextField customPosY;
    public JButton customPosSet;
    public JCheckBox saveInfoCheckBox;
    public JCheckBox soundCheckBox;
    public JCheckBox dropTest;
    public JCheckBox invSwapTest;
    public JCheckBox vaultSwap;
    public JCheckBox invVaultSwap;
    public JCheckBox dontValidateIfHaveTest;
    private JPanel testPanel;
    public JFormattedTextField versionField;
    public JSpinner timeoutSpinner;
    public JButton invSwapButton;
    public BotClient parentBot;

    public ButtonListener bListener;

    public List<String> serverNames;
    public List<String> serverDNSs;

    public long startTime;
    public String locationString = "Logged Out";
    public Timer timer;

    public Location previousPos = new Location(0,0);
    public boolean continueTrade = false;

    Sound sound = new Sound();

    Properties prop = new Properties();
    File propFile;
    public boolean selectItemsAtTheStart;

	/**
	 * Bot GUI contains all the visual parameters of autoTrader
	 * Contains Login/Trade/Move/Vault/Exchange panels with various functions
	 * To change functionality, edit the BotClient class
	 */
    public BotGui(BotClient parentBot){

        panel1.setPreferredSize(new Dimension(518,410));
        talkerText.setDocument(new LimitDocument(200));
        this.parentBot = parentBot;

        //testPanel.setVisible(false);
        //northButton.setVisible(false);
        tabbedPane1.removeTabAt(tabbedPane1.getTabCount()-1);

        buyingBox.setPrototypeDisplayValue("text testing hereeeee");
        sellingBox.setPrototypeDisplayValue("text testing hereeeee");
        takeComboBox.setPrototypeDisplayValue("text testing hereeeee");
        putComboBox.setPrototypeDisplayValue("text testing hereeeee");

        log("Starting gui...");

        ipSwitch tool = new ipSwitch();
        serverNames = tool.getServerNames();
        serverDNSs = tool.getServerDNSs();
        serverBox.removeAllItems();
        for(int i = 0; i < serverNames.size(); i++){
            serverBox.addItem(serverNames.get(i));
        }
        Random serverGen = new Random();
        serverBox.setSelectedIndex(serverGen.nextInt(20));

        try {
            saveInfoCheckBox.setSelected(true);
            soundCheckBox.setSelected(true);
            propFile = new File("config.properties");
            propFile.createNewFile();
            prop.load(new FileInputStream(propFile));
            if(prop.getProperty("Login")!=null)loginField.setText(prop.getProperty("Login"));
            if(prop.getProperty("Pass")!=null)passwordField.setText(prop.getProperty("Pass"));
            if(prop.getProperty("Server")!=null)serverBox.setSelectedIndex(Integer.parseInt(prop.getProperty("Server")));
            if(prop.getProperty("TalkerText")!=null)talkerText.setText(prop.getProperty("TalkerText"));
            if(prop.getProperty("SecondName")!=null)secondAccNameField.setText(prop.getProperty("SecondName"));
            if(prop.getProperty("Sound")!=null)if(prop.getProperty("Sound").equals("true"))soundCheckBox.setSelected(true);else soundCheckBox.setSelected(false);
        } catch (IOException e) {
            log("Problems with config file input/output: " + e.getCause());
        }

        SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1, 8, 1);
        SpinnerNumberModel model2 = new SpinnerNumberModel(1, 1, 8, 1);
        SpinnerNumberModel model3 = new SpinnerNumberModel(10, 10, 20, 1);
        SpinnerNumberModel model4 = new SpinnerNumberModel(20, 15, 40, 1);
        buyingAmount.setModel(model1);
        sellingAmount.setModel(model2);
        textInterval.setModel(model3);
        timeoutSpinner.setModel(model4);


        logoutButton.setEnabled(false);
        stopButton.setEnabled(false);
        goToNexusButton.setEnabled(false);
        takeButton.setEnabled(false);
        putButton.setEnabled(false);
        teleportToAccButton.setEnabled(false);
        eStartButton.setEnabled(false);
        updateCaret();

        bListener = new ButtonListener(this,parentBot);

        loginButton.addActionListener(bListener);
        logoutButton.addActionListener(bListener);
        startButton.addActionListener(bListener);
        stopButton.addActionListener(bListener);
        northButton.addActionListener(bListener);
        southButton.addActionListener(bListener);
        westButton.addActionListener(bListener);
        eastButton.addActionListener(bListener);
        goToNexusButton.addActionListener(bListener);
        goToVaultButton.addActionListener(bListener);
        takeButton.addActionListener(bListener);
        putButton.addActionListener(bListener);
        teleportToAccButton.addActionListener(bListener);
        eStartButton.addActionListener(bListener);
        customPosSet.addActionListener(bListener);
        eCheckBox1.addActionListener(bListener);
        eCheckBox2.addActionListener(bListener);
        eCheckBox3.addActionListener(bListener);
        eCheckBox4.addActionListener(bListener);
        eCheckBox5.addActionListener(bListener);
        eCheckBox6.addActionListener(bListener);
        eCheckBox7.addActionListener(bListener);
        eCheckBox8.addActionListener(bListener);

        TextAreaOutputStream taos = new TextAreaOutputStream( consoleOutput, 800 );
        PrintStream ps = new PrintStream( taos );
        System.setOut( ps );
        System.setErr( ps );
    }
    private static void checkIfRunning() {
        try {
            //Bind to localhost adapter with a zero connection queue
            ServerSocket socket1 = new ServerSocket(9999,0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
            //socket1.accept();
        }
        catch (Exception e) {
            try {
                //Bind to localhost adapter with a zero connection queue
                ServerSocket socket2 = new ServerSocket(9998,0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
                //socket2.accept();
            }
            catch (Exception ee) {

                System.exit(1);
            }
        }
    }

    public void updateCaret(){
        DefaultCaret caret = (DefaultCaret)consoleOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void clickLogin(int num){
        bListener.nextGameId = num;
        loginButton.setEnabled(true);
        loginButton.doClick();
    }

    public void resetTimer(){
        TimeListener timeListener = new TimeListener(this);
        if(timer!=null)if(timer.isRunning())timer.stop();
        timer = new Timer(500, timeListener);
        timer.setInitialDelay(0);
        timer.start();
    }

    public static void log(String str){
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        System.out.println(timeFormat.format(now)+": "+str);
    }

    public static void main(String[] args){

        checkIfRunning();
        log("yay");
        JFrame frame = new JFrame("AutoTrader V1.05");
        frame.setResizable(false);
        BotClient botClient = new BotClient();
        botClient.setDaemon(true);
        BotGui newBotGui = new BotGui(botClient);
        newBotGui.startTime = System.currentTimeMillis();
        frame.setContentPane(newBotGui.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

}


class TimeListener implements ActionListener
{
    private BotGui parentGui;
    private itemSwitch itemSwitchy = new itemSwitch();
    private boolean onlyOncePlayer=false;
    private boolean onlyOnceTake=false;
    private boolean onlyOnceExchange=false;

    final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public TimeListener(BotGui parentGui){
        this.parentGui = parentGui;
    }
    public void actionPerformed(ActionEvent e)
    {
        long diff = System.currentTimeMillis()-parentGui.startTime-7200000;
        String time = timeFormat.format(diff);
        parentGui.timeWasted.setText(time);
        parentGui.tradesComplete.setText(String.valueOf(parentGui.parentBot.tradesDone));
        parentGui.tradesCancelled.setText(String.valueOf(parentGui.parentBot.tradesFailed));
        if(parentGui.parentBot.currentPos!=null)parentGui.cPos.setText(parentGui.parentBot.currentPos.toString());
        if(parentGui.parentBot.targetPos!=null)parentGui.tPos.setText(parentGui.parentBot.targetPos.toString());
        parentGui.locationJLabel.setText(parentGui.locationString);


        if(parentGui.parentBot.inventoryItems!=null && onlyOncePlayer==false){
            parentGui.log("Initializing inventory items: " + Arrays.toString(parentGui.parentBot.inventoryItems));
            parentGui.sellingBox.removeAllItems();
            parentGui.putComboBox.removeAllItems();
            onlyOncePlayer = true;
            for(int i = 0; i < parentGui.parentBot.inventoryItems.length; i++){
                String itemName = itemSwitchy.idToName(parentGui.parentBot.inventoryItems[i]);
                String tot = ""+parentGui.parentBot.inventoryItems[i]+": "+itemName;
                parentGui.sellingBox.addItem(tot);
                parentGui.putComboBox.addItem(tot);

                if(i==0)parentGui.eCheckBox1.setText(itemName);else
                if(i==1)parentGui.eCheckBox2.setText(itemName);else
                if(i==2)parentGui.eCheckBox3.setText(itemName);else
                if(i==3)parentGui.eCheckBox4.setText(itemName);else
                if(i==4)parentGui.eCheckBox5.setText(itemName);else
                if(i==5)parentGui.eCheckBox6.setText(itemName);else
                if(i==6)parentGui.eCheckBox7.setText(itemName);else
                if(i==7)parentGui.eCheckBox8.setText(itemName);
            }

            if(parentGui.parentBot.botStarted){
                for(int i = 0; i < parentGui.parentBot.inventoryItems.length; i++){
                    if(parentGui.parentBot.inventoryItems[i]==parentGui.parentBot.itemIDSell){
                        parentGui.sellingBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

           // if(parentGui.prop.getProperty("Server")!=null)serverBox.setSelectedIndex(Integer.parseInt(prop.getProperty("Server")));
            if(parentGui.selectItemsAtTheStart == false){
                parentGui.selectItemsAtTheStart = true;
                if(parentGui.prop.getProperty("Selling")!=null)parentGui.sellingBox.setSelectedIndex(Integer.parseInt(parentGui.prop.getProperty("Selling")));
                if(parentGui.prop.getProperty("Buying")!=null)parentGui.buyingBox.setSelectedIndex(Integer.parseInt(parentGui.prop.getProperty("Buying")));
            }
        }
        if(parentGui.parentBot.chestItems!=null && onlyOnceTake==false){
            parentGui.takeComboBox.removeAllItems();
            onlyOnceTake = true;
            for(int i = 0; i < parentGui.parentBot.chestItems.length; i++){
                String itemName = itemSwitchy.idToName(parentGui.parentBot.chestItems[i]);
                String tot = ""+parentGui.parentBot.chestItems[i]+": "+itemName;
                parentGui.takeComboBox.addItem(tot);
            }
        }

        if(System.currentTimeMillis() - parentGui.parentBot.lastTeleportTimer > 10000 && parentGui.secondAccNameField.getText().length()>0){
            parentGui.teleportToAccButton.setEnabled(true);
        }

        if(parentGui.secondAccNameField.getText().length()>0 && onlyOnceExchange==false){
            onlyOnceExchange=true;
            parentGui.teleportToAccButton.setEnabled(true);
            parentGui.eStartButton.setEnabled(true);
        }
    }
};


class ButtonListener implements ActionListener {
    private BotClient parentBot = null;
    private BotGui parentGui = null;
    public int nextGameId = 0;

    public ButtonListener(BotGui parentGui, BotClient parentBot){
        this.parentGui = parentGui;
        this.parentBot = parentBot;
    }

    public void actionPerformed(ActionEvent e){

        AbstractButton sourceButton = (AbstractButton)e.getSource();
        String name = sourceButton.getText();
        //String fieldName = sourceButton.get
        String toolTipTextName = " "+sourceButton.getToolTipText();

        if(name.equals("Exit"))System.exit(1);else
        if(name.equals("Login")){
            parentGui.log("Doing login to gameId:"+nextGameId);
            parentBot = new BotClient(parentGui);
            parentGui.parentBot = this.parentBot;
            int serverSelected = parentGui.serverBox.getSelectedIndex();
            parentBot.proxyAddress = parentGui.serverDNSs.get(serverSelected);
            parentBot.login = parentGui.loginField.getText();
            parentBot.pass = parentGui.passwordField.getText();
            parentBot.charID = new xmlSwitch(parentBot.login,parentBot.pass).getCharId();
            parentBot.nextGameId = this.nextGameId;
            parentGui.loginButton.setEnabled(false);
            parentGui.logoutButton.setEnabled(true);

            itemSwitch itemSwitchy = new itemSwitch();
            parentBot.itemNameList = itemSwitchy.getItemStrings();
            parentBot.itemIdList = itemSwitchy.getItemIds();
            parentBot.itemNameArray = parentBot.itemNameList.toArray(new String[parentBot.itemNameList.size()]);
            parentBot.itemIdArray = parentBot.itemIdList.toArray(new Integer[parentBot.itemIdList.size()]);

            parentGui.buyingBox.removeAllItems();
            for(int i = 0; i < parentGui.parentBot.itemNameList.size(); i++){
                String tot = ""+parentGui.parentBot.itemIdArray[i]+": "+parentGui.parentBot.itemNameArray[i];
                parentGui.buyingBox.addItem(tot);
            }
            if(parentGui.saveInfoCheckBox.isSelected())
            try {
                parentGui.prop.setProperty("Login",parentGui.loginField.getText());
                parentGui.prop.setProperty("Pass",parentGui.passwordField.getText());
                parentGui.prop.setProperty("Server",Integer.toString(parentGui.serverBox.getSelectedIndex()));
                if(parentGui.soundCheckBox.isSelected())parentGui.prop.setProperty("Sound","true");else parentGui.prop.setProperty("Sound","false");
                parentGui.prop.store(new FileOutputStream(parentGui.propFile), null);
            } catch (IOException e1) {
                parentGui.log("Problem while storing info in config file");
            }



            parentGui.resetTimer();
            parentBot.start();

            if(parentGui.continueTrade == true && parentGui.previousPos.x>0){
                try {
                    parentGui.continueTrade=false;
                    wait(3000);
                    parentBot.targetPos = parentGui.previousPos;
                    parentGui.startButton.setEnabled(true);
                    parentGui.startButton.doClick();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }else
        if(name.equals("Logout")){
            parentBot.stop();
            parentBot.logout();
            parentGui.loginButton.setEnabled(true);
            parentGui.logoutButton.setEnabled(false);
        }else
        if(name.equals("Start") && parentBot.inventoryItems[parentGui.sellingBox.getSelectedIndex()]>0){
            parentBot.itemAmountBuy = (Integer)parentGui.buyingAmount.getValue();
            parentBot.itemAmountSell = (Integer)parentGui.sellingAmount.getValue();
            int buyingIndex = parentGui.buyingBox.getSelectedIndex();
            int sellingIndex = parentGui.sellingBox.getSelectedIndex();
            parentBot.itemIDBuy = parentBot.itemIdList.get(buyingIndex);
            parentBot.itemIDSell = parentBot.inventoryItems[sellingIndex];
            parentBot.textToSend = parentGui.talkerText.getText();
            parentBot.textInterval = 1000*(Integer)parentGui.textInterval.getValue();
            parentBot.timeOut = 1000*(Integer)parentGui.timeoutSpinner.getValue();
            parentBot.botStarted = true;
            parentBot.checkValidity();
            parentBot.randomMovement = parentGui.randomMovementCheckBox.isSelected();
            parentGui.startButton.setEnabled(false);
            parentGui.stopButton.setEnabled(true);
            parentGui.eStartButton.setEnabled(false);

            if(parentGui.saveInfoCheckBox.isSelected())
            try {
                parentGui.prop.setProperty("Selling",Integer.toString(parentGui.sellingBox.getSelectedIndex()));
                parentGui.prop.setProperty("Buying",Integer.toString(parentGui.buyingBox.getSelectedIndex()));
                parentGui.prop.setProperty("TalkerText",parentGui.talkerText.getText());
                parentGui.prop.store(new FileOutputStream(parentGui.propFile), null);
            } catch (IOException e1) {
                parentGui.log("Problem while storing info in config file");
            }
        }else
        if(name.equals("Stop")){
            parentBot.botStarted = false;
            parentGui.startButton.setEnabled(true);
            parentGui.stopButton.setEnabled(false);
            parentGui.eStartButton.setEnabled(true);
        }else
        if(name.equals("North")){
            parentGui.log("Moving north");
            parentBot.targetPos.y-=2;
        }else
        if(name.equals("South")){
            parentGui.log("Moving south");
            parentBot.targetPos.y+=2;
        }else
        if(name.equals("West")){
            parentGui.log("Moving west");
            parentBot.targetPos.x-=2;
        }else
        if(name.equals("East")){
            parentGui.log("Moving east");
            parentBot.targetPos.x+=2;
        }else
        if(name.equals("Go to Nexus")){
            parentBot.reconnecting = true;
            parentBot.usePortal(47);
            parentGui.goToVaultButton.setEnabled(true);
            parentGui.goToNexusButton.setEnabled(false);
            parentGui.takeButton.setEnabled(false);
            parentGui.putButton.setEnabled(false);
        }else
        if(name.equals("Go to Vault")){
            parentBot.reconnecting = true;
            parentBot.usePortal(503);
            parentGui.goToVaultButton.setEnabled(false);
            parentGui.goToNexusButton.setEnabled(true);
            parentGui.takeButton.setEnabled(true);
            parentGui.putButton.setEnabled(true);
        }else
        if(name.equals("Take")){
            //parentBot.usePortal(502);
            parentBot.takeItem(parentGui.takeComboBox.getSelectedIndex());
        }else
        if(name.equals("Put")){
            //parentBot.usePortal(502);
            parentBot.putItem(parentGui.putComboBox.getSelectedIndex());
        }else
        if(name.equals("Teleport to account")){

            parentGui.teleportToAccButton.setEnabled(false);
            parentBot.teleportToName(parentGui.secondAccNameField.getText());

            //send teleport packet to specified name
        }else
        if(name.equals("Send trade request")){
            parentGui.eStartButton.setEnabled(false);
            parentGui.startButton.setEnabled(false);
            parentBot.startTradeExternal(parentGui.secondAccNameField.getText());
            if(parentGui.saveInfoCheckBox.isSelected())
            try {
                parentGui.prop.setProperty("SecondName",parentGui.secondAccNameField.getText());
                parentGui.prop.store(new FileOutputStream(parentGui.propFile), null);
            } catch (IOException e1) {
                parentGui.log("Problem while storing info in config file");
            }
        }else
        if(name.equals("Set")){
            String strPosX = parentGui.customPosX.getText();
            String strPosY = parentGui.customPosY.getText();
            parentBot.targetPos.x = Integer.parseInt(strPosX);
            parentBot.targetPos.y = Integer.parseInt(strPosY);
            parentGui.log("Setting location: " + Integer.parseInt(strPosX) + "..." + Integer.parseInt(strPosY));
        }else
        if(toolTipTextName.contains("checkbox") && parentBot.exchangeLock){

            parentBot.deselectAll();
            parentBot.selectMyItems();
            parentBot.acceptTrade();
        }

    }
}