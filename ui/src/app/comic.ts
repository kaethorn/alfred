export interface Comic {
  id: string;
  path: string;
  title: string;
  series: string;
  number: number;
  position: string;
  volume: string;
  summary?: string;
  notes?: string;
  year: number;
  month: number;
  writer?: string;
  penciller?: string;
  inker?: string;
  colorist?: string;
  letterer?: string;
  cover_artist?: string;
  editor?: string;
  publisher: string;
  web?: string;
  pageCount: number;
  manga?: boolean;
  characters?: string;
  teams?: string;
  locations?: string;
  thumbnail: string;
  nextId?: string;
  previousId?: string;
  read?: boolean;
  currentPage?: number;
  lastRead?: Date;
}
