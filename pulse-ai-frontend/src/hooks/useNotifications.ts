import { useQuery } from '@tanstack/react-query';
import { getMyNotifications, type Notification } from '../services/notification.service';

export const useNotifications = () => {
  return useQuery<Notification[], Error>({
    queryKey: ['notifications'],
    queryFn: getMyNotifications,
  });
};
