/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app.WebChatApp;

import org.json.JSONObject;

/**
 *
 * @author michel
 */
public interface WebChatAppFactoryMethod {
    
    public WebChatApp createWebChatApp(JSONObject config);
    
}
