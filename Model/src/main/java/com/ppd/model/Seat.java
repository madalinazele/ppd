package com.ppd.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private long seatID;

    @Column(name = "seat_number")
    private int seatNumber;

    @Column(name = "room_id")
    private long roomID;
}
