import {Injectable} from "@angular/core";
import {CurrencyEntity} from "./currencyEntity";

/*export const currencies: any[] = [
  {name: "PLN", flag: "fi-pl"},
  {name: "GBP", flag: "fi-gb"},
  {name: "EUR", flag: "fi-eu"},
  {name: "USD", flag: "fi-us"},
  {name: "THB", flag: "fi-th"},
  {name: "AUD", flag: "fi-au"},
  {name: "CAD", flag: "fi-ca"},
  {name: "HUF", flag: "fi-hu"},
  {name: "CHF", flag: "fi-ch"},
  {name: "JPY", flag: "fi-jp"},
  {name: "CLP", flag: "fi-cl"},
  {name: "INR", flag: "fi-in"}
]*/

const currencies: Array<CurrencyEntity> = Array.of(
  {code: "PLN", country: "Poland", flag: "fi-pl"},
  {code: "GBP", country: "Great Britain", flag: "fi-gb"},
  {code: "EUR", country: "Europe Union", flag: "fi-eu"},
  {code: "USD", country: "United States", flag: "fi-us"},
  {code: "THB", country: "Thailand", flag: "fi-th"},
  {code: "AUD", country: "Australia", flag: "fi-au"},
  {code: "CAD", country: "Canada", flag: "fi-ca"},
  {code: "HUF", country: "Hungary", flag: "fi-hu"},
  {code: "CHF", country: "Switzerland", flag: "fi-ch"},
  {code: "JPY", country: "Japan", flag: "fi-jp"},
  {code: "CLP", country: "Chile", flag: "fi-cl"},
  {code: "INR", country: "India", flag: "fi-in"}
);

@Injectable({
  providedIn: 'root'
})
export class Currency {

  getCurrencyInfo(): CurrencyEntity[]{
    return currencies;
  }

  getInitialCurrency(index: number): string {
    return currencies.at(index)?.code ?? "";
  }

  getFlag(code: string): string {
    const currency = currencies.find(currency => currency.code == code);
    return currency?.flag ?? "";
  }

  getCountry(code: string): string {
    const currency = currencies.find(currency => currency.code == code);
    return currency?.country ?? "";
  }

}
