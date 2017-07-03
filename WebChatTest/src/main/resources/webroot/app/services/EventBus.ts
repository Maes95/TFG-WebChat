import { Injectable } from "@angular/core";

declare class EventBus {
    public constructor( url : string, options : Object);

    public onerror : Function;
    public state : number;
    public send(address : string, message : string, headers : Array<string>, replyCallback : Function) : Function;
    public publish(address : string, message : string, headers : Array<string>) : Function;
    public registerHandler(address : string, headers : Array<string>, callback : Function) : Function;
    public unregisterHandler(address : string, headers : Array<string>, callback : Function) : Function;
    public close() : Function;
}

@Injectable()
export class VertEventBus  {

    /** Our real eventbus client instance. */
    public eventBus : EventBus;

    private errMsg : string;

    /**
     * Creates a new EventBus
     */
    public constructor() {
        this.eventBus = new EventBus('http://localhost:8080/eventbus/', {});
        this.eventBus.onerror = (err: Error) : void => {
            console.log(err);
            this.errMsg = err.toString();
            this.eventBus = null;
        };
    }

    private validate() : void {
        if (this.eventBus == null) {
            throw new Error(`EventBus not connected! [${this.errMsg}]`);
        }
    }

    /**
     * Adds a handler for when messages are received.
     *
     * @param address The address to listen to.
     * @param callback The callback that will be called with new messges.
     * @param headers Filter messages on these headers. Optional.
     */
    public addHandler (address : string, callback : Function, headers : Array<string> ) : VertEventBus {
        this.validate();
        let int = setInterval(()=> {
          if(this.eventBus.state == 1){
            this.eventBus.registerHandler(address, headers, callback);
            clearInterval(int);
          }
        }, 1000)
        return this;
    }

}
