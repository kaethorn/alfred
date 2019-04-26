import { of } from 'rxjs';

const volumesService = jasmine.createSpyObj('VolumesService', [
  'listVolumesByPublisher'
]);

volumesService.listVolumesByPublisher.and.returnValue( of([]) );

export { volumesService as VolumesServiceMocks };
