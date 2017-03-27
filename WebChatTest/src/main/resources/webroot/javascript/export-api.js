angular.module("client")
    .service('$export', function(Excel) {

        this.toPDF = function(id, title) {
            var css = "<link href='https://rawgit.com/Semantic-Org/Semantic-UI/next/dist/semantic.css' rel='stylesheet'></link>";
            setTimeout(function() {
                var printWindow = window.open("", "", "width=1000, height=800");
                printWindow.document.write('<html><head><title>' + title + '</title>' + css + '</head><body onload="window.print()">' + document.getElementById(id).innerHTML + '</body></html>');
                printWindow.document.close();
            }, 1000);
        }

        var style = ".item.title{font-size:1.1em;font-weight:700!important}#menu{width:96%;margin:1em auto}body{font-family:sans-serif;color:#333;height:auto;background-image:radial-gradient(circle farthest-corner at center,#3C4B57 0,#1C262B 100%)}.main{max-width:90%;margin:3em auto auto}h2.ui.header{margin-top:0;text-align:center}table td{text-align:center!important}table td.app-name{font-size:.9em!important;font-weight:700}.metrics-menu{width:100%!important}table td.metric{font-size:.8em!important}table td.collapsing{font-weight:700}td{font-size:.9em!important}.graphic{width:96%;margin:1em auto}"

        this.toHTML = function(id, title) {
            setTimeout(function() {
                var css = "<link href='https://rawgit.com/Semantic-Org/Semantic-UI/next/dist/semantic.css' rel='stylesheet'></link><style>" + style + "</style>";
                var js = "<script src='https://code.jquery.com/jquery-1.11.2.min.js'></script><script src='https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.9/semantic.min.js'></script>";
                var html = '<html><head><title>' + title + '</title>' + css + js + '</head><body">' + document.getElementById(id).innerHTML + '</body></html>';
                download('fullDocument', 'data:' + 'text/html' + ';charset=utf-8,' + encodeURIComponent(html))
            }, 1000)
        }

        this.toPNG = function(id, fileName) {
            download(fileName + ".png", $(id)[0].toDataURL("image/png"));
        }

        this.toJSON = function(data, fileName) {
            download(fileName + ".json", "data: " + "text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data)));
        }

        this.toXLS = function(id, fileName) {
            setTimeout(function() {
                download(fileName + '.xls', Excel.tableToExcel(id, fileName));
            }, 10);
        }

        function download(name, href) {
            var a = document.createElement('a');
            a.href = href
            a.download = name;
            a.click();
        }
    })
    .factory('Excel', function($window) {
        var uri = 'data:application/vnd.ms-excel;base64,',
            template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
            base64 = function(s) {
                return $window.btoa(unescape(encodeURIComponent(s)));
            },
            format = function(s, c) {
                return s.replace(/{(\w+)}/g, function(m, p) {
                    return c[p];
                })
            };
        return {
            tableToExcel: function(tableId, worksheetName) {
                var table = $(tableId),
                    ctx = {
                        worksheet: worksheetName,
                        table: table.html()
                    },
                    href = uri + base64(format(template, ctx));
                return href;
            }
        };
    })
