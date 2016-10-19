var express = require('express'),
    path = require('path'),
    http = require('http');

var router = express.Router();
var app = express();

// To serve public folder
app.set('view engine', 'jade');
app.use(express.static(path.join(__dirname, 'public')));
app.use('/node_modules',  express.static(__dirname + '/node_modules'));

// Render Angular2 index template
router.get('/*',function(req, res){

	res.render('index');

});


// initiate
app.use('/',  router);

// 404 and redirect
app.use(function(req, res, next) {
  res.redirect("/")
});

var server = http.createServer(app);
var io = require('socket.io').listen(server);
server.listen(8080);


// SOCKET.IO

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
			client.emit( 'messages', {name: message.name, text: message.text} );
		});

	});


	client.on( 'messages', function( message ) {

    console.log(message);

		saveMessage( client.name, message );
		client.broadcast.emit( 'messages', {name: client.name, text: message});

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
