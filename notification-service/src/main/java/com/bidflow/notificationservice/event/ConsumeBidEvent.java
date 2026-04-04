package com.bidflow.notificationservice.event;

import org.springframework.kafka.annotation.KafkaListener;

public class ConsumeBidEvent {
    @KafkaListener(topics = "bid-events", groupId = "notification-group")
    public void consume(BidEvent event) {
        System.out.println("Received bid event: " + event.getAmount());
        System.out.println("Received for auction: " + event.getAuctionId());
        System.out.println("Received from user: " + event.getUserId());
    }
}
