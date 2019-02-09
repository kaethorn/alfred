import { Component, Input } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { Volume } from '../../publisher';

@Component({ selector: 'app-volume',
  templateUrl: './volume.component.html',
  styleUrls: ['./volume.component.sass']
})
export class VolumeComponent {

  @Input() volume: Volume;
  @Input() series: string;
  @Input() publisher: string;

  constructor (
    private sanitizer: DomSanitizer
  ) {
  }

  get thumbnail(): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ this.volume.thumbnail }`);
  }
}
