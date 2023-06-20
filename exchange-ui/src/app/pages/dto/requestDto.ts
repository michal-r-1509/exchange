export class RequestDto {
  inputCurrency: string;
  outputCurrency: string;
  value: string;

  constructor(inputCurrency: string, outputCurrency: string, value: string) {
    this.inputCurrency = inputCurrency;
    this.outputCurrency = outputCurrency;
    this.value = value;
  }
}
