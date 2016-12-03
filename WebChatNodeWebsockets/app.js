const express = require('express');
const app = express();
const SocketServer = require('ws').Server;
const http = require('http');
const server = http.createServer(app).listen(9000);
const path = require('path')

// To use client files

app.use(express.static(path.join(__dirname, '/client')));

const wss = new SocketServer({ server });

wss.on('connection', (ws) => {

  console.log('Client connected');

	ws.on('message', (data) => {
		var message = JSON.parse(data)
		// console.log(data);
		if(message['message']){
			// Mensaje estandar
			wss.clients.forEach((client) => reSend(client, message) );
		}else{
			// Mensaje de inicio
			newUser(message, ws);
		}
	});
  ws.on('close', () => console.log('Client disconnected'));
});

function reSend(client, data){
	data.name = data.user;
	client.send(JSON.stringify(data));
}
