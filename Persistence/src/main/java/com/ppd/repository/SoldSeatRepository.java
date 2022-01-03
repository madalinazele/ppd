package com.ppd.repository;

import com.ppd.model.SoldSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoldSeatRepository extends JpaRepository<SoldSeat, Long> {

    Optional<SoldSeat> findBySeatID(long seatID);

    List<SoldSeat> findBySaleID(long saleID);
}
