// $('#chatters');
var my_name;
var my_chat;

// GET DATA FROM CLIENT
my_chat = prompt("Chat name", "chat");
if(my_chat == null || my_chat === ''){
  my_chat = "chat";
}

my_name = prompt("User name", "user");
if(my_name == null || my_name === ''){
  my_name = "user";
}

// OPEN CONECTION
var eb = new EventBus("/eventbus/");
eb.onopen = function () {

  var message = {
    chat : my_chat,
    user : my_name
  };

  eb.publish("connect", message);

  eb.registerHandler("message", insertMessage);
};


function sendMessage(event) {
  if (event.keyCode == 13 || event.which == 13) {
    var message = $('#chat-input').val();
    if (message.length > 0) {
      eb.publish("new.message", message);
      $('#chat-input').val("");
    }
  }
}

function insertMessage(error, message) {
  var new_message = document.createElement('span');
  new_message.innerHTML = message.body + '<br/>';
  $('#chat-console').append(new_message);
}
