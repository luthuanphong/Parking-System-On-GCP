package com.gcp.practise.parking.concurent;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.dtos.BookingProcessing;

@Primary
@Component
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "1")
public class BookingProcessingQueue {
    private ConcurrentLinkedQueue<BookingProcessing> queue = new ConcurrentLinkedQueue<>();

    public void addToQueue(BookingProcessing processing) {
        queue.add(processing);
    }

    public BookingProcessing getNextInQueue() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
