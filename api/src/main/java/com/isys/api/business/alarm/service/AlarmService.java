package com.isys.api.business.alarm.service;

import com.isys.api.business.alarm.dto.AlarmDetailRequestDTO;
import com.isys.api.business.alarm.dto.AlarmListRequestDTO;
import com.isys.api.business.alarm.dto.AlarmListResponseDTO;
import com.isys.api.business.alarm.mapper.AlarmMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmMapper alarmMapper;

    public List<AlarmListResponseDTO> alarmList(AlarmListRequestDTO alarmListRequestDTO) {
        int offset = (alarmListRequestDTO.getPage() - 1) * alarmListRequestDTO.getPageSize();
        offset = Math.max(offset, 0); // 음수가 되지 않도록 함
        return alarmMapper.getAlarmList(alarmListRequestDTO, offset);
    }

    public List<Optional<AlarmListResponseDTO>> alarmDetail(AlarmDetailRequestDTO alarmDetailRequestDTO) {
        return alarmMapper.getAlarmDetail(alarmDetailRequestDTO);
    }
}
