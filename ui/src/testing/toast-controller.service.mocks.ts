import { ToastController } from '@ionic/angular';

export class ToastControllerServiceMocks {

  private static toastElement: jasmine.SpyObj<HTMLIonToastElement>;

  public static get toastElementSpy(): jasmine.SpyObj<HTMLIonToastElement> {
    if (!this.toastElement) {
      this.toastElement = jasmine.createSpyObj('HTMLIonToastElement', ['present']);
    }
    return this.toastElement;
  }

  public static get toastController(): jasmine.SpyObj<ToastController> {
    delete this.toastElement;
    const toastController = jasmine.createSpyObj('ToastController', ['create']);
    toastController.create.and.returnValue(Promise.resolve(this.toastElementSpy));
    return toastController;
  }
}
