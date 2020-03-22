import { AsyncSubject } from 'rxjs';

import { IndexedDbService } from '../app/indexed-db.service';

export class IndexedDbServiceMocks {

  public static get IndexedDbService(): jasmine.SpyObj<IndexedDbService> {
    const service = jasmine.createSpyObj('IndexedDbService', {
      open: null,
      save: Promise.resolve(new Event('')),
      hasKey: Promise.resolve(true)
    });
    service.ready = new AsyncSubject<void>();

    service.open.and.callFake(() => {
      service.ready.complete();
    });

    return service;
  }
}
