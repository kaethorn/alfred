export enum ScannerIssueSeverity {
  INFO,
  WARNING,
  ERROR
}

export enum ScannerIssueType {
  UNKNOWN,
  NOT_FLAT,
  NO_MONTH,
  NO_YEAR,
  NO_IMAGES,
  INVALID_FILE_FORMAT
}

export interface ScannerIssue {
  date: Date;
  type: ScannerIssueType;
  message: string;
  fixable: boolean;
  inProgress?: boolean;
  severity: ScannerIssueSeverity;
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
  coverArtist?: string;
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
  dirty?: number;
  errors?: ScannerIssue[];
  files: string[];
}
