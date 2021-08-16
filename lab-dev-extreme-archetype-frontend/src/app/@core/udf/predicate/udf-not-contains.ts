import { UdfPredicate } from './udf-predicate';

export class UdfNotContains extends UdfPredicate {
  constructor(dataField: string, value: string) {
    super(dataField, 'notcontains', value);
  }

  toQueryParam(): string {
    return `${this.dataField}!=${this.valueToString()}`;
  }

  private valueToString(): string {
    if (this.value.includes(' ')) {
      return `'*${this.value}*'`;
    }
    return `*${this.value}*`;
  }
}
