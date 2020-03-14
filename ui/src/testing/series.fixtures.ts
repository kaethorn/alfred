import { Series } from '../app/series';

export class SeriesFixtures {

  public static get series(): Series {
    const series: Series = {} as Series;
    series.name = 'Batgirl';
    series.publisher = 'DC Comics';
    series.volumesCount = 7;

    return series;
  }
}
