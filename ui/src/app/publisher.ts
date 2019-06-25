import { Series } from './series';

export interface Publisher {
  publisher: string;
  seriesCount: number;
  series?: Series[];
}
