package com.ppd.client;

import com.ppd.server.controller.request.BookRequest;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client extends Thread {

    private static final String URL = "http://localhost:8080/ppd";
    private final Random random = new Random();
    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicBoolean running = new AtomicBoolean(true);

    private <T> T execute(Callable<T> callable) throws Exception {
        return callable.call();
    }

    public Boolean bookSeats(List<Integer> seats, long showID) throws Exception {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setSeats(seats);
        bookRequest.setEventID(showID);

        return execute(() -> restTemplate.postForObject(URL + "/book", bookRequest, Boolean.class));
    }

    private int generateRandom(int maxValue) {
        return random.nextInt(maxValue) + 1;
    }

    private List<Integer> generateRandomSeats(int maxNoSeats, int maxSeatNumber) {
        List<Integer> seats = new ArrayList<>();

        int nr = generateRandom(maxNoSeats);
        for (int i = 0; i < nr; ++i) {
            int seat = generateRandom(maxSeatNumber);
            while (seats.contains(seat)) {
                seat = generateRandom(maxSeatNumber);
            }
            seats.add(seat);
        }
        return seats;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        long lifeSpan = TimeUnit.NANOSECONDS.convert(1, TimeUnit.MINUTES);
        long sleepTime = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS);

        while (System.nanoTime() - startTime < lifeSpan && running.get()) {
            List<Integer> seats = generateRandomSeats(5, 100);
            System.out.println(seats);

            int showID = generateRandom(3);

            try {
                Boolean result = bookSeats(seats, showID);
                if (result == null) {
                    running.set(false);
                    System.out.println("Client stopped!");
                    this.interrupt();
                }
                System.out.println(result);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.out.println("Client stopped!");
            }
        }
    }
}
