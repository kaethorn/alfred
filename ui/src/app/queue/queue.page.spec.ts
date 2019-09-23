import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../testing/test.module';
import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';

import { ComicsService } from '../comics.service';

import { QueuePage } from './queue.page';

describe('QueuePage', () => {
  let component: QueuePage;
  let fixture: ComponentFixture<QueuePage>;

  beforeEach(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: ComicsService, useValue: comicsService
    });
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(QueuePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
