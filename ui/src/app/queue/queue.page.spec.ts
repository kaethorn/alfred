import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QueuePage } from './queue.page';

describe('QueuePage', () => {
  let component: QueuePage;
  let fixture: ComponentFixture<QueuePage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QueuePage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueuePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
