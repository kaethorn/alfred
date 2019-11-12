import { Injectable } from '@angular/core';

import { Comic } from './comic';
import { ComicsService } from './comics.service';
import { Observable, from } from 'rxjs';
import { concatMap, flatMap } from 'rxjs/operators';
import { ComicDatabaseService } from './comic-database.service';

@Injectable({
  providedIn: 'root'
})
export class QueueService {

  constructor (
    private comicsService: ComicsService,
    private comicDatabaseService: ComicDatabaseService,
  ) {
    this.comicDatabaseService.ready.toPromise().then(() => this.process());
  }

  /**
   * Save each comic in the queue, removing it if successful.
   *
   * Returns a promise that either resolves when all items are
   * processed successfully or rejects on the first error.
   */
  process (): Observable<Comic> {
    return from(this.load()).pipe(
      flatMap(comics => from(comics)),
      concatMap((comic) => {
        delete comic.dirty;
        const update = this.comicsService.update(comic);
        update.subscribe(() => {
          this.comicDatabaseService.save(comic);
        }, () => {
          comic.dirty = 1;
          this.comicDatabaseService.save(comic);
        });
        return update;
      })
    );
  }

  private load (): Promise<Comic[]> {
    return this.comicDatabaseService.getComicsBy('dirty', 1);
  }

  add (comic: Comic): Promise<any> {
    comic.dirty = 1;
    return this.comicDatabaseService.save(comic);
  }

  async hasItems (): Promise<boolean> {
    const count = await this.count();
    return count > 0;
  }

  async count (): Promise<number> {
    const comics = await this.load();
    return comics.length;
  }
}
