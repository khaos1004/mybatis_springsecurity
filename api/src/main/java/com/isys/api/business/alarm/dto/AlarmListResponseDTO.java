package com.isys.api.business.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmListResponseDTO {
    private Long LOG_ID;
    private Long REF_LOG_ID;
    private LocalDateTime DEV_EVENT_TIME;
    private LocalDateTime RECV_DEV_EVENT_TIME;
    private BigDecimal CONTAINER_FK;
    private BigDecimal EQUIPMENT_FK;
    private BigDecimal PROGRAM_FK;
    private BigDecimal DEPARTURE_STATION_FK;
    private BigDecimal ARRIVAL_STATION_FK;
    private BigDecimal FROM_STATION_FK;
    private BigDecimal TO_STATION_FK;
    private BigDecimal ROUTE_FK;
    private BigDecimal EDGE_FK;
    private String DIRECTION_KOVIS;
    private BigDecimal LINEID;
    private BigDecimal TRACKID;
    private BigDecimal COUNT1;
    private BigDecimal COUNT2;
    private BigDecimal PARAM1;
    private BigDecimal OFFICE_FK;
    private BigDecimal HEADER_OFFICE_FK;
    private BigDecimal CENTER_OFFICE_FK;
    private BigDecimal MEMBER_OFFICE_FK;
    private BigDecimal TYPE_FK;
    private BigDecimal CMD_CODE;
    private BigDecimal CODE_FK;
    private String MESSAGE;
    private Float KP;
    private Float KM;
    private BigDecimal LINE_SPEED_CLASS;
    private String PT_NUMBER;
    private BigDecimal LATITUDE;
    private BigDecimal LONGITUDE;
    private BigDecimal PRIORITY;
    private BigDecimal ACK;
    private BigDecimal ACK_USER_FK;
    private LocalDateTime ACK_TIME;
    private BigDecimal SEND_YN;
    private String REPORT_FILENAME;
    private BigDecimal ALARM_REPORT;
    private String JSON_DATA;
}
