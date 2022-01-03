package com.ppd.repository;

import com.ppd.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByRoomIDAndSeatNumber(long roomID, int seatNumber);
}
