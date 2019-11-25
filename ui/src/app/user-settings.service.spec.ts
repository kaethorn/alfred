import { TestBed } from '@angular/core/testing';

import { UserSettingsService } from './user-settings.service';

describe('UserSettingsService', () => {
  let service: UserSettingsService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
    });
    service = TestBed.get(UserSettingsService);
  });

  afterEach(() => {
    localStorage.clear();
    TestBed.resetTestingModule();
  });

  describe('#constructor', () => {

    it('loads empty user settings', () => {
      expect(service.get()).toBeDefined();
    });

    it('determines dark mode user preference', () => {
      expect(service.get().darkMode).toBeDefined();
    });

    describe('with existing settings', () => {

      beforeEach(() => {
        localStorage.setItem('userSettings', JSON.stringify({ foo: 'bar' }));
        service.load();
      });

      it('loads user settings', () => {
        expect(service.get().foo).toEqual('bar');
      });
    });

    describe('dark mode', () => {

      it('overrides user preference', () => {
        localStorage.setItem('userSettings', JSON.stringify({ darkMode: false }));
        service.load();
        expect(service.get().darkMode).toEqual(false);

        localStorage.setItem('userSettings', JSON.stringify({ darkMode: true }));
        service.load();
        expect(service.get().darkMode).toEqual(true);
      });
    });
  });

  describe('#save', () => {

    it('persists user settings', () => {
      expect(localStorage.getItem('userSettings')).toBeNull();

      service.get().foo = 'bar';
      service.save();

      expect(localStorage.getItem('userSettings')).toBeDefined();
      expect(JSON.parse(localStorage.getItem('userSettings')).foo).toEqual('bar');
    });
  });
});
