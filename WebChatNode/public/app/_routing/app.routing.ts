import { Routes, RouterModule } from '@angular/router';

import { AppComponent }  from '../app/app.component';
import { ChatComponent } from '../chat/chat';

const appRoutes: Routes = [
  {
    path: '',
    redirectTo: '/chat',
    pathMatch: 'full'
  },
  {
    path: 'chat',
    component: ChatComponent
  }
];

export const routing = RouterModule.forRoot(appRoutes);
