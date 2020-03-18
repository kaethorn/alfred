export class StatsHttpMocks {

  public static get stats(): Object {
    return {
      _links: {
        self: {
          href: 'http://localhost:4200/api/stats'
        }
      },
      issues: 305,
      publishers: 3,
      series: 5,
      users: 1,
      volumes: 9
    };
  }
}
