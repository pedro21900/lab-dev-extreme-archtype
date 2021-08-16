import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Page} from "../types";

/**
 * Classe base para os services de CRUD.
 */
export abstract class AbstractCrudService<T, ID> {
  abstract readonly API_PATH: string;
  abstract readonly RESOURCE_ID: string;

  protected constructor(private _http: HttpClient) {
  }

  public findAll(params: HttpParams): Observable<{ data: T[]; totalCount: number }> {
    return this._http.get<Page<T>>(this.API_PATH, {params})
      .pipe(
        map((page: Page<T>) => ({
          data: page.content,
          totalCount: page.totalElements
        })),
        catchError(error => {
          return throwError(new Error(error.message))
        })
      )
  }

  // public summarize(aggregables: UdfAggregable[], filter?: string): Observable<UdfAggregation[]> {
  //   const params: HttpParams = new HttpParams();
  //   if (filter) {
  //     params.set('q', filter);
  //   }
  //   return this._http.post<UdfAggregation[]>(`${this.API_PATH}/summarize`, aggregables, {params});
  // }

  public findById(id: ID): Observable<T> {
    return this._http.get<T>(`${this.API_PATH}/${id}`);
  }

  public findEditById(id: ID): Observable<T> {
    return this._http.get<T>(`${this.API_PATH}/edit/${id}`);
  }

  public save(resource: T): Observable<T> {
    return resource[this.RESOURCE_ID]
      ? this.update(resource[this.RESOURCE_ID], resource)
      : this.insert(resource);
  }

  public insert(resource: T): Observable<T> {
    return this._http.post<T>(this.API_PATH, resource);
  }

  public update(id: ID, resource: T): Observable<T> {
    return this._http.put<T>(`${this.API_PATH}/${id}`, resource);
  }

  public delete(id: ID): Observable<void> {
    return this._http.delete<void>(`${this.API_PATH}/${id}`);
  }
}
