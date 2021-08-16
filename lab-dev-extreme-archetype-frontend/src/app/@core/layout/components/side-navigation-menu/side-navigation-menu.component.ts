import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  NgModule,
  OnDestroy, OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {DxTreeViewComponent, DxTreeViewModule} from 'devextreme-angular/ui/tree-view';
import {navigation} from '../../../../app-navigation';
import * as events from 'devextreme/events';
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-side-navigation-menu',
  templateUrl: './side-navigation-menu.component.html',
  styleUrls: ['./side-navigation-menu.component.scss']
})
export class SideNavigationMenuComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild(DxTreeViewComponent, {static: true})
  menu: DxTreeViewComponent;

  @Output()
  selectedItemChanged = new EventEmitter<string>();

  @Output()
  openMenu = new EventEmitter<any>();

  items: any[];

  /**
   * Lista com todas as roles do usuário obtida pelo Keycloak.
   * Array inicializado no construtor.
   * @private
   */
  private roles = [];

  constructor(private elementRef: ElementRef,
              private keycloakService: KeycloakService) {
    this.roles = keycloakService.getUserRoles();
  }

  private _selectedItem: String;
  @Input()
  set selectedItem(value: String) {
    this._selectedItem = value;
    if (!this.menu.instance) {
      return;
    }

    this.menu.instance.selectItem(value);
  }

  ngOnInit(): void {
    this.items = navigation
      .filter(item => this.applySecurityByRoles(item))
      .map((item) => {
        if (item.path && !(/^\//.test(item.path))) {
          item.path = `/${item.path}`;
        }
        return {...item, expanded: !this._compactMode}
      });

  }

  private applySecurityByRoles(item): boolean {
    // Get the roles required from the route.
    const requiredRoles = item.data?.roles;
    // Allow the user to to proceed if no additional roles are required to access the route.
    if (!(requiredRoles instanceof Array) || requiredRoles.length === 0) {
      return true;
    }
    return requiredRoles.some(role => this.roles.includes(role));
  }

  private _compactMode = false;
  @Input()
  get compactMode() {
    return this._compactMode;
  }

  set compactMode(val) {
    this._compactMode = val;

    if (!this.menu.instance) {
      return;
    }

    if (val) {
      this.menu.instance.collapseAll();
    } else {
      this.menu.instance.expandItem(this._selectedItem);
    }
  }


  onItemClick(event) {
    this.selectedItemChanged.emit(event);
  }

  ngAfterViewInit() {
    events.on(this.elementRef.nativeElement, 'dxclick', (e) => {
      this.openMenu.next(e);
    });
  }

  ngOnDestroy() {
    events.off(this.elementRef.nativeElement, 'dxclick');
  }
}

@NgModule({
  imports: [DxTreeViewModule, KeycloakAngularModule],
  declarations: [SideNavigationMenuComponent],
  exports: [SideNavigationMenuComponent]
})
export class SideNavigationMenuModule {
}
