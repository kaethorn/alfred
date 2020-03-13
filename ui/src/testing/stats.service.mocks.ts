import { of } from 'rxjs';

import { StatsService } from '../app/stats.service';

export class StatsServiceMocks {

  public static get statsService(): jasmine.SpyObj<StatsService> {
    const statsService = jasmine.createSpyObj('StatsService', [
      'get'
    ]);

    statsService.get.and.returnValue(of({}));

    return statsService;
  }
}
