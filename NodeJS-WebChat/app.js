const express = require('express');
const app = express();
const SocketServer = require('ws').Server;
const http = require('http');
const server = http.createServer(app).listen(9000);
const path = require('path')

// To use client files

app.use(express.static(path.join(__dirname, '/client')));

const wss = new SocketServer({ server });

// Dictionary where key is the user_name and the value is the chat_name
var users = new Map();

wss.on('connection', (ws) => {

  console.log('Client connected');

	ws.on('message', (data) => {
		var message = JSON.parse(data);
		if(message['message']){
			// Normal message
      message.name = message['name'];
      var stringMessage = JSON.stringify(message)
			wss.clients
      .filter( (client) => client.chat == users.get(message['name']))
      .forEach( (client) => {
        client.send(stringMessage);
      });
		}else{
			// First message (first connection)
      if(users.has(message['name'])){
        ws.send(JSON.stringify({ type: 'system', message: 'User already exist' }));
        ws.close();
      }else{
        users.set(message['name'], message.chat);
        ws.chat = message.chat;
        ws['name'] = message['name'];
      }
		}
	});

  ws.on('close', () => {
    users.delete(ws['name']);
    console.log('Client disconnected')
  });

});
