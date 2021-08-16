import { UdfPredicate } from './udf-predicate';

export class UdfContains extends UdfPredicate {
  constructor(dataField: string, value: string) {
    super(dataField, 'contains', value);
  }

  toQueryParam(): string {
    return `${this.dataField}==${this.valueToString()}`;
  }

  private valueToString(): string {
    if (this.value.includes(' ')) {
      return `'*${this.value}*'`;
    }
    return `*${this.value}*`;
  }
}
