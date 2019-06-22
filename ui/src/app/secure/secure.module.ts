import { NgModule } from '@angular/core';

import { SecurePipe } from './secure.pipe';

@NgModule({
  imports: [],
  declarations: [
    SecurePipe
  ],
  exports: [
    SecurePipe
  ]
})
export class SecureModule {}
