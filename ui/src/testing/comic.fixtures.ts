import { Comic, ScannerIssue, ScannerIssueType, ScannerIssueSeverity } from '../app/comic';

const comic1: Comic = {} as Comic;
comic1.pageCount = 5;
comic1.number = 401;
comic1.id = '923';
comic1.title = 'There\'s Nothing So Savage as a Man Destroying Himself';
comic1.series = 'Batman';
comic1.volume = '1940';
comic1.publisher = 'DC Comics';
comic1.fileName = '401.cbz';

export { comic1 };

const volume1: Comic[] = [
  Object.assign({}, comic1, { id: '11', position: '401.0', nextId: '12' }),
  Object.assign({}, comic1, { id: '12', position: '402.0', nextId: '13', previousId: '11' }),
  Object.assign({}, comic1, { id: '13', position: '403.0', nextId: '14', previousId: '12' }),
  Object.assign({}, comic1, { id: '14', position: '404.0', nextId: '15', previousId: '13' }),
  Object.assign({}, comic1, { id: '15', position: '405.0', nextId: '16', previousId: '14' }),
  Object.assign({}, comic1, { id: '16', position: '406.0', nextId: '17', previousId: '15' }),
  Object.assign({}, comic1, { id: '17', position: '407.0', nextId: '18', previousId: '16' }),
  Object.assign({}, comic1, { id: '18', position: '408.0', previousId: '17' })
];

const volume2: Comic[] = [
  Object.assign({}, comic1, { id: '21', series: 'Batgirl', volume: '2018', position: '1.0', nextId: '22' }),
  Object.assign({}, comic1, { id: '22', series: 'Batgirl', volume: '2018', position: '2.0', nextId: '23', previousId: '21' }),
  Object.assign({}, comic1, { id: '23', series: 'Batgirl', volume: '2018', position: '3.0', nextId: '24', previousId: '22' }),
  Object.assign({}, comic1, { id: '24', series: 'Batgirl', volume: '2018', position: '4.0', nextId: '25', previousId: '23' }),
  Object.assign({}, comic1, { id: '25', series: 'Batgirl', volume: '2018', position: '5.0', nextId: '26', previousId: '24' }),
  Object.assign({}, comic1, { id: '26', series: 'Batgirl', volume: '2018', position: '6.0', nextId: '27', previousId: '25' }),
  Object.assign({}, comic1, { id: '27', series: 'Batgirl', volume: '2018', position: '7.0', nextId: '28', previousId: '26' }),
  Object.assign({}, comic1, { id: '28', series: 'Batgirl', volume: '2018', position: '8.0', previousId: '27' })
];

export { volume1 as volume };

const volumeInProgress1: Comic[] = volume1;
volumeInProgress1[0].read = true;
volumeInProgress1[1].read = true;

export { volumeInProgress1 as volumeInProgress };

const volumeInProgress2: Comic[] = volume2;
volumeInProgress2[0].read = true;

const volumesInProgress: Comic[] = volumeInProgress1.concat(volumeInProgress2);

export { volumesInProgress };

const scannerIssueFixable: ScannerIssue = {
  date: new Date(),
  type: ScannerIssueType.NOT_FLAT,
  message: 'Found directory entries in the archive.',
  fixable: true,
  severity: ScannerIssueSeverity.WARNING
};

export { scannerIssueFixable };
