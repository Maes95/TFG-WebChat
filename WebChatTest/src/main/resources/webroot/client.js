// OPEN CONECTION
var eb = new EventBus("/eventbus/");
eb.onopen = function () {

  eb.registerHandler("new.result", function(err, message){
    console.log(JSON.stringify(message.body));
    // console.log(message.body["numUsers"]+"-"+message.body["chatSize"]+" Avg: "+message.body["avgTime"]+" Times: "+message.body["times"]);
  });

};
