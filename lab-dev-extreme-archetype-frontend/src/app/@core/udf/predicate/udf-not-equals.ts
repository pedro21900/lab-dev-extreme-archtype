import { UdfPredicate } from './udf-predicate';

export class UdfNotEquals extends UdfPredicate {
  constructor(dataField: string, value: any) {
    super(dataField, '<>', value);
  }

  toQueryParam(): string {
    return `${this.dataField}!=${this.valueToString()}`;
  }

  private valueToString(): string {
    if (this.value instanceof Date) {
      return this.value.toISOString().substr(0, 19);
    }
    if (typeof this.value === 'string' && this.value.includes(' ')) {
      return `'${this.value}'`;
    }
    return this.value;
  }
}
