package com.isys.api.business.alarm.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class AlarmListRequestDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate; //DEV_EVENT_TIME

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate; //DEV_EVENT_TIME

    private List<BigDecimal> point; //CONTAINER_FK
    private String train; //PT_NUM
    private int take = 1; // 기본값으로 1 설정
    private int skip = 30; // 기본값으로 20 설정
}

