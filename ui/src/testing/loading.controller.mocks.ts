import { LoadingController } from '@ionic/angular';

export class LoadingControllerMocks {

  private static loadingElement: jasmine.SpyObj<HTMLIonLoadingElement>;

  public static get loadingElementSpy(): jasmine.SpyObj<HTMLIonLoadingElement> {
    if (!this.loadingElement) {
      this.loadingElement = jasmine.createSpyObj('HTMLIonLoadingElement', {
        present: Promise.resolve(),
        dismiss: Promise.resolve()
      });
    }
    return this.loadingElement;
  }

  public static get loadingController(): jasmine.SpyObj<LoadingController> {
    delete this.loadingElement;
    return jasmine.createSpyObj('LoadingController', {
      create: Promise.resolve(this.loadingElementSpy)
    });
  }
}
