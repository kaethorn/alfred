import { PopoverController } from '@ionic/angular';

export class PopoverControllerMocks {

  private static popoverElement: jasmine.SpyObj<HTMLIonPopoverElement>;

  public static get popoverElementSpy(): jasmine.SpyObj<HTMLIonPopoverElement> {
    if (!this.popoverElement) {
      this.popoverElement = jasmine.createSpyObj('HTMLIonPopoverElement', ['present']);
    }
    return this.popoverElement;
  }

  public static get popoverController(): jasmine.SpyObj<PopoverController> {
    delete this.popoverElement;
    return jasmine.createSpyObj('PopoverController', {
      create: Promise.resolve(this.popoverElementSpy),
      dismiss: Promise.resolve(true)
    });
  }
}
