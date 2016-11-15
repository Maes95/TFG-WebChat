var server = io.connect('http://localhost:8080');
var chat_chatters = document.getElementById('chatters');
var chat_input = document.getElementById('chat-input');
var chat_console = document.getElementById('chat-console');
var user_name = "user";
var chat_name = "chat";

function removeChatter(name) {
  var current_chatters = document.querySelectorAll('[data-name]');
  for (var i = 0; i < current_chatters.length; i++) {
    if (name === current_chatters[i].getAttribute('data-name')) {
      current_chatters[i].parentNode.removeChild(current_chatters[i]);
      newAlert(	name+' has disconnected to the chat server', 'disconnected');
      break;
    }
  }
}

function insertChatter(name) {
  var new_chatter = document.createElement('li');
  new_chatter.setAttribute('data-name', name);
  new_chatter.setAttribute('class', 'connected');
  new_chatter.innerHTML = name;
  chat_chatters.appendChild(new_chatter);

  // New chatter alert
  if(name != user_name){
    newAlert(	name+' has connected to the chat server','connected');
  }
}

function insertMessage(message) {
  var new_message = document.createElement('div');
  var html = "<div class=\"comment\"><div class=\"content\"> "+
      "<a class=\"author\">"+message.user+"</a> <div class=\"metadata\"> "+
      "<span class=\"date\">"+message.timestamp+"</span>"+
      "</div> <div class=\"text\">"+message.text+"</div></div></div></div>";
  new_message.innerHTML = html;
  chat_console.appendChild(new_message);
}

function newAlert(msg, _class){
  var new_alert = document.createElement('div');
  new_alert.setAttribute('class', _class+ " alert");
  new_alert.innerHTML = msg+'<br/>';
  chat_console.appendChild(new_alert);
}

document.getElementById('chat-form').onsubmit = function(e) {
  e.preventDefault();
  var date = new Date();
  var msg = { user: user_name, text: chat_input.value, timestamp: date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()};
  server.emit('messages', msg);
  insertMessage(msg);
  chat_input.value = null;
};

function tryToConnect(){
  chat_name = prompt('What do you want to connect to?');
  user_name = prompt('What is your name?');
  server.emit('join', { name: user_name, chat: chat_name});
}

server.on('connect', tryToConnect);

server.on('already exist', function(){
  alert("Name already exist");
  location.reload();
})

server.on('connected', function(){
  newAlert(	'You has connected to the chat server','connected');
  server.on(chat_name+' new message', insertMessage );
  server.on(chat_name+' new chatter', insertChatter );
  server.on(chat_name+' remove chatter', removeChatter);
})
