var server = io.connect('http://localhost:8080');
var chatters = document.getElementById('chatters');
var chat_input = document.getElementById('chat-input');
var chat_console = document.getElementById('chat-console');
var my_name;

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
  chatters.appendChild(new_chatter);

  // New chatter alert
  if(name != my_name){
    newAlert(	name+' has connected to the chat server','connected');
  }
}

function insertMessage(message) {
  var new_message = document.createElement('span');
  new_message.innerHTML = message + '<br/>';
  chat_console.appendChild(new_message);
}

function newAlert(msg, _class){
  var new_alert = document.createElement('span');
  new_alert.setAttribute('class', _class);
  new_alert.innerHTML = msg+'<br/>';
  chat_console.appendChild(new_alert);
}

document.getElementById('chat-form').onsubmit = function(e) {
  e.preventDefault();
  server.emit('messages', chat_input.value);
  insertMessage(my_name + ' : ' + chat_input.value);
  chat_input.value = null;
};
server.on('messages', function(data) {
  insertMessage(data);
});
server.on('connect', function(data) {
  my_name = prompt('What is your name?');
  newAlert(	'You has connected to the chat server','connected');
  server.emit('join', my_name);
});
server.on('new chatter', insertChatter);
server.on('remove chatter', removeChatter);
