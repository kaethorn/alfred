import { of } from 'rxjs';

import { thumbnail1 as thumbnail } from './thumbnail.fixtures';

const thumbnailsService = jasmine.createSpyObj('ThumbnailsService', [
  'get'
]);

thumbnailsService.get.and.returnValue( of(thumbnail) );

export { thumbnailsService as ThumbnailsServiceMocks };
