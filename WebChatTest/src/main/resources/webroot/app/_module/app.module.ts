import { NgModule }       from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { FormsModule }    from '@angular/forms';
import { HttpModule }     from '@angular/http';

import { ChartsModule } from 'ng2-charts';

import { AppComponent }  from '../app/app.component';

import { DashboardComponent } from '../components/dashboard/dashboard.component';

import { routing }        from '../_routing/app.routing';

@NgModule({
  imports: [
    BrowserModule,
    routing,
    FormsModule,
    ChartsModule
  ],
  declarations: [
    AppComponent,
    DashboardComponent
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
