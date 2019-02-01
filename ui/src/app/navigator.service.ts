import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NavigatorService {
  static page = 0;
  private pageCount: number;
  private sideBySide: boolean;

  constructor() {}

  set (pageCount: number, currentPage: number, sideBySide: boolean) {
    NavigatorService.page = currentPage;
    this.pageCount = pageCount;
    this.sideBySide = sideBySide;
  }

  go (offset: number = 0): boolean {
    if (offset < 0) {
      NavigatorService.page -= NavigatorService.page > 0 ? 1 : 0;
      NavigatorService.page -= (this.sideBySide && NavigatorService.page > 0) ? 1 : 0;
    } else if (offset > 0) {
      const increment = (this.sideBySide && NavigatorService.page > 0) ? 2 : 1;
      NavigatorService.page += (NavigatorService.page + increment) < this.pageCount ? increment : 0;
    }

    return this.sideBySide && NavigatorService.page > 0 && NavigatorService.page < (this.pageCount - 1);
  }
}
