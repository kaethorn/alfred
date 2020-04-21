import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule, Injectable } from '@angular/core';
import { BrowserModule, HammerModule, HammerGestureConfig, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import * as Hammer from 'hammerjs';

import { environment } from '../environments/environment';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './auth.interceptor';
import { LOCATION_TOKEN } from './location.token';

@Injectable()
export class HammerConfig extends HammerGestureConfig {
  public overrides = <any> {
    swipe: { direction: Hammer.DIRECTION_ALL }
  };
}

@NgModule({
  bootstrap: [ AppComponent ],
  declarations: [
    AppComponent
  ],
  entryComponents: [],
  imports: [
    AppRoutingModule,
    BrowserModule,
    HttpClientModule,
    IonicModule.forRoot(),
    HammerModule,
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production })
  ],
  providers: [
    SplashScreen,
    StatusBar,
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    { multi: true, provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor },
    { provide: LOCATION_TOKEN, useValue: window.location },
    { provide: HAMMER_GESTURE_CONFIG, useClass: HammerConfig }
  ]
})
export class AppModule {}
