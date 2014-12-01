/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jambientmovitatingexample;

/**
 *
 * @author Benjamin D. Horne
 * This code is a motivating example of mobile ambient movement as a mobile device
 */

import Jambient.*;
import java.util.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JambientMovitatingExample implements Runnable {
    
    //final static String cloud = "127.0.0.1";
    static Thread th;
    static Boolean inWireless = true; static Boolean iTookaPhoto = false; static Boolean phoneIsOn = true;
    static HashMap<String, Jambient.Ambient> aData = aData = new HashMap<>(); static HashMap<String, Jambient.Process> pData = pData = new HashMap<>();
    
    public JambientMovitatingExample(){
        th = new Thread(this);
    }

    public static void main(String[] args) throws InterruptedException {
        
        // Ambient Set Up
        Ambient CellNetwork = new Ambient("CellNetwork");
        CellNetwork.make(null);
        aData.put("CellNetwork", CellNetwork);
        Ambient HomeWirelessNetwork = new Ambient("HomeWirelessNetwork");
        HomeWirelessNetwork.make(CellNetwork);
        aData.put("HomeWirelessNetwork", HomeWirelessNetwork);
        Ambient WorkWirelessNetwork = new Ambient("WorkWirelessNetwork");
        WorkWirelessNetwork.make(CellNetwork);
        aData.put("WorkWirelessNetwork", WorkWirelessNetwork);
        Ambient GalaxyS4 = new Ambient("GalaxyS4");
        GalaxyS4.make(HomeWirelessNetwork);
        aData.put("GalaxyS4", GalaxyS4);
        Ambient Cloud = new Ambient("cloud");
        Cloud.make(GalaxyS4);
        aData.put("cloud", Cloud);
        
        //Process Set Up
        PhotoCloudBackup photoBackup = new PhotoCloudBackup("photoBackup", GalaxyS4);
        photoBackup.make();
        pData.put("photoBackup", photoBackup);
        SendEmail sendEmail = new SendEmail("sendEmail", GalaxyS4);
        sendEmail.make();
        pData.put("sendEmail", sendEmail);
        WeatherUpdate getWeather = new WeatherUpdate("getWeather", GalaxyS4);
        getWeather.make();
        pData.put("getWeather", getWeather);
        InstallUpdates update = new InstallUpdates("update", GalaxyS4);
        update.make();
        pData.put("update", update);
        
        TomsDay();
        
    }
    
    public static void TomsDay() throws InterruptedException{
        JambientMovitatingExample j = new JambientMovitatingExample();
        j.th.start();
        // Toms Day
        System.out.println("Tom is at Home");
        System.out.println("Tom left Home");
        inWireless = false;
        aData.get("GalaxyS4").out(aData.get("HomeWirelessNetwork"));
        System.out.println("Tom is walking to work...");
        Thread.sleep(5000);
        System.out.println("Tom is at work");
        inWireless = true;
        aData.get("GalaxyS4").in(aData.get("WorkWirelessNetwork"));
        System.out.println("Tom sent an email");
        pData.get("sendEmail").mvin(aData.get("cloud"));
        pData.get("sendEmail").runProcess();
        //aData.get("cloud").migrate_runone(cloud, "sendEmail");
        pData.get("sendEmail").mvout(aData.get("cloud"));
        Thread.sleep(3000);
        pData.get("sendEmail").mvin(aData.get("cloud"));
        pData.get("sendEmail").runProcess();
        //aData.get("cloud").migrate_runone(cloud, "sendEmail");
        pData.get("sendEmail").mvout(aData.get("cloud"));
        aData.get("GalaxyS4").out(aData.get("WorkWirelessNetwork"));
        inWireless = false;
        System.out.println("Tom leaves work and is walking home...");
        System.out.println("Tom took a picture of the sunset!");
        iTookaPhoto = true;
        Thread.sleep(5000);
        aData.get("GalaxyS4").in(aData.get("HomeWirelessNetwork"));
        System.out.println("Tom is at Home");
        inWireless = true;
        Thread.sleep(5000);
        System.out.println("Tom is tired and turning off his phone...");
        phoneIsOn = false;
        
        
    }
    
    @Override
    public void run(){
        while(phoneIsOn){
            try {
                pData.get("getWeather").mvin(aData.get("cloud"));
                pData.get("getWeather").runProcess();
                //aData.get("cloud").migrate_runone(cloud, "getWeather");
                pData.get("getWeather").mvout(aData.get("cloud"));
                if(inWireless){
                    pData.get("update").mvin(aData.get("cloud"));
                    pData.get("update").runProcess();
                    //aData.get("cloud").migrate_runone(cloud, "update");
                    pData.get("update").mvout(aData.get("cloud"));
                    if(iTookaPhoto){
                        pData.get("photoBackup").mvin(aData.get("cloud"));
                        pData.get("photoBackup").runProcess();
                        //aData.get("cloud").migrate_runone(cloud, "photoBackup");
                        pData.get("photoBackup").mvout(aData.get("cloud"));
                    }
                }
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(JambientMovitatingExample.class.getName()).log(Level.SEVERE, null, ex);
            }
        }if(!phoneIsOn){th.interrupt();}
    }
    
}

class PhotoCloudBackup extends Jambient.Process{
    PhotoCloudBackup(String n, Ambient x){
        super(n, x);
    }
    @Override
    public void runProcess() {
        System.out.println("Photos backing up to the cloud...");
    }
    
}

class SendEmail extends Jambient.Process{
    SendEmail(String n, Ambient x){
        super(n, x);
    }
    @Override
    public void runProcess() {
        System.out.println("Sending Email....");
    }
}

class WeatherUpdate extends Jambient.Process{
    WeatherUpdate(String n, Ambient x){
        super(n, x);
    }
    @Override
    public void runProcess() {
        Random rand = new Random();
        int degree = rand.nextInt((100 - 1) + 1);
        System.out.printf("YOUR WEATHER UPDATE: It is %d degrees\n", degree);
    }
    
}

class InstallUpdates extends Jambient.Process{
    InstallUpdates(String n, Ambient x){
        super(n, x);
    }
    @Override
    public void runProcess() {
        System.out.println("..............Installing Updates...............");
    }
    
}
