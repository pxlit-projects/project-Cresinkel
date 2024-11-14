import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { CreatePostComponent } from './create-post/create-post.component';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', component: LoginComponent }, // Default route goes to LoginComponent
  { path: 'create-post', component: CreatePostComponent, canActivate: [AuthGuard] }
];
