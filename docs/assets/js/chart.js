function createChart(name, legend, type, chat_size){
	var myChart = new Chart($("#"+name), {
		type: 'line',
		data: getDataSet(type, chat_size),
		options: getOptions(legend)
	});
}

function getDataSet(type, chat_size){
	var data = {
		labels: [],
		datasets : []
	};
	var apps = []
	for(var i in results){
		for(var j in results[i]){
			var result = results[i][j];
			if(chat_size == Number(result.chatSize)){
				var value = result.numUsers * result.numUsers * 500 * result.chatSize;
				if(data.labels.indexOf(value) == -1) data.labels.push(value);
				if(apps.indexOf(result.app) == -1 ){
					apps.push(result.app);
					var new_index = apps.indexOf(result.app);
					data.datasets.push({
						label: result.app,
						data: [result[type]],
						backgroundColor: 'rgba(0, 0, 0, 0)',
						pointBackgroundColor: colors[new_index],
						borderColor: colors[new_index],
						borderWidth: 2,
						lineTension: 0
					});
				}else{
					var index = apps.indexOf(result.app);
					data.datasets[index].data.push(Math.round(result[type]));
				}
			}
		}
	}
	return data;
}

function getOptions(legend) {
	var options = {
		scales: {
			yAxes: [{
				ticks: {
					padding: 30,
				},
				scaleLabel: {
					display: true,
					labelString: legend
				},
			}],
			xAxes: [{
				gridLines: {
					lineWidth: 1,
					zeroLineColor: "rgba(0, 0, 0, 0)"
				},
				scaleLabel: {
					display: true,
					labelString: "Number of messages sent"
				},
			}]
		}
	}
	return options;
}

var colors = [
	'#ff6384', // RED
	"#32CD32", // OLIVE
	"#008080", // TEAL
	'#46BFBD', // GREEN
	'#FDB45C', // ORANGE
	'#6435C9', // VIOLET
	"#A52A2A", // BROWN
	"#A0A0A0", // GREY
	"#000000"  // BLACK
];
