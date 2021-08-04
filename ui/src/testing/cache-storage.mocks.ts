export class CacheStorageMocks {

  public static get cache(): jasmine.SpyObj<Cache> {
    return jasmine.createSpyObj('Cache', {
      delete: Promise.resolve(true),
      keys: Promise.resolve([{
        url: 'https://alfred/api/thumbnails/front-cover/922'
      }, {
        url: 'https://alfred/api/thumbnails/back-cover/922'
      }, {
        url: 'https://alfred/api/thumbnails/front-cover/923'
      }, {
        url: 'https://alfred/api/thumbnails/back-cover/923'
      }])
    });
  }

  public static get cacheStorage(): jasmine.SpyObj<CacheStorage> {
    return jasmine.createSpyObj('CacheStorage', {
      keys: Promise.resolve([
        'ngsw:/:db:control',
        'ngsw:/:f884fffcbbe57a42433279dd93c09cc4a1ec40a9:assets:app:cache',
        'ngsw:/:db:ngsw:/:f884fffcbbe57a42433279dd93c09cc4a1ec40a9:assets:app:meta',
        'ngsw:/:f884fffcbbe57a42433279dd93c09cc4a1ec40a9:assets:assets:cache',
        'ngsw:/:db:ngsw:/:f884fffcbbe57a42433279dd93c09cc4a1ec40a9:assets:assets:meta',
        'ngsw:/:61688a881b0321f73a8cc66daaea8e8f5f6ec377:assets:app:cache',
        'ngsw:/:c13bc7465cf7b0e35bd5b8a7e90983870fcc6229:assets:app:cache',
        'ngsw:/:4c5b1da76fb12d7eda1ad175ad27d8b5fc12df92:assets:app:cache',
        'ngsw:/:1:data:dynamic:user-api:cache',
        'ngsw:/:db:ngsw:/:1:data:dynamic:user-api:lru',
        'ngsw:/:db:ngsw:/:1:data:dynamic:user-api:age',
        'ngsw:/:1:data:dynamic:library-api:cache',
        'ngsw:/:db:ngsw:/:1:data:dynamic:library-api:lru',
        'ngsw:/:db:ngsw:/:1:data:dynamic:library-api:age',
        'ngsw:/:1:data:dynamic:thumbnails-api:cache',
        'ngsw:/:db:ngsw:/:1:data:dynamic:thumbnails-api:lru',
        'ngsw:/:db:ngsw:/:1:data:dynamic:thumbnails-api:age',
        'ngsw:/:1:data:dynamic:no-cache:cache',
        'ngsw:/:db:ngsw:/:1:data:dynamic:no-cache:lru',
        'ngsw:/:db:ngsw:/:1:data:dynamic:no-cache:age'
      ]),
      open: Promise.resolve(this.cache)
    });
  }
}
