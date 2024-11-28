import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { CreatePostComponent } from './create-post/create-post.component';
import { AuthGuard } from './auth.guard';
import {DraftsComponent} from "./drafts/drafts.component";
import {PostsComponent} from "./posts/posts.component";

export const routes: Routes = [
  { path: '', component: LoginComponent }, // Default route goes to LoginComponent
  { path: 'create-post', component: CreatePostComponent, canActivate: [AuthGuard] },
  { path: 'drafts', component: DraftsComponent, canActivate: [AuthGuard] },
  { path: 'posts', component: PostsComponent, canActivate: [AuthGuard] }
];
