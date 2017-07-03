import { Component, OnInit } from '@angular/core';
import { Result, Message } from '../../models/result';
import { KeysPipe } from '../../pipes/keys.pipe';
import { VertEventBus } from '../../services/EventBus';
import { ExportService } from '../../services/export.service';
import { FakeResultsService } from '../../services/fakeResults.service';

declare var $:any;

@Component({
    selector: 'dashboard',
    templateUrl: '../app/components/dashboard/dashboard.component.html',
    providers: [VertEventBus,ExportService, FakeResultsService],
    pipes: [KeysPipe],
    styleUrls: ['./app/components/dashboard/dashboard.component.css']
})
export class DashboardComponent implements OnInit{

  STATIC:boolean = true;

  datasetOverride:any[] = [];

  timeOptions: Object;
  cpuOptions: Object;
  memoryOptions: Object;

  colors:any[] = [];

  n = 0;
  apps = {};
  graphics = {};
  empty = true;
  selectedApp: Object;
  currentItem: Object;

  dataKeys = ["dataTimes", "dataCpuUse", "dataMemoryUse"];
  dataKeysDescription = ["time (in ms)", "% CPU use", "memory use (in KBytes)"];

  defaultColors:number[][] = [
     [255, 99, 132],
     [54, 162, 235],
     [255, 206, 86],
     [165, 105, 189],
     [75, 192, 192],
     [151, 187, 205],
     [253, 180, 92],

     [148, 159, 177],
     [77, 83, 96]
  ];

  showAll:boolean;

  constructor(private _eventBus: VertEventBus, private _export: ExportService, private _fake: FakeResultsService){}

  ngOnInit(){
    for( let c of this.defaultColors ){
      this.colors.push(this.formatLineColor(c));
    }
    this.selectedApp = {};
    this.currentItem = { labels: [] };
    this.timeOptions = this.getOptions('Time in milliseconds');
    this.cpuOptions = this.getOptions('% of CPU');
    this.memoryOptions = this.getOptions('Memory in KBytes');

    if(window.location.host == "localhost:9000"){
      // DEVELOPMENT
      this._fake.generateResults((result:Result)=> this.addResult(result));
    }else{
      // PRODUCTION
      this._eventBus.addHandler("new.result", (err:any, message:Message) => {
          console.log(message.body);
          this.addResult(message.body);
      }, []);
    }
  }

  private formatLineColor(colors: number[]) {
     return {
         backgroundColor: 'rgba(0,0,0,0)',
         borderColor: 'rgba('+colors+', 1)',
         pointBackgroundColor: 'rgba('+colors+', 1)',
         pointBorderColor: '#fff',
         pointHoverBackgroundColor: '#fff',
         pointHoverBorderColor: 'rgba('+colors+', 0.8)'
     };
  }

  private addResult(result: Result){
    this.empty = false;
    let chatSizeName = result.chatSize.toString();
    // IF GRAFIC WITH N CHAT ROOMS EXISTS
    if (!this.graphics[chatSizeName])
      this.newGraphic(chatSizeName);
    // IF LABEL IN GRAPHIC EXIST
    let label = result.numUsers * result.numUsers * 500 * result.chatSize;
    let index = this.graphics[chatSizeName].labels.indexOf(label)
    if (index == -1) this.newLabel(label, chatSizeName);
    // IF APP EXISTS
    if (!this.apps[result.app])
      this.newApp(result.app, result.globalDefinition, result.specificDefinition);
    let k = this.apps[result.app].index;

    this.graphics[chatSizeName].dataTimes[k].data.push(result.avgTime);
    this.graphics[chatSizeName].dataCpuUse[k].data.push(Math.round(result.avgCpuUse));
    this.graphics[chatSizeName].dataMemoryUse[k].data.push(Math.round(result.avgRam));

    this.apps[result.app].results.push(result);

    this.graphics[chatSizeName].dataTimes = this.graphics[chatSizeName].dataTimes.slice();
    this.graphics[chatSizeName].dataCpuUse = this.graphics[chatSizeName].dataCpuUse.slice();
    this.graphics[chatSizeName].dataMemoryUse = this.graphics[chatSizeName].dataMemoryUse.slice();
  }

