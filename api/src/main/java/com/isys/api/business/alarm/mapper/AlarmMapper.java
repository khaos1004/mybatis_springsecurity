package com.isys.api.business.alarm.mapper;

import com.isys.api.business.alarm.dto.AlarmDetailRequestDTO;
import com.isys.api.business.alarm.dto.AlarmListRequestDTO;
import com.isys.api.business.alarm.dto.AlarmListResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AlarmMapper {
    List<AlarmListResponseDTO> getAlarmList(@Param("requestDTO") AlarmListRequestDTO requestDTO, @Param("offset") int offset);

    Optional<AlarmListResponseDTO> getAlarmDetail(@Param("requestId") AlarmDetailRequestDTO id);
}
