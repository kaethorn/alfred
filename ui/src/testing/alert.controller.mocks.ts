import { AlertController } from '@ionic/angular';

export class AlertControllerMocks {

  private static alertElement: jasmine.SpyObj<HTMLIonAlertElement>;

  public static get alertElementSpy(): jasmine.SpyObj<HTMLIonAlertElement> {
    if (!this.alertElement) {
      this.alertElement = jasmine.createSpyObj('HTMLIonAlertElement', {
        dismiss: Promise.resolve(),
        present: Promise.resolve()
      });
    }
    return this.alertElement;
  }

  public static get alertController(): jasmine.SpyObj<AlertController> {
    delete this.alertElement;
    return jasmine.createSpyObj('AlertController', {
      create: Promise.resolve(this.alertElementSpy)
    });
  }
}
