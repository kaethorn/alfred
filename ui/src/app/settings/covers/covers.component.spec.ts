import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SettingsPageModule } from '../settings.module';

import { CoversComponent } from './covers.component';

let component: CoversComponent;
let fixture: ComponentFixture<CoversComponent>;

describe('CoversComponent', () => {

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
