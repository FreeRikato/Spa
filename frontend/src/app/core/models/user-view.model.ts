export interface UserView {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: 'USER' | 'CLIENT' | 'ADMIN';
  membershipName: string | null;
  membershipStatus: 'ACTIVE' | 'PENDING' | 'REJECTED' | 'INACTIVE' | null;
}
