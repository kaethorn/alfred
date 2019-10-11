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

  constructor (name: string, version: number, stores: Store[]) {
    this.open(name, version, stores);
  }

  private open (name: string, version: number, stores: Store[]) {
    const request: any = indexedDB.open(name, version);
    request.onerror = (event) => {
      console.log(`Error opening DB '${ name }': ${ event }`);
    };
    request.onsuccess = () => {
      this.db = request.result;
    };
    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      stores.forEach((store) => {
        const objectStore = db.createObjectStore(store.name, store.options);
        if (store.indices) {
          store.indices.forEach((index) => {
            objectStore.createIndex(...index);
          });
        }
      });
    };
  }

  hasKey (storeName: string, key: IDBValidKey): Promise<boolean> {
    return new Promise((resolve) => {
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readonly');
      transaction.onerror = () => resolve(false);
      const store = transaction.objectStore(storeName).getKey(key);
      store.onerror = () => resolve(false);
      store.onsuccess = (event: any) => {
        resolve(event.target.result === key);
      };
    });
  }

  get (storeName: string, key: IDBValidKey): Promise<string> {
    return new Promise((resolve, reject) => {
      const transaction: IDBTransaction = this.db.transaction([storeName], 'readonly');
      transaction.onerror = () => reject();
      const store = transaction.objectStore(storeName).get(key);
      store.onerror = () => reject();
      store.onsuccess = (event: any) => resolve(event.target.result);
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
