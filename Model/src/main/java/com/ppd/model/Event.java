package com.ppd.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @Column(name = "event_id")
    private long eventID;

    @Column(name = "event_date")
    private String eventDate;

    @Column(name = "title")
    private String title;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    @Column(name = "balance")
    private BigDecimal balance = new BigDecimal(0);

    @Column(name = "room_id")
    private long roomID;

    @Column(name = "sold_seats")
    private String soldSeatsNumbers = "";

}
