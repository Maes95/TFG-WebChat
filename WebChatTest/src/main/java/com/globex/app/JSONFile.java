package com.globex.app;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author michel
 */
public class JSONFile {
    
    public static JSONObject parse(String filePath){
        JSONObject json = new JSONObject();
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
            json =  new JSONObject(result);
        } catch(IOException e) {
            System.err.println("Can't read json file");
            e.printStackTrace();
        }
        return json;
    }
}
