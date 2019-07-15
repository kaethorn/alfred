import { Injectable } from '@angular/core';

export enum AdjacentComic {
  previous,
  next,
  same
}

export interface NavigationInstruction {
  sideBySide: boolean;
  adjacent: AdjacentComic;
}

@Injectable({
  providedIn: 'root'
})
export class NavigatorService {
  static page = 0;
  private pageCount: number;
  private sideBySide: boolean;

  constructor () {}

  set (pageCount: number, currentPage: number, sideBySide: boolean) {
    NavigatorService.page = currentPage;
    this.pageCount = pageCount;
    this.sideBySide = sideBySide;
  }

  /**
   * Returns navigation instructions.
   */
  go (offset: number = 0): NavigationInstruction {
    let direction: AdjacentComic = AdjacentComic.same;

    if (offset < 0) {
      if (NavigatorService.page > 0) {
        NavigatorService.page -= 1;
      } else {
        direction = AdjacentComic.previous;
      }
      NavigatorService.page -= (this.sideBySide && NavigatorService.page > 0) ? 1 : 0;
    } else if (offset > 0) {
      const increment = (this.sideBySide && NavigatorService.page > 0) ? 2 : 1;
      if ((NavigatorService.page + increment) < this.pageCount) {
        NavigatorService.page += increment;
      } else {
        direction = AdjacentComic.next;
      }
    }

    return {
      sideBySide: this.sideBySide && NavigatorService.page > 0 && NavigatorService.page < (this.pageCount - 1),
      adjacent: direction
    };
  }
}
