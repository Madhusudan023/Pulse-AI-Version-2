package com.pulseai.notificationservice.controller;

import com.pulseai.notificationservice.entity.Notification;
import com.pulseai.notificationservice.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void testGetMyNotifications_ReturnsList() {
        when(request.getAttribute("employeeId")).thenReturn(1L);
        Notification notification = new Notification();
        notification.setId(10L);
        notification.setRecipientId(1L);

        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.singletonList(notification));

        ResponseEntity<List<Notification>> response = notificationController.getMyNotifications(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(10L, response.getBody().get(0).getId());
    }

    @Test
    void testGetMyNotifications_Empty() {
        when(request.getAttribute("employeeId")).thenReturn(2L);
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(2L))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<Notification>> response = notificationController.getMyNotifications(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetUnreadCount() {
        when(request.getAttribute("employeeId")).thenReturn(1L);
        when(notificationRepository.countByRecipientIdAndIsReadFalse(1L)).thenReturn(5L);

        ResponseEntity<Long> response = notificationController.getUnreadCount(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody());
    }

    @Test
    void testMarkAsRead_Success() {
        when(request.getAttribute("employeeId")).thenReturn(1L);
        Notification n = new Notification();
        n.setId(100L);
        n.setRecipientId(1L);
        n.setRead(false);

        when(notificationRepository.findById(100L)).thenReturn(Optional.of(n));

        ResponseEntity<Void> response = notificationController.markAsRead(100L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(n.isRead());
        verify(notificationRepository, times(1)).save(n);
    }

    @Test
    void testMarkAsRead_NotFound() {
        when(request.getAttribute("employeeId")).thenReturn(1L);
        when(notificationRepository.findById(200L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = notificationController.markAsRead(200L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testMarkAsRead_UnauthorizedUser() {
        when(request.getAttribute("employeeId")).thenReturn(1L); // requester is user 1
        Notification n = new Notification();
        n.setId(100L);
        n.setRecipientId(2L); // notification belongs to user 2
        n.setRead(false);

        when(notificationRepository.findById(100L)).thenReturn(Optional.of(n));

        ResponseEntity<Void> response = notificationController.markAsRead(100L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(n.isRead());
        verify(notificationRepository, never()).save(any());
    }
}
