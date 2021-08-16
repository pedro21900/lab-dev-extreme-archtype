import { UdfPredicate } from './udf-predicate';

export class UdfLess extends UdfPredicate {
  constructor(dataField: string, value: number | Date | '@now' | string) {
    super(dataField, '<', value);
  }

  toQueryParam(): string {
    return `${this.dataField}<${this.valueToString()}`;
  }

  private valueToString(): string {
    if (this.value instanceof Date) {
      return this.value.toISOString().substr(0, 19);
    }
    return this.value.toString();
  }
}
