import {HttpParams} from "@angular/common/http";
import {LoadOptions} from "devextreme/data/load_options";
import {toFilterable} from "../udf/udf-parser";
import {UdfContains} from "../udf/predicate/udf-contains";
import {UdfFilterable} from "../udf/udf-filterable";

/**
 * Classe Adapter para montagem do HttpParams a partir do LoadOptions
 */
export class HttpParamsAdapter {

    constructor(private options: LoadOptions) {
    }

    httpParams(): HttpParams {
        const
          size = this.options.take,
          page = this.options.skip ? this.options.skip / (size || 1) : 0,
          sort = this.options.sort || [];
        let params = new HttpParams();
        if (this.options.take && this.options.skip) params = params.append('page', `${page}`);
        if (this.options.skip) params = params.append('size', `${size}`);
        if (this.hasFilter(this.options)) params = params.append("q", this.prepareFilter(this.options))
        for (let i = 0; i < sort.length; i++) {
            params = params.append('sort', `${sort[i].selector},${sort[i].desc ? 'desc' : 'asc'}`);
        }
        return params;
    }

    protected hasFilter(options: LoadOptions) {
        return (options.searchExpr && options.searchOperation && options.searchValue) || options.filter
    }

    protected prepareFilter(options: LoadOptions): string {
        let udfFilterable = options.filter ? toFilterable(options.filter) : new UdfFilterable();
        if (options.searchExpr && options.searchOperation && options.searchValue) {
            if (options.searchOperation === 'contains') {
                let searchPredicate = new UdfContains(options.searchExpr as string, options.searchValue);
                udfFilterable.predicates.push(searchPredicate);
            }
        }
        return udfFilterable.toQueryParam();
    }
}
