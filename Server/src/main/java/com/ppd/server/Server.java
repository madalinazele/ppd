package com.ppd.server;

import com.ppd.service.SaleService;

import java.util.List;
import java.util.concurrent.*;

public class Server {

    private static final int MAX_THREADS = 3;
    private final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
    private final SaleService saleService;
    private static Server instance;
    private boolean isRunning = true;

    public Server(SaleService saleService) {
        this.saleService = saleService;
        if (instance == null) {
            instance = this;
        }
    }

    public void startServer() {
        Thread thread = new Thread(() -> instance.run());
        thread.start();
    }

    public void run() {
        long startTime = System.nanoTime();
        long lifeSpan = TimeUnit.NANOSECONDS.convert(2, TimeUnit.MINUTES);
        long sleepTime = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);

        while (System.nanoTime() - startTime < lifeSpan) {
            saleService.report();

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isRunning = false;
        System.out.println("Server stopped!");
    }

    public Future<Boolean> bookSeats(List<Integer> seats, Long showID) {
        return pool.submit(() -> {
            if (isRunning) {
                return saleService.bookSeats(seats, showID);
            } else {
                return null;
            }
        });
    }
}
