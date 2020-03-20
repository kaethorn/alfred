// FIXME inline this
const storage = {};

class ObjectStore implements IDBObjectStore {

  public indexNames: DOMStringList;
  public autoIncrement: boolean;
  public keyPath: string;
  public name: string;
  public transaction: IDBTransaction;

  constructor(
    name: string
  ) {
    this.name = name;
  }

  public getKey(query: any): IDBRequest {
    if (storage[this.transaction.db.name][this.name][query]) {
      return this.request(query);
    } else {
      return this.request(null);
    }
  }

  public get(query: any): IDBRequest {
    return this.request(storage[this.transaction.db.name][this.name][query]);
  }

  public getAll(): IDBRequest {
    return this.request(null);
  }

  public index(): IDBIndex {
    return this.generateIndex();
  }

  public delete(): IDBRequest {
    return this.request(null);
  }

  public put(item: any, key: any): IDBRequest {
    if (!key) {
      key = item.id;
    }
    storage[this.transaction.db.name][this.name][key] = item;
    setTimeout(() => {
      if (this.transaction.oncomplete) {
        this.transaction.oncomplete(new Event(''));
      }
    }, 20);
    return this.request(null);
  }

  public createIndex(): IDBIndex {
    return this.generateIndex();
  }

  public getAllKeys(): any {
  }

  public clear(): any {
  }

  public add(value: any, key?: any): any {
    storage[this.name][key] = value;
  }

  public count(): any {
    return 0;
  }

  public deleteIndex(): void {
  }

  public openCursor(): any {
  }

  public openKeyCursor(): any {
  }

  private generateIndex(): IDBIndex {
    return {
      keyPath: null,
      multiEntry: false,
      name: null,
      objectStore: null,
      unique: null,
      count: null,
      get: null,
      getAll: null,
      getAllKeys: null,
      getKey: null,
      openCursor: null,
      openKeyCursor: null
    };
  }

  private request(result: any): IDBRequest {
    const request = {
      result: null,
      addEventListener: null,
      removeEventListener: null,
      dispatchEvent: null,
      error: null,
      onerror: null,
      onsuccess: null,
      readyState: null,
      source: null,
      transaction: null
    };

    setTimeout(() => {
      if (request.onsuccess) {
        request.result = result;
        request.onsuccess({ target: { result } });
      }
    }, 100);

    return request;
  }
}

class SimpleDOMStringList implements DOMStringList {

  [index: number]: string;

  public length = 0;

  constructor(strings: string[]) {
    strings.forEach((entry, index) => {
      this[index] = entry;
      this.length += 1;
    });
  }

  public contains(string: string): boolean {
    return !!this[string];
  }

  public item(index: number): string | null {
    return this[index];
  }
}

class Transaction implements IDBTransaction {

  public objectStoreNames: DOMStringList;
  public db: IDBDatabase;
  public error: DOMException;
  public mode: IDBTransactionMode;
  public onabort: () => {};
  public oncomplete: () => {};
  public onerror: () => {};

  constructor(db: IDBDatabase, storeNames: string[]) {
    this.db = db;
    this.objectStoreNames = new SimpleDOMStringList(storeNames);
  }

  public objectStore(storeName: string): IDBObjectStore {
    storage[this.db.name][storeName].transaction = this;
    return storage[this.db.name][storeName];
  }

  public abort(): void {
  }

  public addEventListener(): void {
  }

  public removeEventListener(): void {
  }

  public dispatchEvent(): boolean {
    return true;
  }
}

class Database implements IDBDatabase {

  public name: string;
  public objectStoreNames: DOMStringList;
  public version: number;

  public onabort: () => {};
  public onclose: () => {};
  public onerror: () => {};
  public onversionchange: () => {};

  constructor(name: string, version: number) {
    this.name = name;
    this.version = version;
  }

  public createObjectStore(storeName: string): IDBObjectStore {
    storage[this.name][storeName] = new ObjectStore(this.name);
    return storage[this.name][storeName];
  }

  public transaction(storeNames: string[]): IDBTransaction {
    return new Transaction(this, storeNames);
  }

  public close(): void {
  }

  public deleteObjectStore(): void {
  }

  public addEventListener(): void {
  }

  public removeEventListener(): void {
  }

  public dispatchEvent(): boolean {
    return true;
  }
}

class VersionChangeEvent implements IDBVersionChangeEvent {

  public target: any;
  public newVersion: number;
  public oldVersion: number;
  public bubbles: boolean;
  public cancelBubble: boolean;
  public cancelable: boolean;
  public composed: boolean;
  public currentTarget: null;
  public defaultPrevented: boolean;
  public eventPhase: number;
  public isTrusted: boolean;
  public returnValue: boolean;
  public srcElement: null;
  public timeStamp: number;
  public type: string;
  public AT_TARGET: number;
  public BUBBLING_PHASE: number;
  public CAPTURING_PHASE: number;
  public NONE: number;

  constructor(
    target: any,
    newVersion: number,
    oldVersion: number
  ) {
    this.target = target;
    this.newVersion = newVersion;
    this.oldVersion = oldVersion;
  }

  public composedPath(): any {}
  public initEvent(): any {}
  public preventDefault(): any {}
  public stopImmediatePropagation(): any {}
  public stopPropagation(): any {}
}

export class IndexedDbMocks {

  public static get create(): IDBFactory {
    return {
      open: (name: string, version?: number): IDBOpenDBRequest => {
        storage[name] = {};
        storage[name].$version = version;
        return this.openRequest(name, version);
      },
      cmp: null,
      deleteDatabase: null
    };
  }

  public static reset(): void {
    for (const prop of Object.getOwnPropertyNames(storage)) {
      delete storage[prop];
    }
  }

  private static openRequest(name: string, version: number): IDBOpenDBRequest {
    const request: IDBOpenDBRequest = {
      result: null,
      onblocked: null,
      onupgradeneeded: null,
      addEventListener: null,
      removeEventListener: null,
      error: null,
      onerror: null,
      onsuccess: null,
      readyState: null,
      source: null,
      transaction: null,
      dispatchEvent: null
    };

    setTimeout(() => {
      (request as any).result = new Database(name, version);
      if (request.onupgradeneeded) {
        request.onupgradeneeded(new VersionChangeEvent({ result: request.result }, version, -1));
      }
      if (request.onsuccess) {
        request.onsuccess(new Event(''));
      }
    }, 10);

    return request;
  }
}
