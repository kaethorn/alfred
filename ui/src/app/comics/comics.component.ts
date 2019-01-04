import { Component, OnInit, OnDestroy } from '@angular/core';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
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
  public total: number = 0;
  public file: string;
  public counter: number = 0;
  comics: Array<Comic> = [];

  constructor (
    private comicsService: ComicsService,
    private rxStompService: RxStompService
  ) {
    this.list();
  }

  ngOnInit () {
    this.topicSubscription = this.rxStompService.watch('/progress/scanner').subscribe((message: Message) => {
      const response = JSON.parse(message.body);
      if (response.file) {
        this.file = response.file;
        this.counter += 1;
      }
      this.total = this.total || response.total;
      if (this.counter >= this.total) {
        this.counter = 0;
        this.total = 0;
        this.list();
      }
    });
  }

  ngOnDestroy() {
    this.topicSubscription.unsubscribe();
  }

  scan () {
    this.comicsService.scan().subscribe(() => {
      // Nothing returned here as the progress will be transmitted via websocket.
    });
  }

  list () {
    this.comicsService.list()
      .subscribe((data: any) => {
        this.comics = data._embedded.comics as Comic[];
      });
  }
}
