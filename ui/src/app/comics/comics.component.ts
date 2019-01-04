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
  public receivedMessages: string[] = [];
  comics: Array<Comic> = [];
  scanStatus: boolean = null;

  constructor (
    private comicsService: ComicsService,
    private rxStompService: RxStompService
  ) {
    this.list();
  }

  ngOnInit () {
    this.topicSubscription = this.rxStompService.watch('/progress/scanner').subscribe((message: Message) => {
      this.receivedMessages.push(JSON.parse(message.body).file);
    });
  }

  ngOnDestroy() {
    this.topicSubscription.unsubscribe();
  }

  scan () {
    this.comicsService.scan()
      .subscribe(() => {
        this.scanStatus = true;
        this.list();
      },
      error => {
        this.scanStatus = false;
      });
  }

  onSendMessage () {
    const message = `Message generated at ${new Date}`;
    this.rxStompService.publish({ destination: '/progress/scanner', body: message });
  }

  list () {
    this.comicsService.list()
      .subscribe((data: any) => {
        this.comics = data._embedded.comics as Comic[];
      });
  }
}
