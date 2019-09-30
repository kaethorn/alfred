export enum ScannerIssueType {
  INFO,
  WARNING,
  ERROR
}

export interface ScannerIssue {
  date: Date;
  message: string;
  path: string;
  type: ScannerIssueType;
}

export interface Comic {
  id: string;
  path: string;
  fileName: string;
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
  nextId?: string;
  previousId?: string;
  read?: boolean;
  currentPage?: number;
  lastRead?: Date;
  errors?: ScannerIssue[];
}
