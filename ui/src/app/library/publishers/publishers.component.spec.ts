import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../../testing/test.module';

import { PublishersComponent } from './publishers.component';

describe('PublishersComponent', () => {
  let component: PublishersComponent;
  let fixture: ComponentFixture<PublishersComponent>;

  beforeEach(async(() => {
    const testModule: any = TestModule();
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PublishersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
