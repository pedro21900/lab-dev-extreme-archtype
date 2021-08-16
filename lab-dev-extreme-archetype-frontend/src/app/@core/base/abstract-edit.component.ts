import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {take} from 'rxjs/operators';
import notify from 'devextreme/ui/notify';
import {AbstractCrudService} from './abstract-crud.service';

/**
 * Classe base para componentes de edição.
 */
export abstract class AbstractEditComponent<T, ID> {
  id: ID;
  resource: T;

  protected abstract readonly ROUTE_PATH: string;

  protected abstract readonly ROUTE_ID: string;

  protected _fieldOptions: Record<string, any>;

  protected _buttonOptions: Record<string, any>;

  public saveSub: Subscription;

  protected constructor(
    private _activatedRoute: ActivatedRoute,
    private service: AbstractCrudService<T, ID>,
    private _router: Router
  ) {
  }

  public get fieldOptions(): Record<string, any> {
    if (!this._fieldOptions) {
      this._fieldOptions = this.prepareFieldOptions();
    }
    return this._fieldOptions;
  }

  public isEditMode(): boolean {
    return this.id !== undefined && this.id != null;
  }

  protected abstract prepareFieldOptions(): Record<string, any>;

  public get buttonOptions(): Record<string, any> {
    if (!this._buttonOptions) {
      this._buttonOptions = {
        cancel: {
          text: 'Cancelar',
          onClick: this.back,
        },
        save: {
          text: 'Salvar',
          type: 'success',
          useSubmitBehavior: true,
          disabled: this.saveSub && !this.saveSub.closed,
        },
      };
    }
    return this._buttonOptions;
  }

  protected load(resource: new () => T) {
    this.id = this._activatedRoute.snapshot.params[this.ROUTE_ID];
    if (this.id) {
      this.edit();
    } else {
      this.resource = new resource();
    }
  }

  protected edit() {
    this.service
      .findEditById(this.id)
      .pipe(take(1))
      .subscribe(resource => (this.resource = resource));
  }

  public back = () => {
    this._router.navigate([this.ROUTE_PATH]);
  };

  save(event) {
    event.preventDefault();
    this.saveSub = this.service
      .save(this.resource)
      .pipe(take(1))
      .subscribe(() => {
        notify(
          `${this.id ? 'Edição' : 'Criação'} realizada com sucesso.`,
          'success',
          3000
        );
        this.back();
      });
  }
}
