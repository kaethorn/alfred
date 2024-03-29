/**
 * A mock IndexedDB implementation.
 *
 * It supports setting error flags so it's useful to test error
 * handling during transactions and requests.
 *
 * It's by no means feature complete and only supports IndexedDB
 * methods used in this project.
 */

const storage: { [key: string]: any } = {};
const flags: IndexedDbMockFlag[] = [];

export enum IndexedDbMockFlag {
  OPEN_ERROR,
  TRANSACTION_ERROR,
  TRANSACTION_ABORT,
  REQUEST_ERROR
}

class Request implements IDBRequest {

  public error: DOMException | null = null;
  public result: any;
  public addEventListener: any;
  public removeEventListener: any;
  public dispatchEvent: any;
  public readyState: any;
  public source: any;
  public transaction: any;

  constructor(result: any) {
    setTimeout(() => {
      this.result = result;
      if (!this.hasRequestErrors() && this.onsuccess) {
        this.onsuccess({ target: { result: this.result } });
      }
    });
  }

  public onerror: (event: any) => {} = (): any => {};
  public onsuccess: (event: any) => {} = (): any => {};

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

class Index implements IDBIndex {

  public keyPath: string | string [];
  public multiEntry = false;
  public name: string;
  public objectStore: IDBObjectStore;
  public unique = false;

  private options: IDBIndexParameters | undefined;

  constructor(
    objectStore: IDBObjectStore,
    name: string,
    keyPath: string | string[],
    options?: IDBIndexParameters
  ) {
    this.objectStore = objectStore;
    this.name = name;
    this.keyPath = keyPath;
    this.options = options;
  }

  public count(): IDBRequest<any> {
    return new Request(null);
  }

  public get(): IDBRequest {
    return new Request(null);
  }

  public getAll(query: any, count?: number): IDBRequest {
    if (!this.hasTransactionErrors()) {
      const allItems: Map<string, any> = storage[this.objectStore.transaction.db.name][this.objectStore.name].store;
      const items: any[] = Array.from(allItems.values()).filter(item => {
        if (typeof this.keyPath === 'string') {
          return item[this.keyPath] === query;
        }
        return !!this.keyPath.reduce((result, key) => result && item[key] === query, true);
      });

      if (this.options?.unique) {
        return new Request(items.slice(0, 1));
      } else if (count !== undefined && count !== null) {
        return new Request(items.slice(0, count));
      }

      return new Request(items);
    }
    return new Request(null);
  }

  public getAllKeys(): any {}
  public getKey(): any {}
  public openCursor(): any {}
  public openKeyCursor(): any {}

  private hasTransactionErrors(): boolean {
    if (flags.includes(IndexedDbMockFlag.TRANSACTION_ERROR)) {
      if (this.objectStore.transaction.onerror) {
        this.objectStore.transaction.onerror(new Event('Could not execute transaction.'));
      }
      return true;
    } else if (flags.includes(IndexedDbMockFlag.TRANSACTION_ABORT)) {
      if (this.objectStore.transaction.onabort) {
        this.objectStore.transaction.onabort(new Event('Aborted transaction.'));
      }
      return true;
    }
    return false;
  }
}

class ObjectStore implements IDBObjectStore {

  public indexNames!: DOMStringList;
  public autoIncrement = false;
  public keyPath = '';
  public name: string;
  public transaction!: IDBTransaction;

  private store = new Map<string, any>();
  private indices: { [key: string]: IDBIndex } = {};

  constructor(
    name: string
  ) {
    this.name = name;
  }

  public getKey(query: any): IDBRequest {
    if (!this.hasTransactionErrors() && this.store.has(query)) {
      return new Request(query);
    }
    return new Request(null);
  }

  public get(query: any): IDBRequest {
    if (!this.hasTransactionErrors()) {
      return new Request(this.store.get(query));
    }
    return new Request(null);
  }

  public getAll(): IDBRequest {
    if (!this.hasTransactionErrors()) {
      return new Request(Array.from(this.store.values()));
    }
    return new Request(null);
  }

  public index(name: string): IDBIndex {
    return this.indices[name];
  }

  public delete(key: any): IDBRequest {
    setTimeout(() => {
      this.store.delete(key);
      if (!this.hasTransactionErrors() && this.transaction.oncomplete) {
        this.transaction.oncomplete(new Event(''));
      }
    }, 10);
    return new Request(null);
  }

  public put(item: any, key: any = item.id): IDBRequest {
    setTimeout(() => {
      this.store.set(key, item);
      if (!this.hasTransactionErrors() && this.transaction.oncomplete) {
        this.transaction.oncomplete(new Event(''));
      }
    }, 10);
    return new Request(null);
  }

  public createIndex(name: string, keyPath: string | string[], options?: IDBIndexParameters): IDBIndex {
    this.indices[name] = new Index(this, name, keyPath, options);
    return this.indices[name];
  }

  public getAllKeys(): any {
  }

  public clear(): any {
  }

  public add(item: any, key: any = item.id): IDBRequest {
    if (this.store.has(key)) {
      if (this.transaction.onerror) {
        this.transaction.onerror(new Event('Constraint error'));
      }
      return new Request(null);
    }
    return this.put(item, key);
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
    return (string in this);
  }

  public item(index: number): string | null {
    return this[index];
  }
}

class Transaction implements IDBTransaction {

  public objectStoreNames: DOMStringList;
  public db: IDBDatabase;
  public error!: DOMException;
  public mode!: IDBTransactionMode;
  public onabort!: () => {};
  public oncomplete!: () => {};
  public onerror!: () => {};
  public durability!: IDBTransactionDurability;
  public commit: any;

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
  public objectStoreNames!: DOMStringList;
  public version: number;

  public onabort!: () => {};
  public onclose!: () => {};
  public onerror!: () => {};
  public onversionchange!: () => {};

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
  public bubbles = false;
  public cancelBubble = false;
  public cancelable = false;
  public composed = false;
  public currentTarget: any;
  public defaultPrevented = false;
  public eventPhase = 0;
  public isTrusted = false;
  public returnValue = false;
  public srcElement: any;
  public timeStamp = 0;
  public type = '';
  public AT_TARGET = 0;
  public BUBBLING_PHASE = 0;
  public CAPTURING_PHASE = 0;
  public NONE = 0;

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

  public static get get(): IDBFactory {
    return {
      cmp: null as any,
      databases: null as any,
      deleteDatabase: null as any,
      open: (name: string, version?: number): IDBOpenDBRequest => {
        storage[name] = {};
        storage[name].$version = version;
        return this.openRequest(name, version as any);
      }
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
      addEventListener: null as any,
      dispatchEvent: null as any,
      error: null,
      onblocked: null,
      onerror: null,
      onsuccess: null,
      onupgradeneeded: null,
      readyState: null as any,
      removeEventListener: null as any,
      result: null as any,
      source: null as any,
      transaction: null
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
    });

    return request;
  }
}
