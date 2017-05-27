function createBubleChart(name, chatSize, numUsers, title){
  var myBubbleChart = new Chart($("#"+name),{
      type: 'bubble',
      data: getDataSetBuble(chatSize, numUsers),
      options: getOptionsBuble(chatSize, numUsers, title)
  });
}

function getDataSetBuble(chatSize, numUsers){
	var data = {
    datasets: []
  };
  var cont = 0;
  for(var i in results){
    for(var j in results[i]){
      var result = results[i][j];
      if(chatSize == Number(result.chatSize) && Number(result.numUsers) == numUsers){
        data.datasets.push({
          label: [result.app],
          backgroundColor: colors[cont++],
          data: [{
            x: result.avgRam,
            y: result.avgCpuUse,
            r: result.avgTime / 200
          }]
        })
      }
    }
  }
	return data;
}

function getOptionsBuble(chatSize, numUsers, title) {
	var options = {
      title: {
        display: title,
        text: title
      }, scales: {
        yAxes: [{
          scaleLabel: {
            display: true,
            labelString: "% CPU use"
          }
        }],
        xAxes: [{
          scaleLabel: {
            display: true,
            labelString: "Memory (in KBytes)"
          }
        }]
      }
    }
	return options;
}
