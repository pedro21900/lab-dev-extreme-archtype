import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DxRadioGroupModule } from 'devextreme-angular/ui/radio-group';
import { DxLoadPanelModule } from 'devextreme-angular/ui/load-panel';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { DxTagBoxModule } from 'devextreme-angular/ui/tag-box';
import { DxButtonModule } from 'devextreme-angular/ui/button';
import { DxFormModule } from 'devextreme-angular/ui/form';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

const ANGULAR_MODULES = [
  FlexLayoutModule,
  CommonModule
]

const DEVEXTREME_MODULES = [
  DxRadioGroupModule,
  DxLoadPanelModule,
  DxDataGridModule,
  DxButtonModule,
  DxTagBoxModule,
  DxFormModule
]

@NgModule({
  imports: [
    DEVEXTREME_MODULES,
    ANGULAR_MODULES
  ],
  exports: [
    DEVEXTREME_MODULES,
    ANGULAR_MODULES,
    FontAwesomeModule,
    FlexLayoutModule,
  ]
})
export class SharedModule { }
