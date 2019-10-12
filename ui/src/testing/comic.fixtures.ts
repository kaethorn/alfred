import { Comic } from '../app/comic';

const comic1: Comic = {} as Comic;
comic1.pageCount = 5;
comic1.number = 402;
comic1.id = '923';
comic1.title = 'There\'s Nothing So Savage as a Man Destroying Himself';
comic1.series = 'Batman';
comic1.volume = '1940';
comic1.publisher = 'DC Comics';

export { comic1 };

const comics: Comic[] = [
  Object.assign({ id: '1', nextId: '2' }, comic1),
  Object.assign({ id: '2', nextId: '3', previousId: '1' }, comic1),
  Object.assign({ id: '3', nextId: '4', previousId: '2' }, comic1),
  Object.assign({ id: '4', nextId: '5', previousId: '3' }, comic1),
  Object.assign({ id: '5', nextId: '6', previousId: '4' }, comic1),
  Object.assign({ id: '6', nextId: '7', previousId: '5' }, comic1),
  Object.assign({ id: '7', nextId: '8', previousId: '6' }, comic1),
  Object.assign({ id: '8', previousId: '7' }, comic1)
].map((comic: Comic, index: number) => {
  comic.id = `${ index + 1 }`;
  return comic;
});

export { comics as volume };
