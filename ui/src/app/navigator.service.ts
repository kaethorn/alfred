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

export interface PageSource {
  src?: string;
  page: number;
  loaded: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NavigatorService {
  static page = 0;
  static offset = 1;
  static sideBySide: boolean;
  static pageCount: number;

  constructor () {}

  private partition (): PageSource[][] {
    const setCount = Math.round(NavigatorService.sideBySide ?
      ((NavigatorService.pageCount ? NavigatorService.pageCount : -1) + 1) / 2 : NavigatorService.pageCount);

    return Array.from(Array(setCount).keys())
      .map(set => NavigatorService.sideBySide ?
        set === 0 ? [set] : (2 * set < NavigatorService.pageCount) ? [2 * set - 1, 2 * set] : [2 * set - 1] :
        [set])
      .map(set => set.map(index => ({ page: index, loaded: false })));
  }

  set (pageCount: number, currentPage: number, sideBySide: boolean): PageSource[][] {
    NavigatorService.page = currentPage;
    NavigatorService.pageCount = pageCount;
    NavigatorService.sideBySide = sideBySide;
    return this.partition();
  }

  /**
   * Returns navigation instructions.
   */
  go (offset: number = 0): NavigationInstruction {
    let direction: AdjacentComic = AdjacentComic.same;
    NavigatorService.offset = offset;

    if (offset < 0) {
      if (NavigatorService.page > 0) {
        NavigatorService.page -= 1;
      } else {
        direction = AdjacentComic.previous;
      }
      NavigatorService.page -= (NavigatorService.sideBySide && NavigatorService.page > 0) ? 1 : 0;
    } else if (offset > 0) {
      const increment = (NavigatorService.sideBySide && NavigatorService.page < NavigatorService.pageCount - 2) ? 2 : 1;
      if ((NavigatorService.page + increment) < NavigatorService.pageCount) {
        NavigatorService.page += increment;
      } else {
        direction = AdjacentComic.next;
      }
    }

    return {
      sideBySide: NavigatorService.sideBySide && NavigatorService.page > 0 && NavigatorService.page < (NavigatorService.pageCount - 1),
      adjacent: direction
    };
  }

  getSet (): number {
    return NavigatorService.sideBySide ?
      Math.round(NavigatorService.page / 2) : NavigatorService.page;
  }
}
