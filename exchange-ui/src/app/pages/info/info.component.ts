import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ConverterService} from "../../service/converter.service";
import {InfoResponseDto} from "../dto/infoResponseDto";
import {InfoRequestDto} from "../dto/infoRequestDto";

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent implements OnChanges {

  @Input() currency_1: string = '';
  @Input() currency_2: string = '';

  isLoaded: boolean = false;
  dataSet: Array<InfoResponseDto> = new Array<InfoResponseDto>();

  constructor(private service: ConverterService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    let currencies: string[];
    if (this.currency_1 == this.currency_2){
      currencies = [this.currency_1];
    }else {
      currencies = [this.currency_1, this.currency_2];
    }
    this.dataSet.length = 0;
    for (const currency of currencies) {
      const infoRequest_1: InfoRequestDto = {code: currency};
      this.service.getCurrencyInfo(infoRequest_1).pipe().subscribe({
        next: response => {
          this.dataSet.push(response);
        },
        complete: () => this.isLoaded = true,
        error: () => console.log("something gone wrong")
      });
      this.isLoaded = false;
    }
  }
}
