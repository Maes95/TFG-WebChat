/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michel
 */
public class NewMain {
    
    private static String path = System.getProperty("user.dir").substring(0,System.getProperty("user.dir").length() - 11);
    
    public static void runServerSH(String folderName){
        Process process = null;
        try {
            process = new ProcessBuilder("./run.sh").directory(new File(path+folderName)).start();
            process.waitFor();
            System.out.println("El proceso murió");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        NewMain.runServerSH("WebChatVertxWebSockets");
//        Process process = null;
//        String local = System.getProperty("user.dir");
//        String path = local.substring(0,local.length() - 11)+"WebChatVertxWebSockets";
//        try {
//            process = new ProcessBuilder("./run.sh").directory(new File(path)).start();
//            process.waitFor();
//            System.out.println(process.exitValue());
//        } catch (IOException ex) {
//            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        while(process.isAlive()){}
//        System.out.println("El proceso murió");
    }
    
    //    public void runNodeServer(){
//        new Thread(() -> {
////            Process _process = null;
//            String local = System.getProperty("user.dir");
//            String root = local.substring(0,local.length() - 11);
//            try {
//                process = new ProcessBuilder("npm", "start").directory(new File(root+"WebChatNodeWebsockets")).start();
//                process.waitFor();
//                System.out.println("El proceso murió");
//            } catch (IOException | InterruptedException ex) {
//                Logger.getLogger(ChatTest.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }).start();
//    }
    
    
    
}
