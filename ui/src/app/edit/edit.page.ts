import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { ToastController } from '@ionic/angular';

import { ComicsService } from '../comics.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.page.html',
  styleUrls: ['./edit.page.sass'],
})
export class EditPage {

  comic: Comic;
  comicForm = this.formBuilder.group({
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
    locations: [''],
  });

  constructor (
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private formBuilder: FormBuilder,
    private toastController: ToastController,
  ) { }

  ionViewDidEnter () {
    this.get(this.route.snapshot.params.id);
  }

  private get (id: string) {
    this.comicsService
      .get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        Object.keys(this.comicForm.value).forEach(key => {
          this.comicForm.get(key).setValue(data[key]);
        });
      });
  }

  onSubmit () {
    Object.keys(this.comicForm.value).forEach(key => {
      this.comic[key] = this.comicForm.value[key];
    });
    this.comicsService.update(this.comic).subscribe(
      () => this.showToast('Comic saved.'),
      () => this.showToast('Error saving comic.')
    );
  }

  scrape () {
    this.comicsService.scrape(this.comic).subscribe(
      () => {
        this.get(this.comic.id);
        this.showToast('Comic scraped.');
      },
      () => this.showToast('Error scraping comic.')
    );
  }

  private async showToast (message: string, duration: number = 3000) {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }
}
