var fs = require('fs');
var file = "var results = [];";

function readFiles(dirname, onFileContent, onError, onComplete) {
  var cont = 0;
  fs.readdir(dirname, function(err, filenames) {
    if (err) {
      onError(err);
      return;
    }

    filenames.forEach(function(filename) {
      cont++;
      fs.readFile(dirname + filename, 'utf-8', function(err, content) {
        if (err) {
          onError(err);
          return;
        }
        onFileContent(filename, content);
        if(cont == filenames.length){
            onComplete();
        }
      });
    });
  });
}

readFiles('results/', function onFileContent(filename, content) {
  file += "results.push("+content+");";
}, function onError(err) {
  throw err;
}, function onComplete(){
    fs.writeFile('js/results.js', file, function(){});
});
