export interface ReviewResponse {
  id: number;
  title: string;
  description: string;
  author: string;
  publicationDate: string;
  lastEditedDate: string;
  accepted: boolean;
  rejectionReason: string;
}
