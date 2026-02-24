package com.nexus.shared.realtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeService {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishSaleEvent(Object payload) {
        log.debug("Publishing sale event to /topic/sales");
        messagingTemplate.convertAndSend("/topic/sales", payload);
    }

    public void publishAlertEvent(Object payload) {
        log.debug("Publishing alert event to /topic/alerts");
        messagingTemplate.convertAndSend("/topic/alerts", payload);
    }

    public void publishDashboardUpdate(Object payload) {
        messagingTemplate.convertAndSend("/topic/dashboard", payload);
    }
}
