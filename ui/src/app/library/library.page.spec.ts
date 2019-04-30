import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../testing/test.module';
import { VolumesServiceMocks as volumesService } from '../../testing/volumes.service.mocks';

import { VolumesService } from '../volumes.service';
import { LibraryPage } from './library.page';

describe('LibraryPage', () => {
  let component: LibraryPage;
  let fixture: ComponentFixture<LibraryPage>;

  beforeEach(async(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: VolumesService, useValue: volumesService
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LibraryPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
