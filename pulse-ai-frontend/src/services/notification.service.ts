import { api } from './api';

export interface Notification {
  id: number;
  title: string;
  message: string;
  isRead: boolean;
  read: boolean;
  notificationType: string;
  createdAt: string;
  sentAt: string;
}

export const getMyNotifications = async (): Promise<Notification[]> => {
  const response = await api.get('/notifications/me');
  return response.data;
};

export const getUnreadCount = async (): Promise<number> => {
  const response = await api.get('/notifications/unread-count');
  return response.data;
};

export const markAsRead = async (id: number): Promise<void> => {
  await api.put(`/notifications/${id}/read`);
};
