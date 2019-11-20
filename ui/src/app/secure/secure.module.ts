import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { SecurePipe } from './secure.pipe';

@NgModule({
  imports: [
    HttpClientModule
  ],
  declarations: [
    SecurePipe
  ],
  exports: [
    SecurePipe
  ]
})
export class SecureModule {}
