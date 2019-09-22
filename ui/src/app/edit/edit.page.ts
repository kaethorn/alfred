import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ComicsService } from '../comics.service';
import { Comic } from '../comic';
import { FormBuilder, Validators } from '@angular/forms';

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
  });

  constructor (
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private formBuilder: FormBuilder,
  ) { }

  ionViewDidEnter () {
    this.comicsService
      .get(this.route.snapshot.params.id)
      .subscribe((data: Comic) => {
        this.comic = data;
        this.comicForm.get('series').setValue(data.series);
        this.comicForm.get('publisher').setValue(data.publisher);
        this.comicForm.get('volume').setValue(data.volume);
        this.comicForm.get('number').setValue(data.number);
      });
  }
}
