package com.ppd.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "soldseat")
public class SoldSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sold_seat_id")
    private long soldSeatID;

    @Column(name = "sale_id")
    private long saleID;

    @Column(name = "seat_id")
    private long seatID;

    @Column(name = "seat_number")
    private int seatNumber;

    public SoldSeat(long saleID, long seatID, int seatNumber) {
        this.saleID = saleID;
        this.seatID = seatID;
        this.seatNumber = seatNumber;
    }
}
