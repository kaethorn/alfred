import { of } from 'rxjs';

import { StatsService } from '../app/stats.service';

export class StatsServiceMocks {

  public static get statsService(): jasmine.SpyObj<StatsService> {
    return jasmine.createSpyObj('StatsService', {
      get: of({})
    });
  }
}
