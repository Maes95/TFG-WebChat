angular.module("client", ['chart.js']).controller("resultsController", function($scope, $window, FakeResults, Excel) {

	var n = 0;
	$scope.apps = {};
	$scope.graphics = {};

	function addResult(result){
		var chatSizeName = result.chatSize.toString();
		// IF GRAFIC WITH N CHAT ROOMS EXISTS
		if(!$scope.graphics[chatSizeName]) newGraphic(chatSizeName);
		// IF LABEL IN GRAPHIC EXIST
		var index = $scope.graphics[chatSizeName].labels.indexOf(result.numUsers)
		if(index == -1) newLabel(result.numUsers, chatSizeName);
		// IF APP EXISTS
		if(!$scope.apps[result.app]) newApp(result.app);
		var k = $scope.apps[result.app].index;

		$scope.graphics[chatSizeName].data[k].push(result.avgTime);

		$scope.apps[result.app].results.push(result);
		$scope.$apply();
	}

	function newApp(app_name){
		$scope.datasetOverride.push({lineTension: 0});
		$scope.apps[app_name] = {
			name: app_name,
			index: n,
			results: []
		}
		n++;
		for(key in $scope.graphics){
			$scope.graphics[key].data.push([]);
			$scope.graphics[key].series.push(app_name);
		}
	}

	function newGraphic(chatSizeName){
		$scope.graphics[chatSizeName] = {
		  chatSize: Number(chatSizeName),
		  title: "N usuarios en "+chatSizeName+" sala(s) de chat",
		  labels: [],
		  series: [],
		  data: [],
		  tab: 0
		}
		for(key in $scope.apps){
			$scope.graphics[chatSizeName].series.push(key)
			$scope.graphics[chatSizeName].data.push([]);
		}
	}

	function newLabel(label, graphicName){
		$scope.graphics[graphicName].labels.push(label);
	}

	$scope.tabApp = 0;
	$scope._tabApp = function(index){
		$scope.tabApp = index;
	}

	$scope.tabLabel = 0;
	$scope._tabLabel = function(name, index){ $scope.graphics[name].tab = index; }

	// CHART CONFIGURATION

	$scope.onClick = function (points, evt) {
		console.log(points, evt);
	};

	$scope.colors = [
		'#46BFBD', // GREEN
		'#6435C9', // VIOLET
		'#ff6384', // RED
		'#FDB45C', // ORANGE
                '#45b7cd',  // BLUE
		"#FE9A76", // ORANGE
		"#008080", // TEAL
		"#32CD32", // OLIVE
		"#FF1493", // PINK
		"#FFD700", // YELLOW
		"#A52A2A", // BROWN
 		"#A0A0A0", // GREY
                "#000000"  // BLACK
  ];

	$scope.datasetOverride = [];

	$scope.options = {
    scales: {
			yAxes: [{
					ticks: {
						padding: 30,
					},
					scaleLabel: {
						display: true,
						labelString: "Tiempo en milisegundos"
					},
	      }],
	    xAxes: [{
					gridLines :{
						lineWidth: 1,
						zeroLineColor: "rgba(0, 0, 0, 0)"
					},
					scaleLabel: {
						display: true,
						labelString: "Número de usuarios por sala"
					},
	      }]
    }
	};

	var css = "<link href='https://rawgit.com/Semantic-Org/Semantic-UI/next/dist/semantic.css' rel='stylesheet'></link>";
	$scope.exportToPDF = function(item){
		$scope.currentItem = item;
		$scope.chart_img = $('#'+$scope.currentItem.chatSize +'-size')[0].toDataURL("image/png");
		setTimeout(function () {
			var title = $scope.currentItem.title;
			var printWindow = window.open("", "", "width=1000, height=800");
			printWindow.document.write('<html><head><title>'+title+'</title>'+css+'</head><body onload="window.print()">' + document.getElementById('toPrint').innerHTML + '</body></html>');
			printWindow.document.close();
		}, 1000);

	}

	$scope.saveImg = function(item){
		var link = document.createElement('a');
		link.href = $('#'+item.chatSize +'-size')[0].toDataURL("image/png");
		link.download = item.title+'.png';
		document.body.appendChild(link);
		link.click();
	}

	$scope.saveData = function(item){
		$scope.currentItem = item;
		setTimeout(function () {
			var a = document.createElement('a');
	    a.href = Excel.tableToExcel('#results',item.title);;
	    a.download = item.title+'.xls';
	    a.click();
		}, 10);
	}

	if(location.host){
		// SERVER UP, OPEN CONNECTION
		var eb = new EventBus("/eventbus/");
		eb.onopen = function () {
			eb.registerHandler("new.result", function(err, message){
			console.log(JSON.stringify(message.body));
			addResult(message.body);
			});
		};
	}else{
		// NO SERVER AVAILABLE
		FakeResults.generate(function(result){
			addResult(result);
		});
	}

})
.factory('Excel',function($window){
    var uri='data:application/vnd.ms-excel;base64,',
        template='<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
        base64=function(s){return $window.btoa(unescape(encodeURIComponent(s)));},
        format=function(s,c){return s.replace(/{(\w+)}/g,function(m,p){return c[p];})};
    return {
        tableToExcel:function(tableId,worksheetName){
            var table=$(tableId),
                ctx={worksheet:worksheetName,table:table.html()},
                href=uri+base64(format(template,ctx));
            return href;
        }
    };
})
