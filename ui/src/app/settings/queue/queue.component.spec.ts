import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from 'src/testing/test.module';

import { QueueComponent } from './queue.component';

describe('QueueComponent', () => {
  let component: QueueComponent;
  let fixture: ComponentFixture<QueueComponent>;

  beforeEach(() => {
    const testModule: any = TestModule();
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(QueueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(QueueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
