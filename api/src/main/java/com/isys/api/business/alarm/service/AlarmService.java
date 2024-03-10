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
//        if(alarmListRequestDTO.getSkip() == 0 ){
//            alarmListRequestDTO.setSkip(1);
//        }
//        int offset = (alarmListRequestDTO.getSkip() - 1) * alarmListRequestDTO.getTake();
        int offset = (alarmListRequestDTO.getSkip() - 1);
        offset = Math.max(offset, 0); // 음수가 되지 않도록 함
        return alarmMapper.getAlarmList(alarmListRequestDTO, offset);
    }

    public int alarmListCount(AlarmListRequestDTO alarmListRequestDTO) {
        return alarmMapper.getAlarmCount(alarmListRequestDTO);
    }

    public List<Optional<AlarmListResponseDTO>> alarmDetail(AlarmDetailRequestDTO alarmDetailRequestDTO) {
        return alarmMapper.getAlarmDetail(alarmDetailRequestDTO);
    }

    public int alarmDetailCount(AlarmDetailRequestDTO alarmDetailRequestDTO) {
        return alarmMapper.getAlarmDetailCount(alarmDetailRequestDTO);
    }

    public List<Optional<AlarmListResponseDTO>> alarm(AlarmDetailRequestDTO alarmDetailRequestDTO) {
        return alarmMapper.getAlarm(alarmDetailRequestDTO);
    }
}
