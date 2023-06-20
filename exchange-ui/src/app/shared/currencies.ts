import {Injectable} from "@angular/core";

export const currencies: any[] = [
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
]

@Injectable({
  providedIn: 'root'
})
export class Currency {
  getCurrencies(): Map<string, string> {
    const map = new Map<string, string>();
    for (const currency of currencies) {
      map.set(currency.name, currency.flag);
    }
    return map;
  }
}
