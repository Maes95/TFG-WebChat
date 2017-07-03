import { Routes, RouterModule } from '@angular/router';

import { AppComponent }  from '../app/app.component';
import { DashboardComponent } from '../components/dashboard/dashboard.component';

const appRoutes: Routes = [
  {
    path: '',
    component: DashboardComponent
  }
];

export const routing = RouterModule.forRoot(appRoutes);
