const cluster = require('cluster');
const numCPUs = require('os').cpus().length;

cluster.setupMaster({ exec: 'worker.js' });

var users = new Set();

for (var i = 0; i < numCPUs; i++)
  cluster.fork();

cluster.on('message', (worker, message) => {
  switch (message.type) {
    case "message":
      // Broadcast message
      for (var i in cluster.workers)
        cluster.workers[i].send(message);
      break;
    case "new.user":
      // New user
      worker.send({
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
      break;
  }
});

cluster.on('online', (worker) => {
  console.log('New worker with #ID: ' + worker.id);
});

cluster.on('exit', (worker) => {
  console.log('Worker ' + worker.process.pid + ' died');
});
