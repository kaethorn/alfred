import { of } from 'rxjs';

import { QueueService } from '../app/queue.service';

import { ComicFixtures } from './comic.fixtures';

export class QueueServiceMocks {

  public static get queueService(): jasmine.SpyObj<QueueService> {
    return jasmine.createSpyObj('QueueService', {
      process: of(ComicFixtures.comic),
      add: Promise.resolve()
    });
  }
}
