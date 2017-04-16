const express = require('express');
const SocketServer = require('ws').Server;
const http = require('http');
const path = require('path')
const app = express();
const server = http.createServer(app).listen(9000);

// To use client files
app.use(express.static(path.join(__dirname, '/client')));

DUPLICATE_MSG = "{\"type\":\"system\",\"message\":\"User already exist\"}";

const wss = new SocketServer({ server });

var worker_id;

// Dictionary where key is the user_name and the value is the WebSocket

var users = new Map();

wss.on('connection', (ws) => {

  ws.on('message', (data) => {
    var message = JSON.parse(data);
    if(message['message']){
      // Normal message (send to master)
      process.send({ type: "message", chat: ws.chat, message: data });
    }else{
      // First message (first connection)
      users.set(ws._socket._handle.fd, ws);
      process.send({
        type: "new.user",
        name: message.name,
        chat: message.chat,
        worker_id: worker_id,
        ws_id: ws._socket._handle.fd
      });
    }
  });

  ws.on('close', () => {
    process.send({ type: "delete.user", name: ws['name']});
  });

});

process.on('message', (data) => {
  switch (data.type) {
    case "init":
      // Worker id
      worker_id = data.id;
      break;
    case "message":
      // Broadcast message
      wss.clients
      .filter( (client) => client.chat == data.chat )
      .forEach( (client) => client.send(data.message) );
      break;
    case "user.answer":
      // Master said us if user exists or not
      var ws = users.get(data['ws_id']);
      if(data.exist){
          ws.send(DUPLICATE_MSG);
          ws.close();
      }else{
          ws['chat'] = data['chat'];
          ws['name'] = data['name'];
      }
      break;
  }

});
