package com.trader.util;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: PC
 * Date: 12.14.12
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
public class Writer {


    public void addClosedList(String s, boolean appendToFile){

        FileOutputStream fop = null;
        File file;

        try {
            file = new File("C:\\Users\\PC\\Desktop\\closedList.txt");
            fop = new FileOutputStream(file,appendToFile);

            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] contentInBytes = s.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void addHosts(String longDNS){
        try{
            File file = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
            FileWriter fstream = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("\n");
            out.write("127.0.0.1 ");
            out.write(longDNS);
            out.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
