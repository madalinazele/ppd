package com.ppd.service;

import com.ppd.model.*;
import com.ppd.repository.SaleRepository;
import com.ppd.repository.SeatRepository;
import com.ppd.repository.EventRepository;
import com.ppd.repository.SoldSeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SaleService {

    private static final String FILE_PATH = "report.txt";
    private EventRepository eventRepository;
    private SaleRepository saleRepository;
    private SeatRepository seatRepository;
    private SoldSeatRepository soldSeatRepository;

    private SaleService() {
    }

    @Autowired
    public SaleService(EventRepository eventRepository, SaleRepository saleRepository, SeatRepository seatRepository, SoldSeatRepository soldSeatRepository) {
        this.eventRepository = eventRepository;
        this.saleRepository = saleRepository;
        this.seatRepository = seatRepository;
        this.soldSeatRepository = soldSeatRepository;
    }

    public void cleanUp() {
        saleRepository.deleteAll();
        soldSeatRepository.deleteAll();
        eventRepository.deleteAll();

        eventRepository.save(new Event(1, LocalDateTime.now().toString(), "Show1", new BigDecimal(20), new BigDecimal(0), 1, ""));
        eventRepository.save(new Event(2, LocalDateTime.now().toString(), "Show2", new BigDecimal(30), new BigDecimal(0), 2, ""));
        eventRepository.save(new Event(3, LocalDateTime.now().toString(), "Show3", new BigDecimal(50), new BigDecimal(0), 3, ""));
    }

    public synchronized List<Sale> getAllEventSales(Long eventID) {
        return saleRepository.findByEventID(eventID);
    }

    public synchronized List<SoldSeat> getSoldSeatsBySale(Long saleID) {
        return soldSeatRepository.findBySaleID(saleID);
    }

    public Optional<Event> getEvent(long eventID) {
        return eventRepository.findById(eventID);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public synchronized boolean bookSeats(List<Integer> seats, Long eventID) {
        Sale newSale = new Sale();
        newSale.setEventID(eventID);
        newSale.setSaleDate(ZonedDateTime.now());

        Optional<Event> optionalEvent = eventRepository.findById(eventID);
        if (optionalEvent.isEmpty()) {
            return false;
        }

        Event event = optionalEvent.get();
        long roomID = event.getRoomID();

        StringBuilder soldSeatsStr = new StringBuilder(event.getSoldSeatsNumbers());
        Map<Long, Integer> seatIds = new HashMap<>();
        for (int seat : seats) {
            Optional<Seat> eventSeat = seatRepository.findByRoomIDAndSeatNumber(roomID, seat);
            if (eventSeat.isPresent()) {
                if (soldSeatRepository.findBySeatID(eventSeat.get().getSeatID()).isPresent()) {
                    return false;
                }
                seatIds.put(eventSeat.get().getSeatID(), seat);
                soldSeatsStr.append(" , ").append(seat);
            }
        }

        BigDecimal sum = optionalEvent.get().getTicketPrice().multiply(BigDecimal.valueOf(seats.size()));
        newSale.setNoTickets(seats.size());
        newSale.setSum(sum);
        Sale savedSale = saleRepository.save(newSale);

        for (Map.Entry<Long, Integer> entry : seatIds.entrySet()) {
            soldSeatRepository.save(new SoldSeat(savedSale.getSaleID(), entry.getKey(), entry.getValue()));
        }

        event.setSoldSeatsNumbers(soldSeatsStr.toString());
        event.setBalance(event.getBalance().add(sum));
        eventRepository.save(event);
        return true;
    }

    public synchronized void report() {
        List<Report> reports = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        List<Event> allEvents = getAllEvents();
        for (Event event : allEvents) {
            List<Integer> inDBSoldSeatsNumbers = new ArrayList<>();
            BigDecimal totalBalance = new BigDecimal(0);
            List<Sale> eventSales = getAllEventSales(event.getEventID());
            for (Sale sale : eventSales) {
                List<SoldSeat> saleSeats = getSoldSeatsBySale(sale.getSaleID());
                inDBSoldSeatsNumbers.addAll(saleSeats.stream().map(SoldSeat::getSeatNumber).toList());
                totalBalance = totalBalance.add(sale.getSum());
            }
            String s = event.getSoldSeatsNumbers();
            List<Integer> inMemSeats = Arrays.stream(s.split("\\s*,\\s*"))
                    .filter(x -> !x.isEmpty())
                    .map(Integer::parseInt).toList();
            boolean result = totalBalance.equals(event.getBalance()) && inDBSoldSeatsNumbers.containsAll(inMemSeats) && inMemSeats.containsAll(inDBSoldSeatsNumbers);

            System.out.println("Event: " + event.getTitle());
            System.out.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("Sold1: " + event.getBalance());
            System.out.println("Sold2: " + totalBalance);
            System.out.println("Sold seats 1: " + inMemSeats);
            System.out.println("Sold seats 2: " + inDBSoldSeatsNumbers);
            System.out.println("Result: " + result);
            reports.add(new Report(event.getTitle(), inMemSeats, event.getBalance(), result));
        }
        try {
            writeReport(date, reports);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeReport(LocalDateTime dateTime, List<Report> reports) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
        writer.write("Date and time: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        for (Report report : reports) {
            writer.write("  Event: " + report.getEventTitle() + "\n");
            writer.write("      Seats sold: " + report.getEventSeats() + "\n");
            writer.write("      Total sum: " + report.getEventSum() + "\n");
            writer.write("  Result: " + (report.isResult() ? "Correct" : "Incorrect!") + "\n");
        }
        writer.write("\n");
        writer.flush();
        writer.close();
    }
}
