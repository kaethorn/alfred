import { Publisher } from '../app/publisher';

export class PublisherFixtures {

  public static get publisher(): Publisher {
    const publisher: Publisher = {} as Publisher;
    publisher.name = 'DC Comics';
    publisher.seriesCount = 4;
    publisher.series = [];

    return publisher;
  }
}
