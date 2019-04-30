import { of } from 'rxjs';

import { volume1 as volume } from './volume.fixtures';

const volumesService = jasmine.createSpyObj('VolumesService', [
  'listVolumesByPublisher',
  'listVolumes'
]);

volumesService.listVolumesByPublisher.and.returnValue( of([volume]) );
volumesService.listVolumes.and.returnValue( of([volume]) );

export { volumesService as VolumesServiceMocks };
