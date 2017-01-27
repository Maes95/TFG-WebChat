const cluster = require('cluster');
const numCPUs = require('os').cpus().length;

cluster.setupMaster({ exec: 'worker.js' });

for (var i = 0; i < numCPUs; i++) {
  cluster.fork();
}

cluster.on('online', function(worker) {
  console.log('New worker with #ID: ' + worker.id);
});

cluster.on('exit', function(worker, code, signal) {
  console.log('Worker ' + worker.process.pid + ' died');
});

cluster.on('message', (msg) => {
  for (var id in cluster.workers) {
    cluster.workers[id].send(msg);
  }
})
