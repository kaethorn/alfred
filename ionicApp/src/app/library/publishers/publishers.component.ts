import { Component, Input, OnInit } from '@angular/core';

import { Publisher } from './../../publisher';

@Component({
  selector: 'app-publishers',
  templateUrl: './publishers.component.html',
  styleUrls: ['./publishers.component.sass'],
})
export class PublishersComponent implements OnInit {

  publishersData: Publisher[] = [];

  @Input() publishers: Publisher[]

  constructor() { }

  ngOnInit() {
    this.publishersData = this.publishers;
  }

  protected filter (value: string) {
    this.publishersData = this.publishers
      .filter(publisher => publisher.series.filter(series => series.series.match(value)).length)
      .map(publisher => ({
        ...publisher,
        series: publisher.series.filter(series => series.series.match(value))
      }));
  }
}
