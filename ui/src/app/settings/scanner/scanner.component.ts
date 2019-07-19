import { Component, Output, EventEmitter } from '@angular/core';

import { StatsService } from '../../stats.service';
import { Stats } from '../../stats';

interface Error {
  message: string;
  file?: string;
  date: string;
}

@Component({
  selector: 'app-scanner',
  templateUrl: './scanner.component.html',
  styleUrls: ['./scanner.component.sass']
})
export class ScannerComponent {

  @Output() scanned = new EventEmitter<boolean>();

  total = 0;
  file: string;
  counter = 0;
  errors: Error[] = [];
  stats: Stats;

  indeterminate: string;
  scanProgress: EventSource;

  constructor (
    private statsService: StatsService
  ) {
    this.getStats();
  }

  private getStats () {
    this.statsService.get().subscribe((stats: Stats) => {
      this.stats = stats;
    });
  }

  scan () {
    this.errors = [];

    this.scanProgress = new EventSource('/api/scan-progress');

    this.scanProgress.addEventListener('start', (event: any) => {
      this.indeterminate = 'Counting files';
    });

    this.scanProgress.addEventListener('total', (event: any) => {
      this.indeterminate = null;
      this.total = this.total || event.data;
    });

    this.scanProgress.addEventListener('current-file', (event: any) => {
      this.file = event.data;
      this.counter += 1;
    });

    this.scanProgress.addEventListener('cleanUp', (event: any) => {
      this.counter = 0;
      this.total = 0;
      this.indeterminate = 'Cleaning up';
    });

    this.scanProgress.addEventListener('association', (event: any) => {
      this.indeterminate = 'Bundling volumes';
    });

    this.scanProgress.addEventListener('error', (event: any) => {
      if (!event.data) {
        this.close();
        return;
      }
      const parts = event.data.split('|');
      this.errors.push({
        message: parts[0],
        file   : parts.length ? parts[1] : null,
        date   : new Date().toISOString()
      });
    });

    this.scanProgress.addEventListener('done', () => {
      this.indeterminate = null;
      this.scanned.emit(true);
      this.getStats();

      this.close();
    });
  }

  private close () {
    this.counter = 0;
    this.total = 0;
    this.scanProgress.close();
    this.scanProgress = null;
  }
}
