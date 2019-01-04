import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';

import { RxStompConfig } from './rx-stomp.config';

import { AppComponent } from './app.component';
import { ComicsComponent } from './comics/comics.component';

@NgModule({
  declarations: [
    AppComponent,
    ComicsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [{
    provide: InjectableRxStompConfig,
    useValue: RxStompConfig
  }, {
    provide: RxStompService,
    useFactory: rxStompServiceFactory,
    deps: [InjectableRxStompConfig]
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
