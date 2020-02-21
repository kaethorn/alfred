import { SafeUrl } from '@angular/platform-browser';

export enum ThumbnailType {
  FRONT_COVER,
  BACK_COVER
}

export interface Thumbnail {
  id: string;
  comicId: string;
  type: ThumbnailType;
  image: string;
  path: string;
  url?: SafeUrl;
}
