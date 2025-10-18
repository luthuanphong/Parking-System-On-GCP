package com.gcp.practise.parking.concurent;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.dtos.BookingProcessing;

@Primary
@Component
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
