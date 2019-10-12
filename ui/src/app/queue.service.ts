import { Injectable } from '@angular/core';

import { Comic } from './comic';
import { ComicsService } from './comics.service';
import { Observable, from } from 'rxjs';
import { concatMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class QueueService {

  private queue: { [name: string]: Comic } = {};

  constructor (
    private comicsService: ComicsService,
  ) {
    this.load();
  }

  /**
   * Save each comic in the queue, removing it if successful.
   *
   * Returns a promise that either resolves when all items are
   * processed successfully or rejects on the first error.
   */
  process (): Observable<Comic> {
    return from(Object.keys(this.queue)).pipe(
      concatMap((comicId) => {
        const update  = this.comicsService.update(this.queue[comicId]);
        update.subscribe(() => {
          delete this.queue[comicId];
          this.save();
        }, () => {});
        return update;
      })
    );
  }

  private save () {
    // TODO use indexedDB:
    // * Comics should already be saved there.
    // * Save only comic IDs in local storage.
    localStorage.setItem('queue', JSON.stringify(this.queue));
  }

  add (comic: Comic) {
    this.queue[comic.id] = comic;
    this.save();
  }

  load () {
    this.queue = JSON.parse(localStorage.getItem('queue')) || {};
  }

  hasItems (): boolean {
    return this.count() > 0;
  }

  count (): number {
    return Object.keys(this.queue).length;
  }
}
