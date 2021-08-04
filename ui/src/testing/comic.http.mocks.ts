export class ComicHttpMocks {

  public static get comic(): Object {
    return {
      _links: {
        self: {
          href: 'http://localhost:4200/api/comics/923'
        }
      },
      characters: 'Barbara Gordon, Batman, Cassandra Cain, David Cain',
      colorist: 'Jamison, Jason Wright',
      coverArtist: 'Damion Scott, Robert Campanella',
      currentPage: 0,
      editor: 'Darren Vincenzo, Joseph Illidge',
      errors: null,
      fileName: 'Batgirl 001 (2000).cbz',
      files: [
        '/1.png',
        '/ComicInfo.xml'
      ],
      id: '923',
      inker: 'Robert Campanella',
      lastRead: '2020-03-15T20:01:56.692+0000',
      letterer: 'John Costanza',
      locations: 'Clocktower, Gotham City',
      manga: false,
      month: 4,
      nextId: '924',
      notes: 'Scraped metadata from ComicVine [CVDB75503].',
      number: '1',
      pageCount: 1,
      path: '/Batgirl 001 (2000).cbz',
      penciller: 'Damion Scott',
      position: '0001.0',
      previousId: null,
      publisher: 'DC Comics',
      read: true,
      series: 'Batgirl',
      summary: 'Flashback to years ago.',
      teams: '',
      title: '[untitled]',
      valid: true,
      volume: '2000',
      web: 'https://comicvine.gamespot.com/batgirl-1-untitled/4000-75503/',
      writer: 'Kelley Puckett, Scott Peterson',
      year: 2000
    };
  }

  public static get comics(): Object {
    return {
      _embedded: {
        comics: [{
          _links: {
            self: {
              href: 'foo.bar/1'
            }
          },
          month: 4,
          number: 1,
          pageCount: 20,
          path: '/batman 1.cbz',
          position: '1',
          publisher: 'DC Comics',
          series: 'Batman',
          title: 'Batman One',
          volume: '1940',
          year: 1940
        }, {
          _links: {
            self: {
              href: 'foo.bar/2'
            }
          },
          month: 5,
          number: 2,
          pageCount: 20,
          path: '/batman 2.cbz',
          position: '2',
          publisher: 'DC Comics',
          series: 'Batman',
          title: 'Batman Two',
          volume: '1940',
          year: 1940
        }]
      }
    };
  }
}
