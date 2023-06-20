import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ConverterService} from "../../service/converter.service";
import {RequestDto} from "../dto/requestDto";
import {Currency} from "../../shared/currencies";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  currenciesMap: Map<string, string>;
  flag_1?: string;
  flag_2?: string;
  currencies: string[];
  currency_1: string;
  currency_2: string;
  toConvertCurrency: string;
  convertedCurrency: string;
  exchangeRate: string = "";
  inputFlag: boolean;
  pattern: string = "^\\d{1,9}([\\.\\,]\\d{1,2})?$";

  value_1_control = new FormControl('',
    [Validators.required, Validators.pattern(this.pattern)]);
  value_2_control = new FormControl('',
    [Validators.required, Validators.pattern(this.pattern)]);
  option_1_control = new FormControl('', [Validators.required]);
  option_2_control = new FormControl('', [Validators.required]);

  form_1_group: FormGroup = new FormGroup({
    value: this.value_1_control,
    option: this.option_1_control
  });

  form_2_group: FormGroup = new FormGroup({
    value: this.value_2_control,
    option: this.option_2_control
  });

  constructor(private converterService: ConverterService, private currency: Currency) {
  }

  ngOnInit(): void {
    this.currenciesMap = this.currency.getCurrencies();
    this.currencies = Array.from(this.currenciesMap.keys());
    this.currency_1 = this.currencies[0];
    this.currency_2 = this.currencies[1];
    this.setViewHelpers();
  }

  setView(): void {
    this.currency_1 = this.option_1_control.value ?? "";
    this.currency_2 = this.option_2_control.value ?? "";
    this.setViewHelpers();
  }

  setViewHelpers(): void{
    this.option_1_control.setValue(this.currency_1);
    this.option_2_control.setValue(this.currency_2);
    this.flag_1 = this.currenciesMap.get(this.currency_1);
    this.flag_2 = this.currenciesMap.get(this.currency_2);
  }

  convert(state: boolean): void {
    if ((this.inputFlag && state) || (this.inputFlag && !state)){
      this.convert_1_value();
    }else if ((!this.inputFlag && state) || (!this.inputFlag && !state)){
      this.convert_2_value();
    }
  }

  convert_1_value() {
    this.inputFlag = true;
    this.currency_1 = this.option_1_control.value ?? "";
    this.setView();

    if (this.form_1_group.invalid || this.option_2_control.invalid) {
      return;
    }

    let value =  this.value_1_control.value ?? "0";
    value = this.stringValidation(value);
    this.value_1_control.setValue(value);

    const data: RequestDto = this.dataPreparation(this.currency_1, this.currency_2, value);

    this.converterService.convertSingleValue(data).pipe().subscribe({
      next: response => {
        this.value_2_control.setValue(response.result);
        this.toConvertCurrency = this.currency_1;
        this.convertedCurrency = this.currency_2;
        this.exchangeRate = response.exchangeRate;
      },
      error: () => console.log("something gone wrong")
    });
  }

  convert_2_value() {
    this.inputFlag = false;
    this.currency_2 = this.option_2_control.value ?? "";
    this.setView();

    if (this.form_2_group.invalid || this.option_1_control.invalid) {
      return;
    }

    let value =  this.value_2_control.value ?? "0";
    value = this.stringValidation(value);
    this.value_2_control.setValue(value);

    const data: RequestDto = this.dataPreparation(this.currency_2, this.currency_1, value);

    this.converterService.convertSingleValue(data).pipe().subscribe({
      next: response => {
        this.value_1_control.setValue(response.result)
        this.toConvertCurrency = this.currency_2;
        this.convertedCurrency = this.currency_1;
        this.exchangeRate = response.exchangeRate;
      },
      error: () => console.log("something gone wrong")
    });
  }

  dataPreparation(input: string, output: string, value: string): RequestDto{
    return new RequestDto(input, output, this.stringValidation(value));
  }

  stringValidation(input: string): string{
    return input.replace(/^0+/, '').replace(/,/,'.');
  }
}
