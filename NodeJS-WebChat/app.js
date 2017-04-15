const express = require('express');
const app = express();
const SocketServer = require('ws').Server;
const http = require('http');
const server = http.createServer(app).listen(9000);
const path = require('path')

// To use client files

app.use(express.static(path.join(__dirname, '/client')));

DUPLICATE_MSG = "{\"type\":\"system\",\"message\":\"User already exist\"}";

const wss = new SocketServer({ server });

// Set where key is the user_name and the value is the chat_name
var users = new Set();

wss.on('connection', (ws) => {

	ws.on('message', (data) => {
		var message = JSON.parse(data);
		if(message['message']){
			// Normal message (broadcast it)
			wss.clients
      .filter(  (client) => client.chat == ws.chat )
      .forEach( (client) => client.send(data) );
		}else{
			// First message (first connection)
      if(users.has(message['name'])){
        ws.send(DUPLICATE_MSG);
        ws.close();
      }else{
        users.add(message['name']);
        ws['chat'] = message['chat'];
        ws['name'] = message['name'];
      }
		}
	});

  ws.on('close', () => {
    users.delete(ws['name']);
  });

});
