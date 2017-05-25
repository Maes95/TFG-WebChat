angular.module("client").service('StaticData', function() {

    this.generate = function(callback){
      for( var i in results ){
        for(var j in results[i]){
          callback(results[i][j]);
        }
      }
    }

});
