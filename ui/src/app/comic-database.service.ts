import { Injectable } from '@angular/core';

import { Comic } from './comic';
import { ComicsService } from './comics.service';

@Injectable({
  providedIn: 'root'
})
export class ComicDatabaseService {

  db: IDBDatabase;

  constructor (
    private comicService: ComicsService
  ) {
    this.open();
  }

  private open () {
    const request: any = indexedDB.open('Comics', 1);
    request.onerror = (event) => {
      console.log(`Error opening DB 'Comics': ${ event }`);
    };
    request.onsuccess = () => {
      this.db = request.result;
    };
    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      db.createObjectStore('Images', { autoIncrement: true });
      const comicStore: IDBObjectStore = db.createObjectStore('Comics', { keyPath: 'id' });
      comicStore.createIndex('id', 'id', { unique: true });
    };
  }

  store (comic: Comic): Promise<Event> {
    return Array.from(Array(comic.pageCount)).reduce((result, value, page) => {
      return result.then(() => this.saveImage(comic, page));
    }, Promise.resolve()).then(() => this.saveComic(comic));
  }

  isStored (comic: Comic): Promise<boolean> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction(['Comics'], 'readonly');
      transaction.oncomplete = () => resolve(true);
      transaction.onerror = () => resolve(false);
      const store = transaction.objectStore('Comics').getKey(comic.id);
      store.onerror = () => resolve(false);
      store.onsuccess = (event: any) => {
        const result = event.target.result;
        resolve(result === comic.id);
      };
    });
  }

  delete (comic: Comic): Promise<Event> {
    return Array.from(Array(comic.pageCount)).reduce((result, value, page) => {
      return result.then(() => this.removeImage(comic, page));
    }, Promise.resolve()).then(() => this.removeComic(comic));
  }

  getPageLink (): string {
    return 'TODO';
  }

  private removeComic (comic: Comic) {
    return 'TODO';
  }

  private removeImage (comic: Comic, page: number) {
    return 'TODO';
  }

  private saveComic (comic: Comic): Promise<Event> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction(['Comics'], 'readwrite');
      transaction.oncomplete = resolve;
      transaction.onerror = (error) => reject(error);
      const store = transaction.objectStore('Comics').put(comic);
      store.onerror = (error) => reject(error);
    });
  }

  private saveImage (comic: Comic, page: number): Promise<Event> {
    return new Promise((resolve, reject) => {
      this.comicService.getPage(comic, page).subscribe((image: Blob) => {
        const transaction: IDBTransaction = this.db.transaction(['Images'], 'readwrite');
        transaction.oncomplete = resolve;
        transaction.onerror = (error) => reject(error);
        const store = transaction.objectStore('Images')
          .put(image, `${ comic.id }/${ page }`);
        store.onerror = (error) => reject(error);
      });
    });
  }
}
