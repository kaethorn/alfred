import { Injectable } from '@angular/core';
import { AsyncSubject } from 'rxjs';

import { Comic } from './comic';
import { ComicsService } from './comics.service';
import { IndexedDb } from './indexed-db';

@Injectable({
  providedIn: 'root'
})
export class ComicDatabaseService {

  public ready: AsyncSubject<void> = new AsyncSubject<void>();
  private db: IndexedDb;

  constructor(
    private comicService: ComicsService
  ) {
    this.db = new IndexedDb('Comics', 1, [{
      name: 'Images',
      options: { autoIncrement: true }
    }, {
      name: 'Comics',
      options: { keyPath: 'id' },
      indices: [
        [ 'id', 'id', { unique: true }],
        [ 'dirty', 'dirty', { unique: false }]
      ]
    }]);
    this.db.ready.subscribe(
      () => {},
      () => this.ready.thrownError(),
      () => this.ready.complete());
  }

  /**
   * Caches the entire given comic book, including meta data and images.
   * @param comic The comic to cache.
   */
  public async store(comic: Comic): Promise<Event> {
    const isStored = await this.isStored(comic.id);
    if (!isStored) {
      return Array.from(Array(comic.pageCount)).reduce((result, value, page) =>
        result.then(() => this.saveImage(comic.id, page))
      , Promise.resolve()).then(() => this.db.save('Comics', comic));
    }
  }

  public isStored(comicId: string): Promise<boolean> {
    return this.db.hasKey('Comics', comicId);
  }

  /**
   * Deletes the comic and any stored images.
   * @param comic The comic to delete.
   */
  public delete(comic: Comic): Promise<Event> {
    return Array.from(Array(comic.pageCount)).reduce((result, value, page) =>
      result.then(() => this.db.delete('Images', `${ comic.id }/${ page }`))
    , Promise.resolve()).then(() => this.db.delete('Comics', comic.id));
  }

  public async deleteAll(): Promise<void> {
    const comics = await this.getComics();
    for (const comic of comics) {
      await this.delete(comic);
    }
  }

  public getImageUrl(comicId: string, page: number): Promise<string> {
    return this.db.get('Images', `${ comicId }/${ page }`).then((data: any) =>
      URL.createObjectURL(data)
    );
  }

  public async getComic(comicId: string): Promise<Comic> {
    await this.ready.toPromise();
    return this.db.get('Comics', comicId);
  }

  public async getComics(): Promise<Comic[]> {
    await this.ready.toPromise();
    return this.db.getAll('Comics');
  }

  public getComicsBy(key: string, value: any): Promise<Comic[]> {
    return this.db.getAllBy('Comics', key, value);
  }

  public save(comic: Comic): Promise<Event> {
    return this.db.save('Comics', comic);
  }

  private saveImage(comicId: string, page: number): Promise<Event> {
    return new Promise((resolve, reject) => {
      this.comicService.getPage(comicId, page).subscribe((image: Blob) => {
        this.db.save('Images', image, `${ comicId }/${ page }`)
          .then(resolve)
          .catch(error => reject(error));
      }, error => reject(error));
    });
  }
}
