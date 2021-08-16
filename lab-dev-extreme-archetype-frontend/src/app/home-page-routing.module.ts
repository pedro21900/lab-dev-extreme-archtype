import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeRootRouteFragment} from "./view/home/home-root-route-fragment";

const routes: Routes = [
  HomeRootRouteFragment,

  { path: '**', redirectTo: 'home' }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
