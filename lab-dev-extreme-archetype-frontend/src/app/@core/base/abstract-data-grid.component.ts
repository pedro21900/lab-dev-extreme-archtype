import {Router} from '@angular/router';
import {DxDataGridComponent} from 'devextreme-angular';
import {confirm} from 'devextreme/ui/dialog';
import {take} from 'rxjs/operators';
import DataSource from 'devextreme/data/data_source';
import notify from 'devextreme/ui/notify';
import {AbstractCrudService} from './abstract-crud.service';

/**
 * Classe base para componentes de listagem.
 */
export abstract class AbstractDataGridComponent<T, ID> {

  protected abstract readonly dataGrid: DxDataGridComponent;

  protected abstract readonly ROUTE_PATH: string;

  public abstract readonly RESOURCE_NAME: string;

  /**
   * Instância do dataSource do DataGrid.
   */
  dataGridDS: DataSource;

  /**
   * Variável com o numero de recursos exibido no DataGrid.
   */
  totalResources: number = 0;

  /**
   * Referência do button Limpar Filtro passada via método onInitialized() do onToolbarPreparing()
   *
   * @protected
   */
  protected clearFilterButton: any;

  protected constructor(
    private service: AbstractCrudService<T, ID>,
    private _router: Router
  ) {
    this.dataGridDS = this.createDataGridDS();
    this.dataGridDS.on({
      'changed': () => {
        this.totalResources = this.dataGridDS.totalCount();
      }
    })
  }

  protected abstract createDataGridDS(): DataSource;

  create() {
    this._router.navigate([this.ROUTE_PATH, 'edit']);
  }

  detail(id: ID) {
    this._router.navigate([this.ROUTE_PATH, id]);
  }

  edit(id: ID) {
    this._router.navigate([this.ROUTE_PATH, 'edit', id]);
  }

  confirmDelete(resource: T) {
    confirm(
      `Deseja mesmo excluir item?`,
      'Confirmação de exclusão'
    ).then(result => {
      if (result) {
        this.delete(resource[this.service.RESOURCE_ID]);
      }
    });
  }

  onContentReady() {
    this.clearFilterButton.option('visible', this.hasFilter());
  }

  protected delete(id: ID) {
    this.service
      .delete(id)
      .pipe(take(1))
      .subscribe(() => {
        notify(
          `Exclusão realizada com sucesso.`,
          'success',
          3000
        );
        this.dataGrid.instance.refresh();
      });
  }

  protected hasFilter(): boolean {
    return (
      this.dataGridDS.isLoaded() &&
      this.dataGrid.instance.getCombinedFilter() &&
      this.dataGrid.instance.getCombinedFilter().length > 0
    );
  }

  protected clearFilterDataGrid() {
    this.dataGrid.instance.clearFilter('header');
    this.dataGrid.instance.clearFilter('row');
    this.dataGrid.instance.clearFilter('search');
  }
}
