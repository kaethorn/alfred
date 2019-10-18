import { AsyncSubject } from 'rxjs';

export interface Store {
  name: string;
  options?: object;
  indices?: [string, string, object?][];
}

/**
 * Convenience wrapper around indexedDB.
 */
export class IndexedDb {

  private db: IDBDatabase;
  ready: AsyncSubject<void> = new AsyncSubject<void>();

  constructor (name: string, version: number, stores: Store[]) {
    this.open(name, version, stores);
  }

  private open (name: string, version: number, stores: Store[]): Promise<void> {
    return new Promise((resolve, reject) => {
      const request: IDBOpenDBRequest = window.indexedDB.open(name, version);
      request.onerror = (event) => {
        console.log(`Error opening DB '${ name }': ${ event }.`);
        reject();
        this.ready.thrownError();
      };
      request.onsuccess = () => {
        this.db = request.result;
        resolve();
        this.ready.complete();
      };
      request.onupgradeneeded = (event: any) => {
        const db: IDBDatabase = event.target.result;
        stores.forEach((store) => {
          const objectStore = db.createObjectStore(store.name, store.options);
          if (store.indices) {
            store.indices.forEach((index) => {
              objectStore.createIndex(...index);
            });
          }
        });
      };
    });
  }

  hasKey (storeName: string, key: IDBValidKey): Promise<boolean> {
    return new Promise((resolve) => {
      if (!this.db) {
        return resolve(false);
      }
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readonly');
      transaction.onerror = () => resolve(false);
      const store = transaction.objectStore(storeName).getKey(key);
      store.onerror = () => resolve(false);
      store.onsuccess = (event: any) => {
        resolve(event.target.result === key);
      };
    });
  }

  get (storeName: string, key: IDBValidKey): Promise<any> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readonly');
      transaction.onerror = () => reject();
      const store = transaction.objectStore(storeName).get(key);
      store.onerror = () => reject();
      store.onsuccess = (event: any) => {
        if (event.target.result) {
          resolve(event.target.result);
        } else {
          reject();
        }
      };
    });
  }

  getAll (storeName: string): Promise<any> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readonly');
      transaction.onerror = () => reject();
      const request: IDBRequest = transaction.objectStore(storeName).getAll();
      request.onerror = () => reject();
      request.onsuccess = (event: any) => resolve(event.target.result);
    });
  }

  getAllBy (storeName: string, key: string, value: any): Promise<any> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readonly');
      transaction.onerror = () => resolve([]);
      const index: IDBIndex = transaction.objectStore(storeName).index(key);
      const request: IDBRequest = index.getAll(value);
      request.onerror = () => resolve([]);
      request.onsuccess = (event: any) => resolve(event.target.result);
    });
  }

  save (storeName: string, item: any, key?: IDBValidKey): Promise<Event> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readwrite');
      transaction.oncomplete = resolve;
      transaction.onerror = (error) => reject(error);
      const store = transaction.objectStore(storeName).put(item, key);
      store.onerror = (error) => reject(error);
    });
  }

  delete (storeName: string, key: string): Promise<Event> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readwrite');
      transaction.oncomplete = resolve;
      transaction.onerror = (error) => reject(error);
      const store = transaction.objectStore(storeName).delete(key);
      store.onerror = (error) => reject(error);
    });
  }
}
