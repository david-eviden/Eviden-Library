import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'  //disponible a nivel global
})
export class SearchService {
  private urlEndPoint: string = 'http://localhost:8081/api/search'; 

  constructor(private http: HttpClient) {}

  search(query: string): Observable<any> {
    return this.http.get(`${this.urlEndPoint}?query=${encodeURIComponent(query)}`);
  }
}
