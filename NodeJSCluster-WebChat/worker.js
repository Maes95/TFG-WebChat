const express = require('express');
const SocketServer = require('ws').Server;
const http = require('http');
const path = require('path')
const app = express();
const server = http.createServer(app).listen(9000);

// To use client files
app.use(express.static(path.join(__dirname, '/client')));

const wss = new SocketServer({ server });

wss.on('connection', (ws) => {

  console.log('Client connected');

  ws.on('message', (data) => {
    var message = JSON.parse(data);
    if(message['message']){
      // Normal message (send to master)
      process.send({ chatName: ws.chat, message: data });
    }else{
      // First message (first connection)
      ws['chat'] = message['chat'];
      ws['name'] = message['name'];
    }
  });

  ws.on('close', () => {
    console.log('Client -' + ws['name'] + '- disconnected')
  });

});

process.on('message', (msg) => {
  wss.clients
  .filter( (client) => client.chat == msg.chatName )
  .forEach( (client) => {
    client.send(msg.message);
  });
});
