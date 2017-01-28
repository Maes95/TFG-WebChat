const cluster = require('cluster');
const numCPUs = require('os').cpus().length;

cluster.setupMaster({ exec: 'worker.js' });

for (var i = 0; i < numCPUs; i++) {
  var worker = cluster.fork();
  worker.send({type: 'init', id: worker.id });
}

var users = new Set();

for (var id in cluster.workers) {
  cluster.workers[id].on('message', (message) => {
    switch (message.type) {
      case "message":
        // Broadcast message
        for (var i in cluster.workers) {
          cluster.workers[i].send(message);
        }
        break;
      case "new.user":
        // New user
        cluster.workers[message['worker_id']].send({
          type: "user.answer",
          exist: users.has(message['name']),
          name: message['name'],
          chat: message['chat'],
          ws_id: message['ws_id']
        });
        users.add(message['name']);
        break;
      case "delete.user":
        // Delete user
        users.delete(message['name']);
        console.log('Client -' + message['name'] + '- disconnected');
        break;
    }
  });
}

cluster.on('online', function(worker) {
  console.log('New worker with #ID: ' + worker.id);
});

cluster.on('exit', function(worker, code, signal) {
  console.log('Worker ' + worker.process.pid + ' died');
});
