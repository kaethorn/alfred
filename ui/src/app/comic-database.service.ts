import { Injectable } from '@angular/core';

import { Comic } from './comic';
import { ComicsService } from './comics.service';
import { IndexedDb } from './indexed-db';
import { AsyncSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ComicDatabaseService {

  private db: IndexedDb;
  ready: AsyncSubject<void> = new AsyncSubject<void>();

  constructor (
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
  async store (comic: Comic): Promise<Event> {
    const isStored = await this.isStored(comic.id);
    if (!isStored) {
      return Array.from(Array(comic.pageCount)).reduce((result, value, page) => {
        return result.then(() => this.saveImage(comic.id, page));
      }, Promise.resolve()).then(() => this.db.save('Comics', comic));
    }
  }

  isStored (comicId: string): Promise<boolean> {
    return this.db.hasKey('Comics', comicId);
  }

  /**
   * Deletes the comic and any stored images.
   * @param comic The comic to delete.
   */
  delete (comic: Comic): Promise<Event> {
    return Array.from(Array(comic.pageCount)).reduce((result, value, page) => {
      return result.then(() => this.db.delete('Images', `${ comic.id }/${ page }`));
    }, Promise.resolve()).then(() => this.db.delete('Comics', comic.id));
  }

  async deleteAll (): Promise<any> {
    const comics = await this.getComics();
    for (const comic of comics) {
      await this.delete(comic);
    }
  }

  getImageUrl (comicId: string, page: number): Promise<string> {
    return this.db.get('Images', `${ comicId }/${ page }`).then((data: any) => {
      return URL.createObjectURL(data);
    });
  }

  getComic (comicId: string): Promise<Comic> {
    return this.db.get('Comics', comicId);
  }

  getComics (): Promise<Comic[]> {
    return this.db.getAll('Comics');
  }

  getComicsBy (key: string, value: any): Promise<Comic[]> {
    return this.db.getAllBy('Comics', key, value);
  }

  save (comic: Comic): Promise<Event> {
    return this.db.save('Comics', comic);
  }

  private saveImage (comicId: string, page: number): Promise<Event> {
    return new Promise((resolve, reject) => {
      this.comicService.getPage(comicId, page).subscribe((image: Blob) => {
        this.db.save('Images', image, `${ comicId }/${ page }`)
          .then(resolve)
          .catch(error => reject(error));
      }, (error) => reject(error));
    });
  }
}
