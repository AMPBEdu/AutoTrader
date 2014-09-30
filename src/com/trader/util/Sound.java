package com.trader.util;




import sun.tools.jar.Main;

import javax.sound.sampled.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

public class Sound{

    public URL soundFile1;
    public URL soundFile2;
    public URL soundFile3;
    public URL soundFile4;
    private AudioClip song1;//trade complete  ir   (tradeDone)
    private AudioClip song2;//logout          ir  (failure)
    private AudioClip song3;//button press    ir   (actionListener)
    private AudioClip song4;//login-correct   ir (mapinfo)


    public Sound(){

        new Thread(new Runnable() {
            public void run() {
                try {
                    soundFile1 = new URL("http://www.soundjay.com/button/button-3.wav");
                    soundFile2 = new URL("http://www.soundjay.com/button/beep-6.wav");
                    //soundFile3 = new URL("http://www.soundjay.com/button/beep-23.wav");
                    //soundFile4 = new URL("http://www.soundjay.com/button/button-11.wav");
                    song1 = Applet.newAudioClip(soundFile1);
                    song2 = Applet.newAudioClip(soundFile2);
                    //song3 = Applet.newAudioClip(soundFile3);
                    //song4 = Applet.newAudioClip(soundFile4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public synchronized void playSound(final int num) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if(num==1)song1.play();
                    if(num==2)song2.play();
                    //if(num==3)song3.play();
                    //if(num==4)song4.play();
                } catch (Exception e) {
                    System.err.println(e.getCause()+"..."+e.getMessage());
                }
            }
        }).start();
    }
}