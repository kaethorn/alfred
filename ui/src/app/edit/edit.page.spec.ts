import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';

import { ComicsService } from '../comics.service';

import { EditPage } from './edit.page';
import { EditPageModule } from './edit.module';

describe('EditPage', () => {
  let component: EditPage;
  let fixture: ComponentFixture<EditPage>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        EditPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: ComicsService, useValue: comicsService
      }]
    });
    fixture = TestBed.createComponent(EditPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
