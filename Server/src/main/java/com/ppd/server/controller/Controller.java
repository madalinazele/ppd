package com.ppd.server.controller;

import com.ppd.model.Event;
import com.ppd.server.Server;
import com.ppd.server.controller.request.BookRequest;
import com.ppd.service.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
@RequestMapping("/ppd")
public class Controller {
    private final SaleService saleService;
    private Server server;

    public Controller(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("start")
    public void start() {
        saleService.cleanUp();
        this.server = new Server(saleService);
        server.startServer();
    }

    @GetMapping("/clean-up")
    public void cleanUpDB() {
        saleService.cleanUp();
    }

    @GetMapping("/event/{id}")
    public Event getEvent(@PathVariable Long id) {
        return saleService.getEvent(id).get();
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookSeats(@RequestBody BookRequest bookRequest) throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(server.bookSeats(bookRequest.getSeats(), bookRequest.getEventID()).get(), HttpStatus.OK);
    }

    @GetMapping("events")
    public List<Event> getAllEvents() {
        return saleService.getAllEvents();
    }
}
