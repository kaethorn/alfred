import { Publisher } from '../app/publisher';

import { SeriesFixtures } from './series.fixtures';

export class PublisherFixtures {

  public static get publisher(): Publisher {
    const publisher: Publisher = {} as Publisher;
    publisher.name = 'DC Comics';
    publisher.seriesCount = 4;
    publisher.series = [
      SeriesFixtures.series,
      Object.assign(SeriesFixtures.series, { name: 'Batgirl' }),
      Object.assign(SeriesFixtures.series, { name: 'Green Lantern' }),
      Object.assign(SeriesFixtures.series, { name: 'Flash' })
    ];

    return publisher;
  }

  public static get publishers(): Publisher[] {
    return [
      this.publisher,
      Object.assign(this.publisher, {
        name: 'Marvel Comics',
        seriesCount: 2,
        series: [
          Object.assign(SeriesFixtures.series, { name: 'Wolverine' }),
          Object.assign(SeriesFixtures.series, { name: 'Spider-Man' })
        ]
      }),
      Object.assign(this.publisher, {
        name: 'Dark Horse Comics',
        seriesCount: 2,
        series: [
          Object.assign(SeriesFixtures.series, { name: 'Aliens' }),
          Object.assign(SeriesFixtures.series, { name: 'Star Wars' })
        ]
      }),
      Object.assign(this.publisher, {
        name: 'Aspen Comics',
        seriesCount: 2,
        series: [
          Object.assign(SeriesFixtures.series, { name: 'Fathom' }),
          Object.assign(SeriesFixtures.series, { name: 'Ekos' })
        ]
      }),
      Object.assign(this.publisher, {
        name: 'Top Cow Productions',
        seriesCount: 3,
        series: [
          Object.assign(SeriesFixtures.series, { name: 'Witchblade' }),
          Object.assign(SeriesFixtures.series, { name: 'The Darkness' }),
          Object.assign(SeriesFixtures.series, { name: 'Magdalena' })
        ]
      })
    ];
  }
}
