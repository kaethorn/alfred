import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from './../../../testing/test.module';

import { ScannerComponent } from './scanner.component';

describe('ScannerComponent', () => {
  let component: ScannerComponent;
  let fixture: ComponentFixture<ScannerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule(TestModule()).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#scan', () => {

    beforeEach(function () {
      this.addEventListenerSpy = spyOn(EventSource.prototype, 'addEventListener');
      spyOn(EventSource.prototype, 'close');
      component.scan();
    });

    it('adds event listeners', () => {
      expect(EventSource.prototype.addEventListener)
        .toHaveBeenCalledWith('total', jasmine.any(Function));
      expect(EventSource.prototype.addEventListener)
        .toHaveBeenCalledWith('current-file', jasmine.any(Function));
      expect(EventSource.prototype.addEventListener)
        .toHaveBeenCalledWith('error', jasmine.any(Function));
      expect(EventSource.prototype.addEventListener)
        .toHaveBeenCalledWith('done', jasmine.any(Function));
    });

    describe('when complete', () => {

      beforeEach(function () {
        const doneCallback = this.addEventListenerSpy.calls.mostRecent().args[1];
        spyOn(component.scanned, 'emit');
        doneCallback();
      });

      it('emits the `scanned` event', () => {
        expect(component.scanned.emit).toHaveBeenCalledWith(true);
      });

      it('closes the event source', () => {
        expect(EventSource.prototype.close).toHaveBeenCalled();
      });
    });
  });
});
