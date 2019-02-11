export interface Volume {
  volume: string;
  series: string;
  publisher: string;
  issueCount: number;
  readCount: number;
  read: boolean;
  thumbnail: string;
}

export interface Series {
  series: string;
  volumes: Volume[];
}

export interface Publisher {
  publisher: string;
  series: Series[];
}
