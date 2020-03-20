import { ComicFixtures } from '../testing/comic.fixtures';
import { IndexedDbMocks } from '../testing/indexed-db.mock';

import { IndexedDb } from './indexed-db';

let service: IndexedDb;

fdescribe('IndexedDb', () => {

  beforeEach(async () => {
    service = new IndexedDb('Comics', 1, [{
      name: 'Images',
      options: { autoIncrement: true }
    }, {
      name: 'Comics',
      options: { keyPath: 'id' },
      indices: [
        [ 'id', 'id', { unique: true }],
        [ 'dirty', 'dirty', { unique: false }]
      ]
    }], IndexedDbMocks.create);

    await service.ready.toPromise();
  });

  afterEach(() => {
    IndexedDbMocks.reset();
  });

  describe('#hasKey', () => {

    describe('without a db', () => {

      beforeEach(() => {
        service = new IndexedDb('Comics', 1, [{
          name: 'Images',
          options: { autoIncrement: true }
        }, {
          name: 'Comics',
          options: { keyPath: 'id' },
          indices: [
            ['id', 'id', { unique: true }],
            ['dirty', 'dirty', { unique: false }]
          ]
        }]);
      });

      it('resolves to `false`', async () => {
        const result = await service.hasKey('Comics', ComicFixtures.comic.id);
        expect(result).toBeFalse();
      });
    });

    describe('without a matching key', () => {

      it('resolves to `false`', async () => {
        const result = await service.hasKey('Comics', ComicFixtures.comic.id);
        expect(result).toBeFalse();
      });
    });

    describe('with a matching key', () => {

      beforeEach(async () => {
        await service.save('Comics', ComicFixtures.comic);
      });

      it('resolves to `true`', async () => {
        const result = await service.hasKey('Comics', ComicFixtures.comic.id);
        expect(result).toBeTrue();
      });
    });
  });

  describe('#get', () => {

    describe('without a matching item', () => {

      it('rejects', async () => {
        try {
          await service.get('Comics', ComicFixtures.comic.id);
          expect(false).toBeTrue();
        } catch (exception) {
          expect(true).toBeTrue();
        }
      });
    });

    describe('with a matching item', () => {

      beforeEach(async () => {
        await service.save('Comics', ComicFixtures.comic);
      });

      it('resolves with the item', async () => {
        const result = await service.get('Comics', ComicFixtures.comic.id);
        expect(result).toEqual(ComicFixtures.comic);
      });
    });
  });
});
