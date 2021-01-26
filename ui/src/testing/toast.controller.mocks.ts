import { ToastController } from '@ionic/angular';

export class ToastControllerMocks {

  private static toastElement: jasmine.SpyObj<HTMLIonToastElement>;

  public static get toastElementSpy(): jasmine.SpyObj<HTMLIonToastElement> {
    if (!this.toastElement) {
      this.toastElement = jasmine.createSpyObj('HTMLIonToastElement', [ 'present' ]);
    }
    return this.toastElement;
  }

  public static get toastController(): jasmine.SpyObj<ToastController> {
    this.toastElement = null as any;
    return jasmine.createSpyObj('ToastController', {
      create: Promise.resolve(this.toastElementSpy)
    });
  }
}