  private newApp(app_name:string, globalDefinition:string, specificDefinition:string) {
      this.datasetOverride.push({
          lineTension: 0
      });
      this.apps[app_name] = {
          name: app_name,
          globalDefinition: globalDefinition,
          specificDefinition: specificDefinition,
          index: this.n,
          results: []
      }
      this.n++;
      for (let key in this.graphics) {
          this.graphics[key].dataTimes.push({ data: [], label: app_name });
          this.graphics[key].dataCpuUse.push({ data: [], label: app_name });
          this.graphics[key].dataMemoryUse.push({ data: [], label: app_name });
          this.graphics[key].series.push(app_name);
      }
  }

  private newGraphic(chatSizeName: string) {
      this.graphics[chatSizeName] = {
          chatSize: Number(chatSizeName),
          title: "N users in " + chatSizeName + " chat room(s)",
          labels: [],
          series: [],
          dataTimes: [],
          dataCpuUse: [],
          dataMemoryUse: [],
          tab: 0
      }
      for (let key in this.apps) {
          this.graphics[chatSizeName].series.push(key)
          this.graphics[chatSizeName].dataTimes.push({ data: [], label: key });
          this.graphics[chatSizeName].dataCpuUse.push({ data: [], label: key });
          this.graphics[chatSizeName].dataMemoryUse.push({ data: [], label: key });
      }
      this.graphics[chatSizeName].current_tab = 'Response time';
      this.graphics[chatSizeName].tab = 0
      this.graphics[chatSizeName].visible = true;
  }

  private newLabel(label: number, graphicName: string) {
      this.graphics[graphicName].labels.push(label);
  }

  private getOptions(legend: string):Object{
     let options = {
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
         },
         lineTension:1
     };
     return options;
 }

 public getData(graphic: any, tab: any, index:number): any{
     return graphic[this.dataKeys[tab]][index].data;
 }

 /**
  *  EXPORTS
  */

 public generateDocument () {
     this.showAll = true;
     this._export.toHTML("comparativeReport", "Comparative report");
     setTimeout(() => this.showAll = false, 100);
 }

 public getGraphImg (item:any, selector:any) {
     var elem = $('#' + item.chatSize + '-size-' + selector);
     if(elem.length)
         return elem[0].toDataURL("image/png");
 }

 public saveImg (item:any) {
   switch(item.tab){
     case 0: this._export.toPNG( '#' + item.chatSize + '-size-times', item.title ); break;
     case 1: this._export.toPNG( '#' + item.chatSize + '-size-cpu', item.title ); break;
     case 2: this._export.toPNG( '#' + item.chatSize + '-size-memory', item.title ); break;
   }
 }

 public saveData (item:any) {
     this.currentItem = item;
     this._export.toXLS($('#xls-results-dataTimes'), item.title);
     this._export.toXLS($('#xls-results-dataCpuUse'), item.title);
     this._export.toXLS($('#xls-results-dataMemoryUse'), item.title);
 }

 public exportToJSON (appName:string, chatSize:number) {
     var results = [];
     for (var i in this.apps) {
         for (var j = 0; j < this.apps[i].results.length; j++) {
             var result = this.apps[i].results[j];
             if (result.app != appName || (chatSize && Number(result.chatSize) != chatSize)) continue;
             results.push(result);
         }
     }
     var fileName = chatSize ? appName + "_" + chatSize + "_room" : appName;
     this._export.toJSON(results, fileName);
 }

 /**
  *  VIEW FUNCTIONS
  */

 getColor(index: number){
   let color = this.colors[index];
   return color ? color.borderColor : 'black';
 }

 openModal(app: any) {
     this.selectedApp = this.apps[app.name];
     $('#appDef.ui.modal').modal('show');
 }

 dropdown(elem:any) {

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

 slideDown(graph:any) {
     $('#' + graph.chatSize).slideToggle("fast");
     graph.visible = !graph.visible;
 }

}
