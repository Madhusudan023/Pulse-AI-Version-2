import { useQuery } from '@tanstack/react-query';
import { getUnreadCount } from '../services/notification.service';

export const useUnreadCount = () => {
  return useQuery<number, Error>({
    queryKey: ['unreadCount'],
    queryFn: getUnreadCount,
  });
};
