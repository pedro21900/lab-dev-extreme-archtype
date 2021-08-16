import { UdfFilterable } from '../udf-filterable';
import { UdfExpression } from './udf-expression';

export class UdfConjunction extends UdfFilterable {
  type = 'and';

  constructor(predicates?: UdfExpression[]) {
    super();
    if (predicates) {
      this.concatPredicates(predicates);
    }
  }
}
