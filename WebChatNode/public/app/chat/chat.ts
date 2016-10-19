import { Component, OnInit,OnDestroy } from '@angular/core';
import { ChatService } from '../services/chat.service';

@Component({
    selector: 'app-home',
    directives: [],
    templateUrl: '../../app/chat/chat.html',
    providers: [ChatService],
    styleUrls: ['../../app/chat/chat.css']
})
export class ChatComponent implements OnInit, OnDestroy{

  my_name:string;

  connection:any;
  messages:any[];
  chatters: any[];
  current_message:string;

  constructor(private _chatService:ChatService){
    this.messages = [];
    this.chatters = [];
    this.current_message = "";
  }

  ngOnInit() {
    this.connection = this._chatService.getMessages().subscribe(event => {
      switch(event.type){
        case 'messages':  this.addMessage(event.data); break;
        case 'new chatter': this.addChatter(event.data); break;
        case 'remove chatter': this.removeChatter(event.data); break;
        default: console.log("Unknow event: "+event);
      }
    })

    this.my_name = prompt('What is your name?');
    this._chatService.whoim(this.my_name);
  }

  ngOnDestroy() {
    this.connection.unsubscribe();
  }

  /*
   * EVENT EMMITERS
   */

  sendMessage(){
    this._chatService.sendMessage(this.current_message);
    this.messages.push(this.my_name + ' : ' + this.current_message );
    this.current_message = "";
  }

  /*
   *  EVENTS CALLBACKS
   */

  addMessage(data:any){
    this.messages.push(data.name + ' : ' + data.text );
  }

  addChatter(name: any){
    this.chatters.push(name);
  }

  removeChatter(name:any){
    let index = this.chatters.indexOf(name);
    if(index != -1) this.chatters.splice(index, 1);
  }
}
