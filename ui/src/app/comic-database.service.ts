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
      const store: IDBObjectStore = db.createObjectStore('Images', { keyPath: 'id', autoIncrement: true });
      // store.createIndex('comicId', 'comicId', { unique: false });
      // store.createIndex('page', 'page', { unique: false });
    };
  }

  isStored (comic: Comic): boolean {
    return true;
  }

  storeComic (comic: Comic) {
    // TODO also save comic metadata
    Array.from(Array(comic.pageCount)).forEach((value, page) => {
      this.saveImage(comic, page);
    });
  }

  getPageLink (): string {
    return 'TODO';
  }

  private saveImage (comic: Comic, page: number) {
    this.comicService.getPage(comic, page).subscribe((image: Blob) => {
      const transaction: IDBTransaction = this.db.transaction(['Images'], 'readwrite');
      transaction.oncomplete = () => {
        console.log(`Successfully saved page ${ page } of ${ comic.path }.`);
      };
      transaction.onerror = (error) => {
        console.error(`Error saving page ${ page } of ${ comic.path }:`);
        console.error(error);
      };
      const store = transaction.objectStore('Images').put(image);
      store.onerror = (error) => {
        console.error(`Error storing page ${ page } of ${ comic.path }:`);
        console.error(error);
      };
    });
  }
}
