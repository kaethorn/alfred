export class VolumeHttpMocks {

  public static get volumes(): Object {
    return {
      _embedded: {
        volumes: [
          {
            firstComicId: '5e69fe03d6bd090e92202bd9',
            id: '2000',
            issueCount: 73,
            name: '2000',
            publisher: 'DC Comics',
            read: false,
            readCount: 0,
            series: 'Batgirl'
          },
          {
            firstComicId: '5e69fe05d6bd090e92202eca',
            id: '2008',
            issueCount: 6,
            name: '2008',
            publisher: 'DC Comics',
            read: false,
            readCount: 2,
            series: 'Batgirl'
          },
          {
            firstComicId: '5e69fe05d6bd090e92202de9',
            id: '2009',
            issueCount: 24,
            name: '2009',
            publisher: 'DC Comics',
            read: false,
            readCount: 0,
            series: 'Batgirl'
          },
          {
            firstComicId: '5e69fe05d6bd090e92202e9d',
            id: '2011',
            issueCount: 53,
            name: '2011',
            publisher: 'DC Comics',
            read: false,
            readCount: 0,
            series: 'Batgirl'
          },
          {
            firstComicId: '5e69fe03d6bd090e92202c39',
            id: '2016',
            issueCount: 6,
            name: '2016',
            publisher: 'DC Comics',
            read: false,
            readCount: 0,
            series: 'Batgirl'
          }
        ]
      },
      _links: {
        self: {
          href: 'http://localhost:4200/api/publishers/DC%20Comics/series/Batgirl/volumes'
        }
      }
    };
  }

  public static get series(): Object {
    return {
      _embedded: {
        series: [
          {
            _links: {
              self: {
                href: 'http://localhost:4200/api/publishers/DC%20Comics/series/Batgirl/volumes'
              }
            },
            id: 'Batgirl',
            name: 'Batgirl',
            publisher: 'DC Comics',
            volumesCount: 5
          },
          {
            _links: {
              self: {
                href: 'http://localhost:4200/api/publishers/DC%20Comics/series/Batman/volumes'
              }
            },
            id: 'Batman',
            name: 'Batman',
            publisher: 'DC Comics',
            volumesCount: 1
          }
        ]
      },
      _links: {
        self: {
          href: 'http://localhost:4200/api/publishers/DC%20Comics/series'
        }
      }
    };
  }

  public static get publishers(): Object {
    return {
      _embedded: {
        publishers: [{
          _links: {
            self: {
              href: 'http://localhost:4200/api/publishers/DC%20Comics/series'
            }
          },
          id: 'DC Comics',
          name: 'DC Comics',
          series: [{
            id: '{"publisher": "DC Comics", "series": "Batgirl"}',
            name: 'Batgirl',
            publisher: 'DC Comics',
            volumesCount: 5
          }, {
            id: '{"publisher": "DC Comics", "series": "Batman"}',
            name: 'Batman',
            publisher: 'DC Comics',
            volumesCount: 1
          }],
          seriesCount: 2
        }, {
          _links: {
            self: {
              href: 'http://localhost:4200/api/publishers/F5%20Enteratinment/series'
            }
          },
          id: 'F5 Enteratinment',
          name: 'F5 Enteratinment',
          series: [{
            id: '{"publisher": "F5 Enteratinment", "series": "The Tenth"}',
            name: 'The Tenth',
            publisher: 'F5 Enteratinment',
            volumesCount: 1
          }, {
            id: '{"publisher": "F5 Enteratinment", "series": "The Tenth: Resurrected"}',
            name: 'The Tenth: Resurrected',
            publisher: 'F5 Enteratinment',
            volumesCount: 1
          }],
          seriesCount: 2
        }, {
          _links: {
            self: {
              href: 'http://localhost:4200/api/publishers/Top%20Cow/series'
            }
          },
          id: 'Top Cow',
          name: 'Top Cow',
          series: [
            {
              id: '{"publisher": "Top Cow", "series": "Rising Stars"}',
              name: 'Rising Stars',
              publisher: 'Top Cow',
              volumesCount: 1
            }
          ],
          seriesCount: 1
        }
        ]
      },
      _links: {
        self: {
          href: 'http://localhost:4200/api/publishers'
        }
      }
    };
  }
}
