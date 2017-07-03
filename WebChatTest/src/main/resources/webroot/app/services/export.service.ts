import { Injectable } from "@angular/core";
import * as FileSaver from 'file-saver';
import * as XLSX from 'xlsx';

declare var unescape:Function;

const style = "body{background-color: #f5f5f5 !important;color: #616161 !important;}.item.title{font-size:1.1em;font-weight:700!important}#menu{width:96%;margin:1em auto}body{font-family:sans-serif;color:#333;height:auto;}.main{max-width:90%;margin:3em auto auto}h2.ui.header{margin-top:0;text-align:center}table td{text-align:center!important}table td.app-name{font-size:.9em!important;font-weight:700}.metrics-menu{width:100%!important}table td.metric{font-size:.8em!important}table td.collapsing{font-weight:700}td{font-size:.9em!important}.graphic{width:96%;margin:1em auto}";
declare var $:any;

@Injectable()
export class ExportService {

  public toPDF (id:string, title:string) {
      var css = "<link href='https://rawgit.com/Semantic-Org/Semantic-UI/next/dist/semantic.css' rel='stylesheet'></link>";
      setTimeout(() => {
          var printWindow = window.open("", "", "width=1000, height=800");
          printWindow.document.write('<html><head><title>' + title + '</title>' + css + '</head><body onload="window.print()">' + document.getElementById(id).innerHTML + '</body></html>');
          printWindow.document.close();
      }, 1000);
  }

  public toHTML(id:string, title:string) {
      setTimeout(() => {
          var css = "<link href='https://rawgit.com/Semantic-Org/Semantic-UI/next/dist/semantic.css' rel='stylesheet'></link><style>" + style + "</style>";
          var js = "<script src='https://code.jquery.com/jquery-1.11.2.min.js'></script><script src='https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.9/semantic.min.js'></script>";
          var html = '<html><head><title>' + title + '</title>' + css + js + '</head><body">' + document.getElementById(id).innerHTML + '<script>'+animations+'</script></body></html>';
          this.download('ComparativeReport', 'data:' + 'text/html' + ';charset=utf-8,' + encodeURIComponent(html))
      }, 1000)
  }

  public toPNG(id:string, fileName:string) {
      this.download(fileName + ".png", $(id)[0].toDataURL("image/png"));
  }

  public toJSON(data:Object, fileName:string) {
      this.download(fileName + ".json", "data: " + "text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data)));
  }

  public toXLS(id:string, fileName:string) {
    setTimeout(() => this.download(fileName + '.xls', this.toExcel(id, fileName)), 100);
  }

  private download(name:string, href:string) {
      var a = document.createElement('a');
      a.href = href
      a.download = name;
      a.click();
  }

  private toExcel(tableId: any, worksheetName:any){
    let uri = 'data:application/vnd.ms-excel;base64,';
    let template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>';
    let base64 = function(s:any) {
        return window.btoa(unescape(encodeURIComponent(s)));
    };
    let format = function(s:any, c:any) {
        return s.replace(/{(\w+)}/g, function(m:any, p:any) {
            return c[p];
        })
    };
    let table = $(tableId),
        ctx = {
            worksheet: worksheetName,
            table: table.html()
        },
        href = uri + base64(format(template, ctx));
    return href;
  }
}

const animations = `
    $(".app-detail").each(function(i) {
      var id = $(".app-detail")[i].id;
      $("#"+id).popup({popup: '#'+id+' .popup', on: "click"});
    });

    function select(_this, selector, msg){
      var chatSize = _this.getAttribute('chat-size');
      $('#'+chatSize+' table').hide();
      $('#'+chatSize+' img').hide();
      $('#table-'+chatSize+'-'+selector).show();
      $('#'+chatSize+' img#'+chatSize+selector).show();
      $('#'+chatSize+' .default.text').html(msg);
      if(selector == "cpu"){
        $("#"+chatSize+" .message").show();
      }else{
        $("#"+chatSize+" .message").hide();
      }
    }
    function dropdown(_this){
      var elem = "#"+_this.id.trim();
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
`
