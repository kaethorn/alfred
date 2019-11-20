import { Component, Output, EventEmitter } from '@angular/core';

import { ComicsService } from '../../comics.service';
import { StatsService } from '../../stats.service';
import { Stats } from '../../stats';
import { Comic, ScannerIssue } from 'src/app/comic';
import { ComicDatabaseService } from 'src/app/comic-database.service';

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
  issues: ScannerIssue[] = [];
  stats: Stats;
  cachedComicsCount = 0;

  indeterminate: string;
  scanProgress: EventSource;

  constructor (
    private statsService: StatsService,
    private comicsService: ComicsService,
    private comicDatabaseService: ComicDatabaseService,
  ) {
    this.getStats();
    this.getComicsWithErrors();
    this.setCachedComicsCount();
  }

  private getStats () {
    this.statsService.get().subscribe((stats: Stats) => {
      this.stats = stats;
    });
  }

  private getComicsWithErrors (): void {
    this.issues.splice(0);
    this.comicsService.listComicsWithErrors()
      .subscribe((data: Comic[]) => {
        data.forEach((comic: Comic) => {
          this.issues.push(...comic.errors);
        });
      });
  }

  scan () {
    this.issues = [];

    this.scanProgress = new EventSource('/api/scan-progress?ngsw-bypass');

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

    this.scanProgress.addEventListener('scan-issue', (event: any) => {
      if (!event.data) {
        this.close();
        return;
      }

      const issue: ScannerIssue = <ScannerIssue>JSON.parse(event.data);

      this.issues.push(issue);
    });

    this.scanProgress.addEventListener('done', () => {
      this.indeterminate = null;
      this.scanned.emit(true);
      this.getStats();
      this.getComicsWithErrors();

      this.close();
    });
  }

  deleteComics () {
    this.comicsService.deleteComics().subscribe(() => {
      this.getStats();
      this.getComicsWithErrors();
    });
  }

  deleteProgress () {
    this.comicsService.deleteProgress().subscribe(() => {
      this.getStats();
      this.getComicsWithErrors();
    });
  }

  deleteProgressForCurrentUser () {
    this.comicsService.deleteProgressForCurrentUser().subscribe(() => {
      this.getStats();
      this.getComicsWithErrors();
    });
  }

  bundleVolumes () {
    this.comicsService.bundleVolumes().subscribe();
  }

  async deleteCachedComics () {
    await this.comicDatabaseService.deleteAll();
    this.setCachedComicsCount();
  }

  private async setCachedComicsCount () {
    this.comicDatabaseService.getComics().then((comics) => {
      this.cachedComicsCount = comics.length;
    });
  }

  private close () {
    this.counter = 0;
    this.total = 0;
    this.scanProgress.close();
    this.scanProgress = null;
  }
}
