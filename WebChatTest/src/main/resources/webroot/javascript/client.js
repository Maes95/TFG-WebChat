angular.module("client", ['chart.js']).controller("resultsController", function($scope, $window, FakeResults, Excel) {

	var n = 0;
	$scope.apps = {};
	$scope.graphics = {};
	$scope.empty = true;

	function addResult(result){
		$scope.empty = false;
		var chatSizeName = result.chatSize.toString();
		// IF GRAFIC WITH N CHAT ROOMS EXISTS
		if(!$scope.graphics[chatSizeName]) newGraphic(chatSizeName);
		// IF LABEL IN GRAPHIC EXIST
		var index = $scope.graphics[chatSizeName].labels.indexOf(result.numUsers)
		if(index == -1) newLabel(result.numUsers, chatSizeName);
		// IF APP EXISTS
		if(!$scope.apps[result.app]) newApp(result.app);
		var k = $scope.apps[result.app].index;

		$scope.graphics[chatSizeName].dataTimes[k].push(result.avgTime);
		$scope.graphics[chatSizeName].dataCpuUse[k].push(result.avgCpuUse);
		$scope.graphics[chatSizeName].dataMemoryUse[k].push(result.avgMemoryUse);

		$scope.apps[result.app].results.push(result);
		if(!$scope.local) $scope.$apply();
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
			$scope.graphics[key].dataTimes.push([]);
			$scope.graphics[key].dataCpuUse.push([]);
			$scope.graphics[key].dataMemoryUse.push([]);
			$scope.graphics[key].series.push(app_name);
		}
	}

	function newGraphic(chatSizeName){
		$scope.graphics[chatSizeName] = {
		  chatSize: Number(chatSizeName),
		  title: "N users in "+chatSizeName+" chat room(s)",
		  labels: [],
		  series: [],
		  dataTimes: [],
			dataCpuUse : [],
			dataMemoryUse : [],
		  tab: 0
		}
		for(key in $scope.apps){
			$scope.graphics[chatSizeName].series.push(key)
			$scope.graphics[chatSizeName].dataTimes.push([]);
			$scope.graphics[chatSizeName].dataCpuUse.push([]);
			$scope.graphics[chatSizeName].dataMemoryUse.push([]);
		}
	}

	function newLabel(label, graphicName){
		$scope.graphics[graphicName].labels.push(label);
	}

	$scope.tabApp = 0;
	$scope._tabApp = function(index){
		$scope.tabApp = index;
	}

	// MULTI-GRAPHIC

	var dataKeys = ["dataTimes", "dataCpuUse", "dataMemoryUse"];

	$scope.getData = function(graphic, index){
		return graphic[dataKeys[graphic.tab]][index];
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

	$scope.timeOptions = getOptions('Time in milliseconds');
	$scope.cpuOptions = getOptions('% of CPU');
	$scope.memoryOptions = getOptions('% of Memory');

	function getOptions(legend){
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
						gridLines :{
							lineWidth: 1,
							zeroLineColor: "rgba(0, 0, 0, 0)"
						},
						scaleLabel: {
							display: true,
							labelString: "Number of users per chat room"
						},
		      }]
	    }
		}
		return options;
	}

	$scope.dropdown = function(elem){

	 $(".item .menu").not(elem).slideUp( "fast", function() {
		 $(".item .menu").removeClass('active visible');
	 });

   if(!$(elem).hasClass('visible')){
     $(elem).addClass('active visible');
     $(elem+" .menu").slideDown( "fast");
   }else {
     $(elem+" .menu").slideUp( "fast", function() {
       $(elem).removeClass('active visible');
     });
   }
  }


	/**
		EXPORT
	*/

	var css = "<link href='https://rawgit.com/Semantic-Org/Semantic-UI/next/dist/semantic.css' rel='stylesheet'></link>";
	$scope.exportToPDF = function(item){
		$scope.currentItem = item;
		$scope.chart_img = $('#'+$scope.currentItem.chatSize +'-size-times')[0].toDataURL("image/png");
		setTimeout(function () {
			var title = $scope.currentItem.title;
			var printWindow = window.open("", "", "width=1000, height=800");
			printWindow.document.write('<html><head><title>'+title+'</title>'+css+'</head><body onload="window.print()">' + document.getElementById('toPrint').innerHTML + '</body></html>');
			printWindow.document.close();
		}, 1000);

	}

	$scope.saveImg = function(item){
		download(item.title+".png", $('#'+item.chatSize +'-size-times')[0].toDataURL("image/png"));
	}

	$scope.saveData = function(item){
		$scope.currentItem = item;
		setTimeout(function () {
			download(item.title+'.xls', Excel.tableToExcel('#results',item.title));
		}, 10);
	}

	$scope.slideDown = function(graph){
		$('#'+graph.chatSize).slideToggle( "fast");
		graph.visible = !graph.visible;
	}

	$scope.exportToJSON = function(appName, chatSize){
		var results = [];
		for(var i in $scope.apps){
			for(var j = 0; j <  $scope.apps[i].results.length; j++){
				var result =  $scope.apps[i].results[j];
				if(result.app != appName || (chatSize && Number(result.chatSize) != chatSize)) continue;
				delete result.$$hashKey;
				results.push(result);
			}
		}
		var data = "text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(results));
		var fileName = chatSize ? appName + "_"+chatSize+"_room": appName;
		download(fileName+".json", "data: "+data);
	}

	function download(name, href){
		var a = document.createElement('a');
		a.href = href
		a.download = name;
		a.click();
	}


	/**
			UPLOAD RESULTS
	*/

	$scope.files = [];
	$scope.results = [];

	$scope.loadResults = function(){
		for(var i = 0; i < $scope.results.length; i++){
			addResult($scope.results[i]);
		}
	}

	if (window.File && window.FileList) {
			var drop_area = document.getElementById("drop_area");
			drop_area.addEventListener("dragover", dragHandler);
			drop_area.addEventListener("drop", filesDroped);
	}
	else {
			console.log("Your browser does not support File API");
	}

	function dragHandler(event) {
	    event.stopPropagation();
	    event.preventDefault();
	    var drop_area = document.getElementById("drop_area");
	    drop_area.className = "drop-area drag";
	}

	function filesDroped(event) {
	    event.stopPropagation();
	    event.preventDefault();
			readFiles(event.dataTransfer.files);
	}

	$scope.onChange = function(changeEvent){
		readFiles(changeEvent.target.files);
	}

	function readFiles(files){

		var reader = new FileReader();

		function readFile(index) {
				if( index >= files.length ) return;

				var file = files[index];
				reader.onload = function(loadEvent) {
						$scope.$apply(function () {
								$scope.files.push(loadEvent.target.result);
								var _results = JSON.parse(window.atob(loadEvent.target.result.split(',')[1]));
								for(var j = 0; j < _results.length; j++){
									$scope.results.push(_results[j]);
								}
								readFile(index+1)
						});
				}
				reader.readAsDataURL(files[index]);
		}
		readFile(0);

		var filesInfo = "";
		for (var i = 0; i < files.length; i++) {
				filesInfo += "<li class='ui label'><i class='big file icon'></i>" + files[i].name + "</li>";
		}
		var output = document.getElementById("result");
		output.innerHTML = "<ul>" + filesInfo + "</ul>";
	}



	/**
		SET UP
	*/

	if(location.host){
		// SERVER UP, OPEN CONNECTION
		$scope.local = false;
		var eb = new EventBus("/eventbus/");
		eb.onopen = function () {
			eb.registerHandler("new.result", function(err, message){
				console.log(JSON.stringify(message.body));
				addResult(message.body);
			});
		};
	}else{
		// NO SERVER AVAILABLE
		$scope.local = true;
		// FakeResults.generate(function(result){
		// 	addResult(result);
		// });
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
.directive("filesread", [function () {
    return {
        scope: {
            filesread: "="
        },
				restrict: 'A',
        link: function (scope, element, attributes) {
						element.bind('change', scope.filesread);
        }
    }
}]);
