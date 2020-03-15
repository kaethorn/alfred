import { Comic, ScannerIssue, ScannerIssueType, ScannerIssueSeverity } from '../app/comic';

export class ComicFixtures {

  public static get comic(): Comic {
    const comic: Comic = {} as Comic;
    comic.pageCount = 5;
    comic.number = 401;
    comic.id = '923';
    comic.title = 'There\'s Nothing So Savage as a Man Destroying Himself';
    comic.series = 'Batman';
    comic.volume = '1940';
    comic.publisher = 'DC Comics';
    comic.fileName = '401.cbz';

    return comic;
  }

  public static get volume(): Comic[] {
    return [
      Object.assign({}, this.comic, { id: '11', position: '401.0', nextId: '12' }),
      Object.assign({}, this.comic, { id: '12', position: '402.0', nextId: '13', previousId: '11' }),
      Object.assign({}, this.comic, { id: '13', position: '403.0', nextId: '14', previousId: '12' }),
      Object.assign({}, this.comic, { id: '14', position: '404.0', nextId: '15', previousId: '13' }),
      Object.assign({}, this.comic, { id: '15', position: '405.0', nextId: '16', previousId: '14' }),
      Object.assign({}, this.comic, { id: '16', position: '406.0', nextId: '17', previousId: '15' }),
      Object.assign({}, this.comic, { id: '17', position: '407.0', nextId: '18', previousId: '16' }),
      Object.assign({}, this.comic, { id: '18', position: '408.0', previousId: '17' })
    ];
  }

  public static get volume2(): Comic[] {
    return [
      Object.assign({}, this.comic, { id: '21', series: 'Batgirl', volume: '2018', position: '1.0', nextId: '22' }),
      Object.assign({}, this.comic, { id: '22', series: 'Batgirl', volume: '2018', position: '2.0', nextId: '23', previousId: '21' }),
      Object.assign({}, this.comic, { id: '23', series: 'Batgirl', volume: '2018', position: '3.0', nextId: '24', previousId: '22' }),
      Object.assign({}, this.comic, { id: '24', series: 'Batgirl', volume: '2018', position: '4.0', nextId: '25', previousId: '23' }),
      Object.assign({}, this.comic, { id: '25', series: 'Batgirl', volume: '2018', position: '5.0', nextId: '26', previousId: '24' }),
      Object.assign({}, this.comic, { id: '26', series: 'Batgirl', volume: '2018', position: '6.0', nextId: '27', previousId: '25' }),
      Object.assign({}, this.comic, { id: '27', series: 'Batgirl', volume: '2018', position: '7.0', nextId: '28', previousId: '26' }),
      Object.assign({}, this.comic, { id: '28', series: 'Batgirl', volume: '2018', position: '8.0', previousId: '27' })
    ];
  }

  public static get volumeInProgress(): Comic[] {
    const volume: Comic[] = this.volume;
    volume[0].read = true;
    volume[1].read = true;

    return volume;
  }

  public static get volumesInProgress(): Comic[] {
    const volumeInProgress2: Comic[] = this.volume2;
    volumeInProgress2[0].read = true;

    return this.volumeInProgress.concat(volumeInProgress2);
  }

  public static get scannerIssueFixable(): ScannerIssue {
    return {
      date: new Date('2020-03-10T21:24:00'),
      type: ScannerIssueType.NOT_FLAT,
      message: 'Found directory entries in the archive.',
      fixable: true,
      severity: ScannerIssueSeverity.WARNING
    };
  }
}
