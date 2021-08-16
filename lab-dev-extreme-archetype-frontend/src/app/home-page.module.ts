import {  NgModule } from '@angular/core';
import { SharedModule } from './@shared/shared.module';
import { HomePageComponent } from './view/home/home-page.component';
import { AppRoutingModule } from './app-routing.module';
import {  DxChartModule, DxPieChartModule } from 'devextreme-angular';   

@NgModule({
  imports: [AppRoutingModule, SharedModule, DxPieChartModule,DxChartModule ],
  declarations: [HomePageComponent ]})
  export class HomePageModule {}