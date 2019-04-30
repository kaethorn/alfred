import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../../testing/test.module';
import { VolumesServiceMocks as volumesService } from '../../../testing/volumes.service.mocks';

import { VolumesService } from '../../volumes.service';
import { VolumesComponent } from './volumes.component';

describe('VolumesComponent', () => {
  let component: VolumesComponent;
  let fixture: ComponentFixture<VolumesComponent>;

  beforeEach(async(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: VolumesService, useValue: volumesService
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VolumesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('renders volume information', () => {
    const volumeElement: HTMLElement = fixture.nativeElement;
    expect(volumeElement.textContent).toContain('1999');
  });
});
