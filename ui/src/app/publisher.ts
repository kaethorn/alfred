export interface Series {
  series: string;
  volumes: string[];
}

export interface Publisher {
  publisher: string;
  series: Series[];
}
