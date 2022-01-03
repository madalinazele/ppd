package com.ppd.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private long saleID;

    @Column(name = "event_id")
    private long eventID;

    @Column(name = "sale_date")
    private ZonedDateTime saleDate;

    @Column(name = "no_tickets")
    private int noTickets;

    @Column(name = "sum")
    private BigDecimal sum;
}
