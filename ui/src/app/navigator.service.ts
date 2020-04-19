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
  loader?: Promise<void>;
}

@Injectable({
  providedIn: 'root'
})
export class NavigatorService {
  public static page = 0;
  public static offset = 1;
  public static sideBySide: boolean;
  public static pageCount: number;

  constructor() {}

  public set(pageCount: number, currentPage: number, sideBySide: boolean): PageSource[][] {
    NavigatorService.page = currentPage;
    NavigatorService.pageCount = pageCount;
    NavigatorService.sideBySide = sideBySide;
    return this.partition();
  }

  /**
   * Returns navigation instructions.
   */
  public go(offset = 0): NavigationInstruction {
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

    // Restore side by side view which always loads page `n` and `n + 1` except
    // for the cover.
    if (NavigatorService.sideBySide) {
      NavigatorService.page = NavigatorService.page + NavigatorService.page % 2;
      if (NavigatorService.page >= NavigatorService.pageCount - 1) {
        NavigatorService.page = NavigatorService.pageCount - 1;
      }
    }

    return {
      adjacent: direction,
      sideBySide: NavigatorService.sideBySide && NavigatorService.page > 0 && NavigatorService.page < (NavigatorService.pageCount - 1)
    };
  }

  public getSet(): number {
    return NavigatorService.sideBySide ?
      Math.round(NavigatorService.page / 2) : NavigatorService.page;
  }

  private partition(): PageSource[][] {
    const setCount = Math.round(NavigatorService.sideBySide ?
      ((NavigatorService.pageCount ? NavigatorService.pageCount : -1) + 1) / 2 : NavigatorService.pageCount);

    return Array.from(Array(setCount).keys())
      .map(set => NavigatorService.sideBySide ?
        set === 0 ? [ set ] : (2 * set < NavigatorService.pageCount) ? [ 2 * set - 1, 2 * set ] : [ 2 * set - 1 ] :
        [ set ])
      .map(set => set.map(index => ({ loaded: false, page: index })));
  }
}
