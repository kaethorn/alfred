import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { TestModule } from '../../../testing/test.module';
import { VolumesComponent } from './volumes.component';

describe('VolumesComponent', () => {
  let component: VolumesComponent;
  let fixture: ComponentFixture<VolumesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule(TestModule()).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VolumesComponent);
    component = fixture.componentInstance;
    component.volumes = [{
      volume: '1999',
      thumbnail: '',
      series: 'Batgirl',
      publisher: 'DC Comics',
      read: false,
      issueCount: 10,
      readCount: 0
    }];
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
