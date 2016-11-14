var express = require('express');
var app = express();
var http = require('http');
var server = http.createServer(app);
var io = require('socket.io').listen(server);
var path = require('path')

server.listen(8080);

// To use client files

app.use(express.static(path.join(__dirname, '/client')));

var chat_rooms = {};

io.sockets.on( 'connection', function ( client ) {

	client.on( 'join', function( join_data ) {

		var room = chat_rooms[join_data.chat];

		if(room){
			// Chat room exist
			if(room.chatters.indexOf(join_data.name) != -1){
				// User already exist
				client.emit( 'already exist' );
				return;
			}else{
				// User doesn't exist
				room.chatters.push(join_data.name);
			}
		}else{
			// Chat room doesn't exist
			chat_rooms[join_data.chat] = { chatters: [join_data.name], messages: [] };
			room = chat_rooms[join_data.chat];
		}
		client.emit( 'connected' );

		client.name = join_data.name;
		client.chat = join_data.chat;

		console.log("Se ha conectado "+client.name+ " a la sala "+client.chat)

		client.broadcast.emit( client.chat + ' new chatter', client.name );

		room.chatters.forEach( function( name ) {
			client.emit( client.chat + ' new chatter', name );
		});

		room.messages.forEach( function( message ) {
			client.emit( client.chat + ' new message', message );
		});

	});


	client.on( 'messages', function( message ) {

		saveMessage( client.chat, message );
		client.broadcast.emit( client.chat+' new message', message );

	});


	client.on( 'disconnect', function() {

		console.log("Se ha desconectado: "+client.name);

		if(client.chat){
			client.broadcast.emit( client.chat + ' remove chatter', client.name );
			var index = chat_rooms[client.chat].chatters.indexOf(client.name);
			if (index > -1) {
				chat_rooms[client.chat].chatters.splice(index, 1);
			}
		}

	});

});

function saveMessage( chat_name , msg ) {
  chat_rooms[chat_name].messages.push(msg);
  if(chat_rooms[chat_name].messages.length > 10){
    chat_rooms[chat_name].messages.shift();
  }
}
