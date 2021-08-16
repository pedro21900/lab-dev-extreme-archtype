import {NgModule} from '@angular/core';
import {SharedModule} from 'src/app/@shared/shared.module';
import {HomePageComponent} from "./home-page.component";
import {HomePageRoutingModule} from "./home-page-routing.module";


@NgModule({
  imports: [
    HomePageRoutingModule,
    SharedModule
  ],
  declarations: [HomePageComponent]
})
export class HomePageModule {
}