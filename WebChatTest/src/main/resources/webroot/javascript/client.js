angular.module("client", ['chart.js']).controller("resultsController", function($scope, $window, $export,FakeResults, Excel, StaticData, $filter) {

        var n = 0;
        $scope.apps = {};
        $scope.graphics = {};
        $scope.empty = true;

        function addResult(result) {
            $scope.empty = false;
            var chatSizeName = result.chatSize.toString();
            // IF GRAFIC WITH N CHAT ROOMS EXISTS
            if (!$scope.graphics[chatSizeName]) newGraphic(chatSizeName);
            // IF LABEL IN GRAPHIC EXIST
            var label = result.numUsers * result.numUsers * 500 * result.chatSize;
            var index = $scope.graphics[chatSizeName].labels.indexOf(label)
            if (index == -1) newLabel(label, chatSizeName);
            // IF APP EXISTS
            if (!$scope.apps[result.app]) newApp(result.app, result.globalDefinition, result.specificDefinition);
            var k = $scope.apps[result.app].index;

            $scope.graphics[chatSizeName].dataTimes[k].push(result.avgTime);
            $scope.graphics[chatSizeName].dataCpuUse[k].push(Math.round(result.avgCpuUse));
            $scope.graphics[chatSizeName].dataMemoryUse[k].push(Math.round(result.avgRam));

            $scope.apps[result.app].results.push(result);
            if (!$scope.local && !STATIC) $scope.$apply();
        }

        function newApp(app_name, globalDefinition, specificDefinition) {
            $scope.datasetOverride.push({
                lineTension: 0
            });
            $scope.apps[app_name] = {
                name: app_name,
                globalDefinition: globalDefinition,
                specificDefinition: specificDefinition,
                index: n,
                results: []
            }
            n++;
            for (key in $scope.graphics) {
                $scope.graphics[key].dataTimes.push([]);
                $scope.graphics[key].dataCpuUse.push([]);
                $scope.graphics[key].dataMemoryUse.push([]);
                $scope.graphics[key].series.push(app_name);
            }
        }

        function newGraphic(chatSizeName) {
            $scope.graphics[chatSizeName] = {
                chatSize: Number(chatSizeName),
                title: "N users in " + chatSizeName + " chat room(s)",
                labels: [],
                series: [],
                dataTimes: [],
                dataCpuUse: [],
                dataMemoryUse: [],
                tab: 0
            }
            for (key in $scope.apps) {
                $scope.graphics[chatSizeName].series.push(key)
                $scope.graphics[chatSizeName].dataTimes.push([]);
                $scope.graphics[chatSizeName].dataCpuUse.push([]);
                $scope.graphics[chatSizeName].dataMemoryUse.push([]);
            }
        }

        function newLabel(label, graphicName) {
            $scope.graphics[graphicName].labels.push(label);
        }

        $scope.tabApp = 0;
        $scope._tabApp = function(index) {
            $scope.tabApp = index;
        }

        // MULTI-GRAPHIC

        $scope.dataKeys = ["dataTimes", "dataCpuUse", "dataMemoryUse"];
        $scope.dataKeysDescription = ["time (in ms)", "% CPU use", "memory use (in KBytes)"];

        $scope.getData = function(graphic, tab, index) {
            return graphic[$scope.dataKeys[tab]][index];
        }

        $scope.tabLabel = 0;
        $scope._tabLabel = function(name, index) {
            $scope.graphics[name].tab = index;
        }

        // CHART CONFIGURATION

        $scope.onClick = function(points, evt) {
            console.log(points, evt);
        };

        $scope.colors = [
            '#ff6384', // RED
            "#32CD32", // OLIVE
            "#008080", // TEAL
            '#46BFBD', // GREEN
            '#FDB45C', // ORANGE
            '#6435C9', // VIOLET
            "#A52A2A", // BROWN

            "#A0A0A0", // GREY
            "#000000" // BLACK
        ];

        $scope.datasetOverride = [];

        $scope.timeOptions = getOptions('Time in milliseconds');
        $scope.cpuOptions = getOptions('% of CPU');
        $scope.memoryOptions = getOptions('Memory in KBytes');

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

        $scope.dropdown = function(elem) {

            $(".item .menu").not(elem).slideUp("fast", function() {
                $(".item .menu").removeClass('active visible');
            });

            if (!$(elem).hasClass('visible')) {
                $(elem).addClass('active visible');
                $(elem + " .menu").slideDown("fast");
            } else {
                $(elem + " .menu").slideUp("fast", function() {
                    $(elem).removeClass('active visible');
                });
            }
        }

				$scope.slideDown = function(graph) {
						$('#' + graph.chatSize).slideToggle("fast");
						graph.visible = !graph.visible;
				}

        $scope.openModal = function(app) {
            $scope.selectedApp = $scope.apps[app.name];
            $('#appDef.ui.modal').modal('show');
        }


        /**
        	EXPORT
        */


        $scope.generateDocument = function() {
						$export.toHTML("comparativeReport", "Comparative report");
        }

        $scope.getGraphImg = function(item, selector) {
            var elem = $('#' + item.chatSize + '-size-' + selector);
            if(elem.length)
                return elem[0].toDataURL("image/png");
        }

        $scope.exportToPDF = function(item) {
            $scope.currentItem = item;
            $scope.chart_img = $('#' + $scope.currentItem.chatSize + '-size-times')[0].toDataURL("image/png");
						$export.toPDF('toPrint', $scope.currentItem.title);
        }

        $scope.saveImg = function(item) {
						$export.toPNG( '#' + item.chatSize + '-size-times', item.title );
						$export.toPNG( '#' + item.chatSize + '-size-cpu', item.title );
						$export.toPNG( '#' + item.chatSize + '-size-memory', item.title );
        }

        $scope.saveData = function(item) {
            $scope.currentItem = item;
						$export.toXLS('#xls-results-dataTimes', item.title);
            $export.toXLS('#xls-results-dataCpuUse', item.title);
            $export.toXLS('#xls-results-dataMemoryUse', item.title);
        }

        $scope.exportToJSON = function(appName, chatSize) {
            var results = [];
            for (var i in $scope.apps) {
                for (var j = 0; j < $scope.apps[i].results.length; j++) {
                    var result = $scope.apps[i].results[j];
                    if (result.app != appName || (chatSize && Number(result.chatSize) != chatSize)) continue;
                    delete result.$$hashKey;
                    results.push(result);
                }
            }
						var fileName = chatSize ? appName + "_" + chatSize + "_room" : appName;
						$export.toJSON(results, fileName);
        }

        /**
        		UPLOAD RESULTS
        */

        $scope.files = [];
        $scope.results = [];

        $scope.loadResults = function() {
            $scope.uploadData = false;
            for (var i = 0; i < $scope.results.length; i++) {
                addResult($scope.results[i]);
            }
        }

        function enableUploadFiles() {
          $scope.uploadData = true;
          if (window.File && window.FileList) {
              var drop_area = document.getElementById("drop_area");
              drop_area.addEventListener("dragover", dragHandler);
              drop_area.addEventListener("drop", filesDroped);
          } else {
              console.log("Your browser does not support File API");
          }
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

        $scope.onChange = function(changeEvent) {
            readFiles(changeEvent.target.files);
        }

        function readFiles(files) {

            var reader = new FileReader();

            function readFile(index) {
                if (index >= files.length) return;

                var file = files[index];
                reader.onload = function(loadEvent) {
                    $scope.$apply(function() {
                        $scope.files.push(loadEvent.target.result);
                        var _results = JSON.parse(window.atob(loadEvent.target.result.split(',')[1]));
                        for (var j = 0; j < _results.length; j++) {
                            $scope.results.push(_results[j]);
                        }
                        readFile(index + 1)
                    });
                }
                reader.readAsDataURL(files[index]);
            }
            readFile(0);

            var filesInfo = "";
            for (var i = 0; i < files.length; i++) {
                filesInfo += "<li class='ui label'><i class='file icon'></i>" + files[i].name + "</li>";
            }
            var output = document.getElementById("result");
            output.innerHTML = "<ul>" + filesInfo + "</ul>";
        }



        /**
        	SET UP
        */

        $scope.selectMode = function(upload){
          if(upload){
            $scope.local = true;
            $scope.static = false;
            enableUploadFiles();
          }else{
            StaticData.generate(function(result){
              addResult(result);
            });
          }
        }

        STATIC = false;
        $('.main').show();
        if (location.host) {
            if(STATIC){
                // STATIC SERVER
                var param  = window.location.search.substr(1);
                if( param == "view"){
                  $scope.selectMode(false);
                }else if(param == "upload"){
                  $scope.selectMode(true);
                }else{
                  $scope.static = true;
                }
            }else{
              // SERVER UP, OPEN CONNECTION
              //$('#static-loader').hide();
              $scope.loading_text = "Running test";
              $scope.local = false;
              var eb = new EventBus("/eventbus/");
              eb.onopen = function() {
                  eb.registerHandler("new.result", function(err, message) {
                      console.log(JSON.stringify(message.body));
                      addResult(message.body);
                  });
              };
            }
        } else {
            // LOCAL
            $scope.local = true;
            enableUploadFiles();
        }

    })
.directive("filesread", [function() {
    return {
        scope: {
            filesread: "="
        },
        restrict: 'A',
        link: function(scope, element, attributes) {
            element.bind('change', scope.filesread);
        }
    }
}]);
