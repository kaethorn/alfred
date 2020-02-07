import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CoversComponent } from './covers.component';
import { SettingsPageModule } from '../settings.module';

describe('CoversComponent', () => {
  let component: CoversComponent;
  let fixture: ComponentFixture<CoversComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        SettingsPageModule,
        RouterTestingModule
      ]
    });
    fixture = TestBed.createComponent(CoversComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CoversComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
