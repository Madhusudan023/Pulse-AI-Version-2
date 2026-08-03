import React, { useState, useEffect } from 'react';
import { 
  Badge, 
  IconButton, 
  Menu, 
  MenuItem, 
  Typography, 
  Box, 
  CircularProgress,
  Divider
} from '@mui/material';
import { Bell, Check, Circle } from 'lucide-react';
import { alpha, useTheme } from '@mui/material/styles';
import { getMyNotifications, getUnreadCount, markAsRead } from '../../services/notification.service';
import type { Notification } from '../../services/notification.service';

const formatTimeAgo = (dateStr: string) => {
  if (!dateStr) return 'Recently';
  const diff = Date.now() - new Date(dateStr).getTime();
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return 'Just now';
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
};

export const NotificationMenu: React.FC = () => {
  const theme = useTheme();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);

  const open = Boolean(anchorEl);

  const fetchUnreadCount = async () => {
    try {
      const count = await getUnreadCount();
      setUnreadCount(count);
    } catch (error) {
      console.error('Failed to fetch unread count:', error);
    }
  };

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const data = await getMyNotifications();
      setNotifications(data);
      // Update unread count based on fetched data as a fallback/sync
      const unread = data.filter(n => !n.isRead && !n.read).length;
      setUnreadCount(unread);
    } catch (error) {
      console.error('Failed to fetch notifications:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnreadCount();
    // Poll for notifications every minute
    const interval = setInterval(fetchUnreadCount, 60000);
    return () => clearInterval(interval);
  }, []);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
    fetchNotifications();
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await markAsRead(id);
      // Optimistically update local state
      setNotifications(prev => 
        prev.map(n => n.id === id ? { ...n, isRead: true, read: true } : n)
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  };

  return (
    <>
      <IconButton 
        size="small" 
        onClick={handleClick}
        sx={{ 
          bgcolor: alpha(theme.palette.primary.main, 0.1), 
          color: 'primary.main',
          borderRadius: 2,
          '&:hover': { bgcolor: alpha(theme.palette.primary.main, 0.15) }
        }}
      >
        <Badge 
          badgeContent={unreadCount} 
          color="error" 
          variant={unreadCount > 0 ? "dot" : "standard"}
          sx={{
            '& .MuiBadge-badge': {
              top: 2,
              right: 2,
            }
          }}
        >
          <Bell size={20} />
        </Badge>
      </IconButton>

      <Menu
        anchorEl={anchorEl}
        id="notification-menu"
        open={open}
        onClose={handleClose}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        slotProps={{
          paper: {
            elevation: 3,
            sx: {
              mt: 1.5,
              width: 320,
              maxHeight: 400,
              overflow: 'auto',
              borderRadius: 2,
            }
          }
        }}
      >
        <Box sx={{ px: 2, py: 1.5, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="subtitle1" fontWeight={700}>
            Notifications
          </Typography>
          {unreadCount > 0 && (
            <Typography variant="caption" color="primary.main" fontWeight={600}>
              {unreadCount} new
            </Typography>
          )}
        </Box>
        <Divider />
        
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
            <CircularProgress size={24} />
          </Box>
        ) : notifications.length === 0 ? (
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              No notifications yet.
            </Typography>
          </Box>
        ) : (
          notifications.map((notification) => {
            const isUnread = !notification.isRead && !notification.read;
            return (
              <MenuItem 
                key={notification.id} 
                onClick={() => {
                  if (isUnread) handleMarkAsRead(notification.id);
                }}
                sx={{ 
                  py: 1.5, 
                  px: 2,
                  whiteSpace: 'normal',
                  bgcolor: isUnread ? alpha(theme.palette.primary.main, 0.04) : 'transparent',
                  borderLeft: isUnread ? `3px solid ${theme.palette.primary.main}` : '3px solid transparent',
                  alignItems: 'flex-start',
                  gap: 1.5
                }}
              >
                <Box sx={{ mt: 0.5 }}>
                  {isUnread ? (
                    <Circle size={10} color={theme.palette.primary.main} fill={theme.palette.primary.main} />
                  ) : (
                    <Check size={14} color={theme.palette.text.secondary} />
                  )}
                </Box>
                <Box sx={{ flex: 1 }}>
                  <Typography variant="body2" fontWeight={isUnread ? 600 : 400} sx={{ mb: 0.5, color: 'text.primary' }}>
                    {notification.title || notification.notificationType}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" display="block" sx={{ mb: 1, lineHeight: 1.3 }}>
                    {notification.message}
                  </Typography>
                  <Typography variant="caption" color="text.disabled" sx={{ fontSize: '0.7rem' }}>
                    {formatTimeAgo(notification.createdAt || notification.sentAt)}
                  </Typography>
                </Box>
              </MenuItem>
            );
          })
        )}
      </Menu>
    </>
  );
};
