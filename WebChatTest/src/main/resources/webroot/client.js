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

		$scope.graphics[chatSizeName].data[k].push(result.avgTime);

		$scope.apps[result.app].results.push(result);
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
		  title: "N users in "+chatSizeName+" chat room(s)",
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
						labelString: "Time in milliseconds"
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
		download(item.title+".png", $('#'+item.chatSize +'-size')[0].toDataURL("image/png"));
	}

	$scope.saveData = function(item){
		$scope.currentItem = item;
		setTimeout(function () {
			download(item.title+'.xls', Excel.tableToExcel('#results',item.title));
		}, 10);
	}

	$scope.slideDown = function(graph){
		// exportToJSON();
		$('#'+graph.chatSize).slideToggle( "fast");
		graph.visible = !graph.visible;
	}

	function exportToJSON(){
		var results = [];
		for(var i in $scope.apps){
			for(var j = 0; j <  $scope.apps[i].results.length; j++){
				var result =  $scope.apps[i].results[j];
				// if(result.app != "Node") continue;
				delete result.$$hashKey;
				results.push(result);
			}
		}
		var data = "text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(results));
		download("data.json", "data: "+data);
	}

	$scope.files = [];
	$scope.fileToJSON = function(data){
		for(var i = 0; i < data.length; i++){
			var _results = JSON.parse(window.atob(data[i].split(',')[1]));
			for(var j = 0; j < _results.length; j++){
				addResult(_results[j]);
			}
		}
	}

	$scope.loadResults = function(){
		
	}

	function download(name, href){
		var a = document.createElement('a');
		a.href = href
		a.download = name;
		a.click();
	}


	/**
		DRAG AND DROP
	*/

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
	    drop_area.className = "area drag";
	}

	function filesDroped(event) {
	    event.stopPropagation();
	    event.preventDefault();

	    drop_area.className = "area";

	    var files = event.dataTransfer.files;
			var reader = new FileReader();

			function readFile(index) {
					if( index >= files.length ) return;

					var file = files[index];
					reader.onload = function(loadEvent) {
							$scope.$apply(function () {
									$scope.files.push(loadEvent.target.result);
									readFile(index+1)
							});
					}
					reader.readAsDataURL(files[index]);
			}
			readFile(0);


	    var filesInfo = "";

	    for (var i = 0; i < files.length; i++) {
	        var file = files[i];

	        filesInfo += "<li>Name: " + file.name + "</li>";

	    }

	    var output = document.getElementById("result");

	    output.innerHTML = "<ul>" + filesInfo + "</ul>";
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
        link: function (scope, element, attributes) {
            element.bind("change", function (changeEvent) {

								var files = changeEvent.target.files;
                var reader = new FileReader();

								function readFile(index) {
										if( index >= files.length ) return;

										var file = files[index];
										reader.onload = function(loadEvent) {
										    scope.$apply(function () {
		                        scope.filesread.push(loadEvent.target.result);
														readFile(index+1)
		                    });
										}
										reader.readAsDataURL(files[index]);
								}
								readFile(0);


            });
        }
    }
}]);
