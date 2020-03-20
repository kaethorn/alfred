const storage = {};
const flags: IndexedDbMockFlag[] = [];

export enum IndexedDbMockFlag {
  OPEN_ERROR,
  TRANSACTION_ERROR,
  TRANSACTION_ABORT,
  REQUEST_ERROR
}

class Request implements IDBRequest {

  public error: DOMException | null;
  public result: any;
  public addEventListener: null;
  public removeEventListener: null;
  public dispatchEvent: null;
  public onerror: (event: any) => {};
  public onsuccess: (event: any) => {};
  public readyState: null;
  public source: null;
  public transaction: null;

  constructor(result: any) {
    this.result = result;
    setTimeout(() => {
      if (!this.hasRequestErrors() && this.onsuccess) {
        this.onsuccess({ target: { result: this.result } });
      }
    }, 100);
  }

  private hasRequestErrors(): boolean {
    if (flags.includes(IndexedDbMockFlag.REQUEST_ERROR)) {
      if (this.onerror) {
        this.onerror(new Event('Could not process request.'));
      }
      return true;
    }
    return false;
  }
}

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
    if (!this.hasTransactionErrors() && storage[this.transaction.db.name][this.name][query]) {
      return new Request(query);
    }
    return new Request(null);
  }

  public get(query: any): IDBRequest {
    if (!this.hasTransactionErrors()) {
      return new Request(storage[this.transaction.db.name][this.name][query]);
    }
    return new Request(null);
  }

  public getAll(): IDBRequest {
    return new Request(null);
  }

  public index(): IDBIndex {
    return this.generateIndex();
  }

  public delete(): IDBRequest {
    return new Request(null);
  }

  public put(item: any, key: any = item.id): IDBRequest {
    storage[this.transaction.db.name][this.name][key] = item;

    setTimeout(() => {
      if (!this.hasTransactionErrors() && this.transaction.oncomplete) {
        this.transaction.oncomplete(new Event(''));
      }
    }, 20);
    return new Request(null);
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

  private hasTransactionErrors(): boolean {
    if (flags.includes(IndexedDbMockFlag.TRANSACTION_ERROR)) {
      if (this.transaction.onerror) {
        this.transaction.onerror(new Event('Could not execute transaction.'));
      }
      return true;
    } else if (flags.includes(IndexedDbMockFlag.TRANSACTION_ABORT)) {
      if (this.transaction.onabort) {
        this.transaction.onabort(new Event('Aborted transaction.'));
      }
      return true;
    }
    return false;
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

export class IndexedDbMock {

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
    flags.splice(0, flags.length);
  }

  public static setFlag(flag: IndexedDbMockFlag): void {
    flags.push(flag);
  }

  public static removeFlag(flag: IndexedDbMockFlag): void {
    flags.splice(flags.indexOf(flag), 1);
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
      if (flags.includes(IndexedDbMockFlag.OPEN_ERROR)) {
        if (request.onerror) {
          request.onerror(new Event('could not open DB'));
        }
        return;
      } else {
        (request as any).result = new Database(name, version);
        if (request.onupgradeneeded) {
          request.onupgradeneeded(new VersionChangeEvent({ result: request.result }, version, -1));
        }
        if (request.onsuccess) {
          request.onsuccess(new Event(''));
        }
      }
    }, 10);

    return request;
  }
}
