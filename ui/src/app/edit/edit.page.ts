import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ToastController } from '@ionic/angular';

import { Comic } from '../comic';
import { ComicsService } from '../comics.service';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.page.html',
  styleUrls: ['./edit.page.sass']
})
export class EditPage {

  public comic: Comic;
  public comicForm = this.formBuilder.group({
    series: ['', Validators.required],
    publisher: ['', Validators.required],
    volume: ['', Validators.required],
    number: ['', Validators.required],
    year: [''],
    month: [''],
    title: [''],
    summary: [''],
    notes: [''],
    writer: [''],
    penciller: [''],
    inker: [''],
    colorist: [''],
    letterer: [''],
    coverArtist: [''],
    editor: [''],
    manga: [''],
    characters: [''],
    teams: [''],
    locations: ['']
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
        this.showToast('Error saving comic.');
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
        this.showToast('Error scraping comic.');
      }
    );
  }

  private async showToast(message: string, duration = 3000): Promise<void> {
    const toast = await this.toastController.create({
      message,
      duration
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
