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

  connection:any;
  messages:any[];
  current_message:string;

  constructor(private _chatService:ChatService){
    this.messages = [];
    this.current_message = "";
  }

  ngOnInit() {
    this.connection = this._chatService.getMessages().subscribe(message => {
      this.messages.push(message);
    })

    let my_name = prompt('What is your name?');
    this._chatService.whoim(my_name);
  }

  ngOnDestroy() {
    this.connection.unsubscribe();
  }

  sendMessage(){
    this._chatService.sendMessage(this.current_message);
    this.current_message = "";
  }
}
