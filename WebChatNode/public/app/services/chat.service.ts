import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { Observer } from 'rxjs/Observer';
import * as io from 'socket.io-client';

export class ChatService {
  private url = 'http://localhost:8080';
  socket: SocketIOClient.Socket;

  constructor(){
    this.socket = io.connect(this.url);
  }

  whoim(my_name: string){
    this.socket.emit('join', my_name);
  }

  sendMessage(message: string){
    this.socket.emit('messages', message);
  }

  getMessages() {
    let observable = new Observable<any>((observer: any) => {

      this.socket.on('messages', (data:any) => {
        observer.next({ type: 'messages', data: data});
      });

      this.socket.on('new chatter', (data:any) => {
        observer.next({ type: 'new chatter', data: data});
      });

      this.socket.on('remove chatter', (data:any) => {
        observer.next({ type: 'remove chatter', data: data});
      });

      return () => {
        this.socket.disconnect();
      };
    })
    return observable;
  }
}
