import { of } from 'rxjs';

const statsService = jasmine.createSpyObj('StatsService', [
  'get'
]);

statsService.get.and.returnValue( of({}) );

export { statsService as StatsServiceMocks };
