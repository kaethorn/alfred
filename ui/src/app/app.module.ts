import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule, Injectable } from '@angular/core';
import { BrowserModule, HammerModule, HammerGestureConfig, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import Hammer from 'hammerjs';

import { environment } from '../environments/environment';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './auth.interceptor';
import { CACHES_TOKEN } from './caches.token';
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
  imports: [
    AppRoutingModule,
    BrowserModule,
    HttpClientModule,
    IonicModule.forRoot(),
    HammerModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      // Register the ServiceWorker as soon as the app is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    })
  ],
  providers: [
    SplashScreen,
    StatusBar,
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    { multi: true, provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor },
    { provide: LOCATION_TOKEN, useValue: window.location },
    { provide: CACHES_TOKEN, useValue: window.caches },
    { provide: HAMMER_GESTURE_CONFIG, useClass: HammerConfig }
  ]
})
export class AppModule {}
