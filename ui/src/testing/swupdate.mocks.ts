import { SwUpdate } from '@angular/service-worker';
import { of } from 'rxjs';

export class SwUpdateMocks {

  public static get swUpdate(): jasmine.SpyObj<SwUpdate> {
    const updates = jasmine.createSpyObj('SwUpdate', {
      activateUpdate: Promise.resolve(),
      checkForUpdate: Promise.resolve()
    });
    updates.available = of({  available: { hash: '2' }, current: { hash: '1' } });
    updates.activated = of({ current: { hash: '4' }, previous: { hash: '3' } });
    updates.isEnabled = true;

    return updates;
  }
}
