export interface PostResponse {
  id: number;
  title: string;
  description: string;
  author: string;
  publicationDate: string;
  lastEditedDate: string;
  draft: boolean;
  accepted: boolean;
  rejectionReason: string;
}
