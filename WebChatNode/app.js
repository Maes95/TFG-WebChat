var express = require('express');
var app = express();
var http = require('http');
var server = http.createServer(app);
var io = require('socket.io').listen(server);
var path = require('path')

server.listen(8080);

// To use client files

app.use(express.static(path.join(__dirname, '/client')));

var messages = [];
var chatters = [];

io.sockets.on( 'connection', function ( client ) {

	client.on( 'join', function( name ) {

		client.name = name;

		console.log("Se ha conectado: "+client.name)

		client.broadcast.emit( 'new chatter', name );
    chatters.push(name);

		chatters.forEach( function( name ) {
			client.emit( 'new chatter', name );
		});

		messages.forEach( function( message ) {
			client.emit( 'messages',message.name + ' : ' + message.text );
		});

	});


	client.on( 'messages', function( message ) {

		saveMessage( client.name, message );
		client.broadcast.emit( 'messages', client.name + ' : ' + message );

	});


	client.on( 'disconnect', function( name ) {

		console.log("Se ha desconectado: "+client.name)

		client.broadcast.emit( 'remove chatter', client.name );
		var index = chatters.indexOf(client.name);
		if (index > -1) {
			chatters.splice(index, 1);
		}

	});

});

function saveMessage( name, text ) {
  messages.push({name: name, text: text});
  if(messages.length > 10){
    messages.shift();
  }
}
