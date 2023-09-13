import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {RequestDto} from "../pages/dto/requestDto";
import {ResponseDto} from "../pages/dto/responseDto";
import {InfoResponseDto} from "../pages/dto/infoResponseDto";
import {InfoRequestDto} from "../pages/dto/infoRequestDto";

@Injectable({
  providedIn: 'root'
})
export class ConverterService {

  constructor(private http: HttpClient) { }

  convertSingleValue(data: RequestDto): Observable<ResponseDto>{
    return this.http.post<ResponseDto>("/api/convert", data);
  }

  getCurrencyInfo(code: InfoRequestDto): Observable<InfoResponseDto>{
    return this.http.post<InfoResponseDto>("/api/info", code)
  }
}
