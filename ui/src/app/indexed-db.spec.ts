import { ComicFixtures } from '../testing/comic.fixtures';
import { IndexedDbMock, IndexedDbMockFlag } from '../testing/indexed-db.mock';

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
    }], IndexedDbMock.create);

    await service.ready.toPromise();
  });

  afterEach(() => {
    IndexedDbMock.reset();
  });

  describe('#constructor', () => {

    describe('on error', () => {

      beforeEach(() => {
        spyOn(console, 'error');
        IndexedDbMock.setFlag(IndexedDbMockFlag.OPEN_ERROR);
      });

      it('does not initialize', async () => {
        service = new IndexedDb('Comics', 1, [], IndexedDbMock.create);

        try {
          await service.ready.toPromise();
        } catch (exception) {
          expect(service.ready.hasError).toBeTrue();
          expect(service.ready.thrownError).toEqual('Error opening DB \'Comics\'.');
          expect(console.error).toHaveBeenCalledWith('Error opening DB \'Comics\'.', jasmine.any(Event));
        }
      });
    });
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

    describe('on transaction error', () => {

      beforeEach(() => {
        IndexedDbMock.setFlag(IndexedDbMockFlag.TRANSACTION_ERROR);
      });

      it('resolves to `false`', async () => {
        const result = await service.hasKey('Comics', ComicFixtures.comic.id);
        expect(result).toBeFalse();
      });
    });

    describe('on transaction abort', () => {

      beforeEach(() => {
        IndexedDbMock.setFlag(IndexedDbMockFlag.TRANSACTION_ABORT);
      });

      it('resolves to `false`', async () => {
        const result = await service.hasKey('Comics', ComicFixtures.comic.id);
        expect(result).toBeFalse();
      });
    });

    describe('on request error', () => {

      beforeEach(() => {
        IndexedDbMock.setFlag(IndexedDbMockFlag.REQUEST_ERROR);
      });

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

    describe('on transaction error', () => {

      beforeEach(() => {
        IndexedDbMock.setFlag(IndexedDbMockFlag.TRANSACTION_ERROR);
      });

      it('rejects', async () => {
        try {
          await service.get('Comics', ComicFixtures.comic.id);
          expect(false).toBeTrue();
        } catch (exception) {
          expect(true).toBeTrue();
        }
      });
    });

    describe('on transaction abort', () => {

      beforeEach(() => {
        IndexedDbMock.setFlag(IndexedDbMockFlag.TRANSACTION_ABORT);
      });

      it('rejects', async () => {
        try {
          await service.get('Comics', ComicFixtures.comic.id);
          expect(false).toBeTrue();
        } catch (exception) {
          expect(true).toBeTrue();
        }
      });
    });

    describe('on request error', () => {

      beforeEach(() => {
        IndexedDbMock.setFlag(IndexedDbMockFlag.REQUEST_ERROR);
      });

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
