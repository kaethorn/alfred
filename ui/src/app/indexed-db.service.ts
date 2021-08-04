import { Injectable } from '@angular/core';
import { AsyncSubject } from 'rxjs';

export interface Store {
  name: string;
  options?: object;
  indices?: [string, string, object?][];
}

/**
 * Convenience wrapper around indexedDB.
 */
@Injectable({
  providedIn: 'root'
})
export class IndexedDbService {

  public ready: AsyncSubject<void> = new AsyncSubject<void>();
  private db: IDBDatabase | null = null;

  public hasKey(storeName: string, key: IDBValidKey): Promise<boolean> {
    return new Promise(resolve => {
      if (!this.db) {
        return resolve(false);
      }
      const transaction: IDBTransaction = this.db.transaction([ storeName ], 'readonly');
      transaction.onerror = (): void => resolve(false);
      transaction.onabort = (): void => resolve(false);
      const request = transaction.objectStore(storeName).getKey(key);
      request.onerror = (): void => resolve(false);
      request.onsuccess = (event: any): void => {
        resolve(event.target.result === key);
      };
    });
  }

  public get(storeName: string, key: IDBValidKey): Promise<any> {
    return new Promise((resolve, reject) => {
      if (!this.db) {
        return reject();
      }
      const transaction: IDBTransaction = this.db.transaction([ storeName ], 'readonly');
      transaction.onerror = (): void => reject();
      transaction.onabort = (error): void => reject(error);
      const request = transaction.objectStore(storeName).get(key);
      request.onerror = (): void => reject();
      request.onsuccess = (event: any): void => {
        if (event.target.result) {
          resolve(event.target.result);
        } else {
          reject();
        }
      };
    });
  }

  public getAll(storeName: string): Promise<any> {
    return new Promise((resolve, reject) => {
      if (!this.db) {
        return reject();
      }
      const transaction: IDBTransaction = this.db.transaction([ storeName ], 'readonly');
      transaction.onerror = (): void => reject();
      transaction.onabort = (error): void => reject(error);
      const request: IDBRequest = transaction.objectStore(storeName).getAll();
      request.onerror = (): void => reject();
      request.onsuccess = (event: any): void => resolve(event.target.result);
    });
  }

  public getAllBy(storeName: string, key: string, value: any): Promise<any> {
    return new Promise((resolve, reject) => {
      if (!this.db) {
        return reject();
      }
      const transaction: IDBTransaction = this.db.transaction([ storeName ], 'readonly');
      transaction.onerror = (): void => resolve([]);
      transaction.onabort = (error): void => reject(error);
      const index: IDBIndex = transaction.objectStore(storeName).index(key);
      const request: IDBRequest = index.getAll(value);
      request.onerror = (): void => resolve([]);
      request.onsuccess = (event: any): void => resolve(event.target.result);
    });
  }

  public save(storeName: string, item: any, key?: IDBValidKey): Promise<Event> {
    return new Promise((resolve, reject) => {
      if (!this.db) {
        return reject();
      }
      const transaction: IDBTransaction = this.db.transaction([ storeName ], 'readwrite');
      transaction.oncomplete = resolve;
      transaction.onabort = (error): void => reject(error);
      transaction.onerror = (error): void => reject(error);
      const request = transaction.objectStore(storeName).put(item, key);
      request.onerror = (error): void => reject(error);
    });
  }

  public delete(storeName: string, key: string): Promise<Event> {
    return new Promise((resolve, reject) => {
      if (!this.db) {
        return reject();
      }
      const transaction: IDBTransaction = this.db.transaction([ storeName ], 'readwrite');
      transaction.oncomplete = resolve;
      transaction.onerror = (error): void => reject(error);
      transaction.onabort = (error): void => reject(error);
      const request = transaction.objectStore(storeName).delete(key);
      request.onerror = (error): void => reject(error);
    });
  }

  public open(name: string, version: number, stores: Store[], indexedDb: IDBFactory = window.indexedDB): void {
    const request: IDBOpenDBRequest = indexedDb.open(name, version);
    request.onerror = (event): void => {
      const error = `Error opening DB '${ name }'.`;
      console.error(error, event);
      this.ready.error(error);
    };
    request.onsuccess = (): void => {
      this.db = request.result;
      this.ready.complete();
    };
    request.onupgradeneeded = (event: any): void => {
      const db: IDBDatabase = event.target.result;
      stores.forEach(store => {
        const objectStore = db.createObjectStore(store.name, store.options);
        if (store.indices) {
          store.indices.forEach(index => {
            objectStore.createIndex(...index);
          });
        }
      });
    };
  }
}
