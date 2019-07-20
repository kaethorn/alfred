import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../testing/test.module';
import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';
import { ThumbnailsServiceMocks as thumbnailsService } from '../../testing/thumbnails.service.mocks';

import { ComicsService } from '../comics.service';
import { ThumbnailsService } from '../thumbnails.service';
import { IssuesPage } from './issues.page';

describe('IssuesPage', () => {
  let component: IssuesPage;
  let fixture: ComponentFixture<IssuesPage>;

  beforeEach(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: ComicsService, useValue: comicsService
    }, {
      provide: ThumbnailsService, useValue: thumbnailsService
    });
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(IssuesPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
