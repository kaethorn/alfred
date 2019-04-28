import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { TestModule } from './../../../testing/test.module';
import { VolumeComponent } from './volume.component';

describe('VolumeComponent', () => {
  let component: VolumeComponent;
  let fixture: ComponentFixture<VolumeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule(TestModule()).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VolumeComponent);
    component = fixture.componentInstance;
    component.volume = {
      volume: '1999',
      thumbnail: '',
      series: 'Batgirl',
      publisher: 'DC Comics',
      read: false,
      issueCount: 10,
      readCount: 0
    };
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
