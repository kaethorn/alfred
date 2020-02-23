import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';
import { ThumbnailsServiceMocks as thumbnailsService } from '../../testing/thumbnails.service.mocks';
import { ComicsService } from '../comics.service';
import { ThumbnailsService } from '../thumbnails.service';

import { IssuesPageModule } from './issues.module';
import { IssuesPage } from './issues.page';

describe('IssuesPage', () => {
  let component: IssuesPage;
  let fixture: ComponentFixture<IssuesPage>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        IssuesPageModule,
        RouterTestingModule.withRoutes([
        ])
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ThumbnailsService, useValue: thumbnailsService
      }]
    });
    fixture = TestBed.createComponent(IssuesPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
