// $('#chatters'); <i class="users icon"></i>
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

  eb.send("connect", my_name, function(err, res){
    if(err){
      console.error(err);
    }else{
      if(res.body){
        // Register to listen for messages coming from my chatroom
        eb.registerHandler(my_chat, function(err, message){
          err ? console.error(err) : insertMessage(message.body);
        });
      }else{
        alert("User Name already exist");
        eb.close();
      }
    }
  });

};


function sendMessage(event) {
  if (event.keyCode == 13 || event.which == 13) {
    var message = {
      user: my_name,
      text: $('#chat-input').val(),
      chat: my_chat
    };
    if (message.text.length > 0) {
      eb.publish("new.message", message);
      $('#chat-input').val("");
    }
  }
}

function insertMessage(message) {
  var new_message = document.createElement('span');
  var text = message.user + " ("+message.timestamp+"): " + message.text;
  new_message.innerHTML = text + '<br/>';
  $('#chat-console').append(new_message);
}
