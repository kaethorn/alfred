import { Comic } from './comic';
import { Injectable } from '@angular/core';
import { ComicDatabaseService } from './comic-database.service';
import { ComicsService } from './comics.service';

@Injectable({
  providedIn: 'root'
})
export class ComicStorageService {

  private isStored = false;
  private comic: Promise<Comic>;
  private comicId: string;

  constructor (
    private db: ComicDatabaseService,
    private comicsService: ComicsService,
  ) { }

  set (comicId: string): Promise<Comic> {
    this.comicId = comicId;
    return this.comic = new Promise(resolve => {
      this.db.isStored(comicId).then((isStored) => {
        this.isStored = isStored;
        if (this.isStored) {
          this.db.getComic(comicId).then((comic: Comic) => resolve(comic));
        } else {
          this.comicsService.get(comicId).subscribe((comic: Comic) => resolve(comic));
        }
      });
    });
  }

  get (): Promise<Comic> {
    return this.comic;
  }

  /**
   * Reads the given page of this comic, setting the read state.
   * @param page Page number
   * @returns The URL to the image.
   */
  async readPage (page: number): Promise<string> {
    if (this.isStored) {
      const comic = await this.comic;
      comic.currentPage = page;
      // TODO
      // Attempt to save the comic to the API, too. On failure, store the
      // last state per comic in a queue. When calling `this.getBookmarks`
      // and the API request succeeds, process the queue by saving each
      // comic it contains and then repeat the bookmarks API call.
      await this.db.update(comic);
      return this.db.getImageUrl(this.comicId, page);
    } else {
      return Promise.resolve(`/api/read/${ this.comicId }/${ page }`);
    }
  }

  /**
   * Retrieves bookmarks either from API or from indexedDB.
   */
  getBookmarks (): Promise<Comic[]> {
    return new Promise((resolve, reject) => {
      this.comicsService.listLastReadByVolume().subscribe((comics: Comic[]) => {
        resolve(comics);
      }, () => {
        this.db.getComics().then((comics: Comic[]) => {
          resolve(comics);
        }, () => reject());
      });
    });
  }
}
