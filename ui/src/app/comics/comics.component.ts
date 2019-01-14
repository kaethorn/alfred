import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { ComicsService } from './comics.service';
import { Comic } from './comic';

@Component({
  selector: 'app-comics',
  templateUrl: './comics.component.html',
  styleUrls: ['./comics.component.css']
})
export class ComicsComponent implements OnInit, OnDestroy {
  private topicSubscription: Subscription;
  total: number = 0;
  file: string;
  counter: number = 0;
  comics: Array<Comic> = [];

  constructor (
    private comicsService: ComicsService
  ) {
    this.list();
  }

  ngOnInit () {
    const evtSource = new EventSource('/api/scan-progress');
    evtSource.onmessage = event => {
      this.onMessage(event.data);
    }
  }

  ngOnDestroy () {
    this.topicSubscription.unsubscribe();
  }

  private onMessage (message) {
    if (message.file) {
      this.file = message.file;
      this.counter += 1;
    }
    this.total = this.total || message.total;
    if (message.done) {
      this.counter = 0;
      this.total = 0;
      this.list();
    }
  }

  scan () {
    this.comicsService.scan().subscribe(() => {
      // Nothing returned here as the progress will be transmitted via websocket.
    });
  }

  private list () {
    this.comicsService.list()
      .subscribe((data: any) => {
        this.comics = data._embedded.comics as Comic[];
      });
  }
}
