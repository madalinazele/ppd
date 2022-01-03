package com.ppd.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Report {
    private String eventTitle;
    private List<Integer> eventSeats;
    private BigDecimal eventSum;
    private boolean result;
}
