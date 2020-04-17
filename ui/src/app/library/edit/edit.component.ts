import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ToastController } from '@ionic/angular';

import { Comic } from '../../comic';
import { ComicsService } from '../../comics.service';

@Component({
  selector: 'app-edit',
  styleUrls: [ './edit.component.sass' ],
  templateUrl: './edit.component.html'
})
export class EditComponent {

  public comic: Comic;
  public comicForm = this.formBuilder.group({
    characters: [ '' ],
    colorist: [ '' ],
    coverArtist: [ '' ],
    editor: [ '' ],
    inker: [ '' ],
    letterer: [ '' ],
    locations: [ '' ],
    manga: [ '' ],
    month: [ '' ],
    notes: [ '' ],
    number: [ '', Validators.required ],
    penciller: [ '' ],
    publisher: [ '', Validators.required ],
    series: [ '', Validators.required ],
    summary: [ '' ],
    teams: [ '' ],
    title: [ '' ],
    volume: [ '', Validators.required ],
    writer: [ '' ],
    year: [ '' ]
  });
  public scrapeInProgress = false;
  public editInProgress = false;

  constructor(
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private formBuilder: FormBuilder,
    private toastController: ToastController
  ) { }

  public ionViewDidEnter(): void {
    this.get(this.route.snapshot.params.id);
  }

  public onSubmit(): void {
    this.editInProgress = true;
    Object.keys(this.comicForm.value).forEach(key => {
      this.comic[key] = this.comicForm.value[key];
    });
    this.comicsService.update(this.comic).subscribe(
      () => {
        this.editInProgress = false;
        this.showToast('Comic saved.');
      },
      () => {
        this.editInProgress = false;
        this.showToast('Error saving comic.', 4000);
      }
    );
  }

  public scrape(): void {
    this.scrapeInProgress = true;
    this.comicsService.scrape(this.comic).subscribe(
      () => {
        this.scrapeInProgress = false;
        this.get(this.comic.id);
        this.showToast('Comic scraped.');
      },
      () => {
        this.scrapeInProgress = false;
        this.showToast('Error scraping comic.', 4000);
      }
    );
  }

  private async showToast(message: string, duration = 3000): Promise<void> {
    const toast = await this.toastController.create({
      duration,
      message
    });
    toast.present();
  }

  private get(id: string): void {
    this.comicsService
      .get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        Object.keys(this.comicForm.value).forEach(key => {
          this.comicForm.get(key).setValue(data[key]);
        });
      });
  }
}
