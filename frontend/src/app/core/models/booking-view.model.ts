export interface BookingView {
  id: number;
  userId: number;
  spaId: number;
  serviceId: number;
  bookingDate: string;
  startTime: string;
  endTime: string;
  status: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';
  notes: string | null;
  createdDate: string;
  updatedDate: string;
}
