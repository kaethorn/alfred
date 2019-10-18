import { Comic } from './comic';
import { Injectable } from '@angular/core';
import { ComicDatabaseService } from './comic-database.service';
import { ComicsService } from './comics.service';
import { QueueService } from './queue.service';
import { from } from 'rxjs';
import { groupBy, mergeMap, first, filter, toArray } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ComicStorageService {

  constructor (
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
   * Retrieve the given comic from cache or from the server as a fallback.
   * @param comicId Comic ID to retrieve.
   * @param cache Whether to cache the comic if not already done so.
   * @returns A promise returning the comic.
   */
  async cache (comicId: string): Promise<Comic> {
    await this.storeSurrounding(comicId);
    return this.get(comicId);
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
      this.saveComic(comic);
      await this.comicDatabaseService.save(comic);
      return this.comicDatabaseService.getImageUrl(comicId, page);
    } else {
      return Promise.resolve(`/api/read/${ comicId }/${ page }`);
    }
  }

  /**
   * Retrieves bookmarks either from API or from indexedDB.
   */
  getBookmarks (): Promise<Comic[]> {
    return new Promise((resolve, reject) => {
      this.comicsService.listLastReadByVolume().subscribe((comics: Comic[]) => {
        if (this.queueService.hasItems()) {
          this.queueService.process().subscribe(
            () => {},
            () => resolve(comics),
            () => {
              this.comicsService.listLastReadByVolume().subscribe((updatedComics: Comic[]) => {
                resolve(updatedComics);
              });
          });
        } else {
          resolve(comics);
        }
      }, () => {
        this.comicDatabaseService.getComics().then((comics: Comic[]) => {
          // Pick latest unread by volume
          from(comics.sort(comic => comic.number)).pipe(
            groupBy(comic => `${comic.publisher}|${comic.series}|${comic.volume}`),
            mergeMap(group => group.pipe(
              filter(comic => !comic.read),
              first(),
              toArray()
            ))
          ).subscribe(resolve, reject, () => resolve(comics));
        }, () => reject());
      });
    });
  }

  /**
   * Caches the previous, the current and the three next comic books.
   * @param comicId The reference comic ID.
   */
  async storeSurrounding (comicId: string): Promise<void> {
    const cachedIds = [];
    const comic = await this.get(comicId);

    // Store the previous comic.
    if (comic.previousId !== null) {
      const previousComic = await this.get(comic.previousId);
      await this.comicDatabaseService.store(previousComic);
      cachedIds.push(previousComic.id);
    }

    // Store the current comic.
    await this.comicDatabaseService.store(comic);
    cachedIds.push(comic.id);

    // Store the next three comics.
    let nextComic: Comic = comic;
    for (const {} of new Array(3)) {
      if (nextComic.nextId !== null) {
        nextComic = await this.get(nextComic.nextId);
        await this.comicDatabaseService.store(nextComic);
        cachedIds.push(nextComic.id);
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
        cachedIds.indexOf(cachedComic.id) === -1;
    });
    comicsToDelete.forEach((comicToDelete) => {
      this.comicDatabaseService.delete(comicToDelete);
    });
  }

  /**
   * Deletes all comics in the given comic's volume.
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
