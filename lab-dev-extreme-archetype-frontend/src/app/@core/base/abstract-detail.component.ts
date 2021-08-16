import {ActivatedRoute, Router} from '@angular/router';
import {confirm} from 'devextreme/ui/dialog';
import {take} from 'rxjs/operators';
import notify from 'devextreme/ui/notify';
import {AbstractCrudService} from "./abstract-crud.service";

/**
 * Classe base para componentes de detalhamento.
 */
export abstract class AbstractDetailComponent<T, ID> {
  id: ID;
  resource: T;

  protected abstract readonly ROUTE_PATH: string;

  protected abstract readonly ROUTE_ID: string;

  protected constructor(
    private _activatedRoute: ActivatedRoute,
    private service: AbstractCrudService<T, ID>,
    private _router: Router
  ) {
  }

  protected load() {
    this.id = this._activatedRoute.snapshot.params[this.ROUTE_ID];
    this.service
      .findById(this.id)
      .pipe(take(1))
      .subscribe(resource => (this.resource = resource));
  }

  back() {
    this._router.navigate([this.ROUTE_PATH]);
  }

  edit() {
    this._router.navigate([this.ROUTE_PATH, 'edit', this.id]);
  }

  confirmDelete() {
    confirm(
      `Deseja mesmo excluir item?`,
      'Confirmação de Exclusão'
    ).then(result => {
      if (result) {
        this.delete();
      }
    });
  }

  private delete() {
    this.service
      .delete(this.id)
      .pipe(take(1))
      .subscribe(() => {
        notify(
          `Exclusão realizada com sucesso.`,
          'success',
          3000
        );
        this.back();
      });
  }
}
