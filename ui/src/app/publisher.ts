import { Series } from './series';

export interface Publisher {
  name: string;
  seriesCount: number;
  series?: Series[];
}
