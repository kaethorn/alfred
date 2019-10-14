import { Comic } from './comic';
import { Injectable } from '@angular/core';
import { ComicDatabaseService } from './comic-database.service';
import { ComicsService } from './comics.service';
import { QueueService } from './queue.service';

@Injectable({
  providedIn: 'root'
})
export class ComicStorageService {

  constructor (
    private comicDatabaseService: ComicDatabaseService,
    private comicsService: ComicsService,
    private queue: QueueService,
  ) {
    this.queue.process();
  }

  get (comicId: string): Promise<Comic> {
    return this.comicDatabaseService.isStored(comicId).then((isStored) => {
      if (isStored) {
        return this.comicDatabaseService.getComic(comicId);
      }
      return this.comicsService.get(comicId).toPromise();
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
      this.saveComic(comic);
      await this.comicDatabaseService.update(comic);
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
        if (this.queue.hasItems()) {
          this.queue.process().subscribe(
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
          const group: { [name: string]: Comic[] } = comics.reduce((result, comic) => {
            const key = `${comic.publisher}|${comic.series}|${comic.volume}`;
            (result[key] = result[key] || []).push(comic);
            return result;
          }, {});

          const lastUnread: Comic[] = Object.keys(group).reduce((result, key) => {
            result.push(group[key]
              .sort((comic) => comic.number)
              .find((comic: Comic) => !comic.read));
            return result;
          }, []);

          resolve(lastUnread);
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
    if (typeof comic.previousId !== 'undefined') {
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
      if (typeof nextComic.nextId !== 'undefined') {
        nextComic = await this.get(nextComic.nextId);
        await this.comicDatabaseService.store(nextComic);
        cachedIds.push(nextComic.id);
      } else {
        break;
      }
    }

    // Traverse indexedDb's Comic collection for comic books outside
    // that range and delete them.
    await this.comicDatabaseService.getComics().then((cachedComics) => {
      const comicsToDelete = cachedComics.filter((cachedComic) => {
        // Filter out comics from other volumes:
        return comic.publisher === cachedComic.publisher &&
        comic.series === cachedComic.series &&
        comic.volume === cachedComic.volume &&
        // Filter out comics that are not cached
        cachedIds.indexOf(cachedComic.id) === -1;
      });
      comicsToDelete.forEach((comicToDelete) => {
        this.comicDatabaseService.delete(comicToDelete);
      });
    });

    return;
  }

  private saveComic (comic: Comic) {
    this.comicsService.update(comic).subscribe(
      () => this.queue.process(),
      () => this.queue.add(comic));
  }
}
