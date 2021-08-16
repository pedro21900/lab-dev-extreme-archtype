import { Component } from '@angular/core';
import { locale, loadMessages } from 'devextreme/localization';
import ptMessages from 'devextreme/localization/messages/pt.json';

@Component({
  selector: 'app-root',
  templateUrl: './home-page.component.html'
})
export class HomePageComponent {

  constructor() { 
    loadMessages(ptMessages);
    locale(navigator.language);
  }

}
