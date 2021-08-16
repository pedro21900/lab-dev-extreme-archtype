import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AppInitService {

  constructor() { }

  public async init() {
    console.log("Inicializando aplicação com AppInitService.init()");
  }

}
