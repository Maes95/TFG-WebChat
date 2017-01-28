angular.module("client").service('FakeResults', function() {

    // ONLY FOR TESTING WITH NO BACKEND

    var fake_results = [
      {"times":[2507,2505,2505,2505,2505,2505,2505,2505,2505,2505],"numUsers":10,"avgTime":2505,"chatSize":"1", app: "Node"},
      {"times":[2506,2507,2506,2507,2507,2507,2507,2507,2507,2516],"numUsers":20,"avgTime":2507,"chatSize":"1", app: "Node"},
      {"times":[4041,3966,4011,3974,3921,4169,4708,4127,4333,4111],"numUsers":30,"avgTime":4136,"chatSize":"1", app: "Node"},
      {"times":[6853,7779,7278,7701,7542,6874,6816,6807,7132,7611],"numUsers":40,"avgTime":7239,"chatSize":"1", app: "Node"},
      {"times":[11273,11095,10526,11440,10609,10533,10464,10491,10772,10446],"numUsers":50,"avgTime":10764,"chatSize":"1", app: "Node"},
      {"times":[15634,15158,15182,15079,15665,15754,15328,15193,15374,15414],"numUsers":60,"avgTime":15378,"chatSize":"1", app: "Node"},
      {"times":[7311,7147,7356,7247,7364,7125,7150,7358,7409,7622],"numUsers":20,"avgTime":7308,"chatSize":"2", app: "Node"},
      {"times":[10997,10846,10799,10820,11104,10801,11176,11126,10950,11330],"numUsers":25,"avgTime":10994,"chatSize":"2", app: "Node"},
      {"times":[15335,15551,16047,15507,15464,15709,15463,15804,15383,15159],"numUsers":30,"avgTime":15542,"chatSize":"2", app: "Node"},
      {"times":[21569,21033,20909,20607,20926,20984,21762,20974,21189,20676],"numUsers":35,"avgTime":21062,"chatSize":"2", app: "Node"},
      {"times":[10041,10051,10037,10033,10042,10035,10033,10029,10032,10031],"numUsers":10,"avgTime":10036,"chatSize":"4", app: "Node"},
      {"times":[11554,11723,11177,11255,11316,11602,11799,11370,11229,11524],"numUsers":12,"avgTime":11454,"chatSize":"4", app: "Node"},
      {"times":[17137,17337,17480,17616,17205,17477,18075,16753,16956,17074],"numUsers":15,"avgTime":17311,"chatSize":"4", app: "Node"},
      {"times":[22301,21806,21095,20877,21624,21824,21707,21922,21624,20473],"numUsers":17,"avgTime":21525,"chatSize":"4", app: "Node"},

      {"times":[2507,2505,2505,2505,2505,2505,2505,2505,2505,2505],"numUsers":10,"avgTime":1505,"chatSize":"1", app: "Akka"},
      {"times":[2506,2507,2506,2507,2507,2507,2507,2507,2507,2516],"numUsers":20,"avgTime":1507,"chatSize":"1", app: "Akka"},
      {"times":[4041,3966,4011,3974,3921,4169,4708,4127,4333,4111],"numUsers":30,"avgTime":3136,"chatSize":"1", app: "Akka"},
      {"times":[6853,7779,7278,7701,7542,6874,6816,6807,7132,7611],"numUsers":40,"avgTime":4239,"chatSize":"1", app: "Akka"},
      {"times":[11273,11095,10526,11440,10609,10533,10464,10491,10772,10446],"numUsers":50,"avgTime":6764,"chatSize":"1", app: "Akka"},
      {"times":[15634,15158,15182,15079,15665,15754,15328,15193,15374,15414],"numUsers":60,"avgTime":9378,"chatSize":"1", app: "Akka"},
      {"times":[7311,7147,7356,7247,7364,7125,7150,7358,7409,7622],"numUsers":20,"avgTime":2308,"chatSize":"2", app: "Akka"},
      {"times":[10997,10846,10799,10820,11104,10801,11176,11126,10950,11330],"numUsers":25,"avgTime":4994,"chatSize":"2", app: "Akka"},
      {"times":[15335,15551,16047,15507,15464,15709,15463,15804,15383,15159],"numUsers":30,"avgTime":6542,"chatSize":"2", app: "Akka"},
      {"times":[21569,21033,20909,20607,20926,20984,21762,20974,21189,20676],"numUsers":35,"avgTime":10062,"chatSize":"2", app: "Akka"},
      {"times":[10041,10051,10037,10033,10042,10035,10033,10029,10032,10031],"numUsers":10,"avgTime":5036,"chatSize":"4", app: "Akka"},
      {"times":[11554,11723,11177,11255,11316,11602,11799,11370,11229,11524],"numUsers":12,"avgTime":5454,"chatSize":"4", app: "Akka"},
      {"times":[17137,17337,17480,17616,17205,17477,18075,16753,16956,17074],"numUsers":15,"avgTime":7311,"chatSize":"4", app: "Akka"},
      {"times":[22301,21806,21095,20877,21624,21824,21707,21922,21624,20473],"numUsers":17,"avgTime":11525,"chatSize":"4", app: "Akka"}
    ]

    this.generate = function(callback){
      var i = 0;
      var interval = setInterval(function(){
        callback(fake_results[i]);
        i++;
        if(i == fake_results.length){
          clearInterval(interval);
        };
      }, 1000);
    }

});
