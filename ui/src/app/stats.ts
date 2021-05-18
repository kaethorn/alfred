export interface Stats {
  lastScanFinished: Date | null;
  lastScanStarted: Date | null;
  issues: number;
  publishers: number;
  series: number;
  volumes: number;
  users: number;
}
