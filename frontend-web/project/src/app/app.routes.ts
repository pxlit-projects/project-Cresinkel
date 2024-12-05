import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { CreatePostComponent } from './create-post/create-post.component';
import { AuthGuard } from './auth.guard';
import {DraftsComponent} from "./drafts/drafts.component";
import {PostsComponent} from "./posts/posts.component";
import {EditDraftComponent} from "./edit-draft/edit-draft.component";

export const routes: Routes = [
  { path: '', component: LoginComponent }, // Default route goes to LoginComponent
  { path: 'create-post', component: CreatePostComponent, canActivate: [AuthGuard] },
  { path: 'drafts', component: DraftsComponent, canActivate: [AuthGuard] },
  { path: 'posts', component: PostsComponent },
  { path: 'edit-draft/:id', component: EditDraftComponent, canActivate: [AuthGuard] },
];
