import { SafeUrl, DomSanitizer } from '@angular/platform-browser';
import { Injectable } from '@angular/core';

import { from } from 'rxjs';
import { groupBy, mergeMap, filter, toArray, map } from 'rxjs/operators';

import { Comic } from './comic';
import { ComicDatabaseService } from './comic-database.service';
import { ComicsService } from './comics.service';
import { QueueService } from './queue.service';
import { ThumbnailsService } from './thumbnails.service';

export interface StoredState {
  [name: string]: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ComicStorageService {

  constructor (
    private sanitizer: DomSanitizer,
    private thumbnailsService: ThumbnailsService,
    private comicDatabaseService: ComicDatabaseService,
    private comicsService: ComicsService,
    private queueService: QueueService,
  ) { }

  /**
   * Retrieve the given comic from cache or from the server as a fallback.
   * @param comicId Comic ID to retrieve.
   * @param cache Whether to cache the comic if not already done so.
   * @returns A promise returning the comic.
   */
  get (comicId: string): Promise<Comic> {
    return new Promise((resolve, reject) => {
      this.comicDatabaseService.getComic(comicId)
        .then((comic) => resolve(comic))
        .catch(() => {
          this.comicsService.get(comicId).subscribe(
            (apiComic) => resolve(apiComic),
            () => reject());
        });
    });
  }

  /**
   * Reads the given page of this comic, setting the read state.
   * @param page Page number
   * @returns The URL to the image.
   */
  async readPage (comicId: string, page: number): Promise<string> {
    if (await this.comicDatabaseService.isStored(comicId)) {
      const comic = await this.get(comicId);
      comic.currentPage = page;
      comic.lastRead = new Date();
      if (comic.pageCount - 1 === page) {
        comic.read = true;
      }
      await this.saveComic(comic);
      await this.comicDatabaseService.save(comic);
      return this.comicDatabaseService.getImageUrl(comicId, page);
    } else {
      return Promise.resolve(`/api/read/${ comicId }/${ page }`);
    }
  }

  async saveIfStored (comic: Comic): Promise<void> {
    if (await this.comicDatabaseService.isStored(comic.id)) {
      await this.comicDatabaseService.save(comic);
    }
  }

  /**
   * Retrieves bookmarks either from API or from indexedDB.
   */
  getBookmarks (): Promise<Comic[]> {
    return new Promise((resolve, reject) => {
      this.comicsService.listLastReadByVolume().subscribe((comics: Comic[]) => {
        this.queueService.process().subscribe(
          () => {},
          () => resolve(comics),
          () => {
            this.comicsService.listLastReadByVolume().subscribe((updatedComics: Comic[]) => {
              resolve(updatedComics);
            });
        });
      }, () => {
        this.comicDatabaseService.getComics().then((comics: Comic[]) => {
          from(comics).pipe(
            filter(comic => !comic.read),
            groupBy(comic => `${comic.publisher}|${comic.series}|${comic.volume}`),
            mergeMap(group => group.pipe(
              toArray(),
              map(g => g.sort((a, b) => a.position > b.position ? 1 : -1))
            )),
            map(group => group[0]),
            toArray()
          ).subscribe((c) => {
            resolve(c);
          }, reject);
        }, () => reject());
      });
    });
  }

  /**
   * Caches the previous, the current and the three next comic books.
   * @param comicId The reference comic ID.
   */
  async storeSurrounding (comicId: string): Promise<StoredState> {
    const cachedIds: StoredState = {};
    const comic = await this.get(comicId);

    // Store the previous comic.
    if (comic.previousId !== null) {
      const previousComic = await this.get(comic.previousId);
      await this.comicDatabaseService.store(previousComic);
      cachedIds[previousComic.id] = true;
    }

    // Store the current comic.
    await this.comicDatabaseService.store(comic);
    cachedIds[comic.id] = true;

    // Store the next three comics.
    let nextComic: Comic = comic;
    for (const {} of new Array(3)) {
      if (nextComic.nextId !== null) {
        nextComic = await this.get(nextComic.nextId);
        await this.comicDatabaseService.store(nextComic);
        cachedIds[nextComic.id] = true;
      } else {
        break;
      }
    }

    // Traverse indexedDb's Comic collection for comic books outside
    // that range and delete them.
    const cachedComics = await this.comicDatabaseService.getComics();
    const comicsToDelete = cachedComics.filter((cachedComic) => {
      // Filter out comics from other volumes:
      return this.matchesVolume(comic, cachedComic) &&
        // Filter out comics that are not cached
        !(cachedComic.id in cachedIds);
    });
    for (const comicToDelete of comicsToDelete) {
      await this.comicDatabaseService.delete(comicToDelete);
    }
    return Promise.resolve(cachedIds);
  }

  /**
   * Deletes all stored comics in the given comic's volume.
   * @param comic Reference comic.
   */
  async deleteVolume (referenceComic: Comic): Promise<void> {
    const comics = await this.comicDatabaseService.getComics();
    const volume: Comic[] = comics.filter((comic) => {
      return this.matchesVolume(referenceComic, comic);
    });
    for (const comic of volume) {
      await this.comicDatabaseService.delete(comic);
    }
  }

  async getThumbnail (comicId: string): Promise<SafeUrl> {
    return new Promise((resolve, reject) => {
      this.comicDatabaseService.getImageUrl(comicId, 0)
        .then((thumbnail) => {
          resolve(this.sanitizer.bypassSecurityTrustResourceUrl(thumbnail));
        }).catch(() => {
          this.thumbnailsService.get(comicId).subscribe(resolve, reject);
        });
    });
  }

  private matchesVolume (comicA: Comic, comicB: Comic): boolean {
    return comicA.publisher === comicB.publisher &&
      comicA.series === comicB.series &&
      comicA.volume === comicB.volume;
  }

  private saveComic (comic: Comic) {
    this.comicsService.update(comic).subscribe(
      () => this.queueService.process(),
      () => this.queueService.add(comic));
  }
}
