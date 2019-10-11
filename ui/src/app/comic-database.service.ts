import { Injectable } from '@angular/core';

import { Comic } from './comic';
import { ComicsService } from './comics.service';
import { IndexedDb } from './indexed-db';

@Injectable({
  providedIn: 'root'
})
export class ComicDatabaseService {

  private db: IndexedDb;

  constructor (
    private comicService: ComicsService
  ) {
    this.db = new IndexedDb('Comics', 1, [{
      name: 'Images',
      options: { autoIncrement: true }
    }, {
      name: 'Comics',
      options: { keyPath: 'id' },
      indices: [[ 'id', 'id', { unique: true }]]
    }]);
  }

  store (comic: Comic): Promise<Event> {
    return Array.from(Array(comic.pageCount)).reduce((result, value, page) => {
      return result.then(() => this.saveImage(comic, page));
    }, Promise.resolve()).then(() => this.db.save('Comics', comic));
  }

  isStored (comicId: string): Promise<boolean> {
    return this.db.hasKey('Comics', comicId);
  }

  delete (comic: Comic): Promise<Event> {
    return Array.from(Array(comic.pageCount)).reduce((result, value, page) => {
      return result.then(() => this.db.delete('Images', `${ comic.id }/${ page }`));
    }, Promise.resolve()).then(() => this.db.delete('Comics', comic.id));
  }

  getImageUrl (comic: Comic, page: number): Promise<string> {
    return this.db.get('Images', `${ comic.id }/${ page }`).then((data: any) => {
      return URL.createObjectURL(data);
    });
  }

  getComic (comicId: string): Promise<Comic> {
    return this.db.get('Comics', comicId).then((data: any) => {
      return data as Comic;
    });
  }

  private saveImage (comic: Comic, page: number): Promise<Event> {
    return new Promise((resolve, reject) => {
      this.comicService.getPage(comic, page).subscribe((image: Blob) => {
        this.db.save('Images', image, `${ comic.id }/${ page }`)
          .then(resolve)
          .catch(error => reject(error));
      });
    });
  }
}
