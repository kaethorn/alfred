import { Component, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';

import { Comic, ScannerIssue } from '../../comic';
import { ComicDatabaseService } from '../../comic-database.service';
import { ComicsService } from '../../comics.service';
import { Stats } from '../../stats';
import { StatsService } from '../../stats.service';

@Component({
  selector: 'app-scanner',
  styleUrls: [ './scanner.component.sass' ],
  templateUrl: './scanner.component.html'
})
export class ScannerComponent {

  @Output() public scanned = new EventEmitter<boolean>();

  public total = 0;
  public file: string;
  public counter = 0;
  public issues: ScannerIssue[] = [];
  public stats: Stats = {
    issues: 0,
    lastScanFinished: null,
    lastScanStarted: null,
    publishers: 0,
    series: 0,
    users: 0,
    volumes: 0
  };
  public cachedComicsCount = 0;
  public lastScanDuration: string;
  public indeterminate: string;
  public scanProgress: EventSource;

  constructor(
    private statsService: StatsService,
    private comicsService: ComicsService,
    private comicDatabaseService: ComicDatabaseService
  ) {
    this.getStats();
    this.getComicsWithErrors();
    this.setCachedComicsCount();
  }

  public scan(): void {
    this.issues = [];

    this.scanProgress = new EventSource('/api/scan-progress?ngsw-bypass');

    this.scanProgress.addEventListener('START', () => {
      this.indeterminate = 'Counting files';
    });

    this.scanProgress.addEventListener('TOTAL', (event: any) => {
      this.indeterminate = null;
      this.total = this.total || event.data;
    });

    this.scanProgress.addEventListener('CURRENT_FILE', (event: any) => {
      this.file = event.data;
      this.counter += 1;
    });

    this.scanProgress.addEventListener('CLEAN_UP', () => {
      this.counter = 0;
      this.total = 0;
      this.indeterminate = 'Cleaning up';
    });

    this.scanProgress.addEventListener('ASSOCIATION', () => {
      this.indeterminate = 'Bundling volumes';
    });

    this.scanProgress.addEventListener('SCAN_ISSUE', (event: any) => {
      if (!event.data) {
        this.close();
        return;
      }

      const issue: ScannerIssue = <ScannerIssue>JSON.parse(event.data);

      this.issues.push(issue);
    });

    this.scanProgress.addEventListener('DONE', () => {
      this.indeterminate = null;
      this.scanned.emit(true);
      this.getStats();
      this.getComicsWithErrors();

      this.close();
    });
  }

  public deleteComics(): void {
    this.comicsService.deleteComics().subscribe(() => {
      this.getStats();
      this.getComicsWithErrors();
    });
  }

  public deleteProgress(): void {
    this.comicsService.deleteProgress().subscribe(() => {
      this.getStats();
      this.getComicsWithErrors();
    });
  }

  public deleteProgressForCurrentUser(): void {
    this.comicsService.deleteProgressForCurrentUser().subscribe(() => {
      this.getStats();
      this.getComicsWithErrors();
    });
  }

  public bundleVolumes(): void {
    this.comicsService.bundleVolumes().subscribe();
  }

  public async deleteCachedComics(): Promise<void> {
    await this.comicDatabaseService.deleteAll();
    this.setCachedComicsCount();
  }

  private setCachedComicsCount(): void {
    this.comicDatabaseService.getComics().then(comics => {
      this.cachedComicsCount = comics.length;
    });
  }

  private close(): void {
    this.counter = 0;
    this.total = 0;
    this.scanProgress.close();
    this.scanProgress = null;
  }

  private getStats(): void {
    this.statsService.get().subscribe((stats: Stats) => {
      this.stats = stats;
      this.setDuration();
    });
  }

  private getComicsWithErrors(): void {
    this.issues.splice(0);
    this.comicsService.listComicsWithErrors()
      .subscribe((data: Comic[]) => {
        data.forEach((comic: Comic) => {
          this.issues.push(...comic.errors);
        });
      });
  }

  private setDuration(): void {
    this.lastScanDuration = moment.duration(
      moment(this.stats.lastScanFinished).diff(moment(this.stats.lastScanStarted))
    ).humanize();
  }
}
