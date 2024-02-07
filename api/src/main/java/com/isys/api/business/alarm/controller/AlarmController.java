package com.isys.api.business.alarm.controller;

import com.isys.api.business.alarm.dto.AlarmDetailRequestDTO;
import com.isys.api.business.alarm.dto.AlarmListRequestDTO;
import com.isys.api.business.alarm.dto.AlarmListResponseDTO;
import com.isys.api.business.alarm.dto.PdfRequestDTO;
import com.isys.api.business.alarm.service.AlarmService;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.UrlResource;

@RestController
@RequestMapping("/alarm")
public class AlarmController {

    private final AlarmService alarmService;
    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);


    @Autowired
    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAlarmList(AlarmListRequestDTO alarmListRequestDTO) {

        List<AlarmListResponseDTO> result = alarmService.alarmList(alarmListRequestDTO);
        String train = alarmListRequestDTO.getTrain();
        String trainValueType = (train != null) ? train.getClass().getName() : "null";
        logger.info("Received train value: {}, Type: {}", train, trainValueType);

        if (!result.isEmpty()) {
            return ResponseEntity.ok(Map.of("ok", true, "result", result));
        }

        return ResponseEntity.ok(Map.of("ok", false, "result", result));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getAlarmDetail(AlarmDetailRequestDTO alarmDetailRequestDTO) {
        Optional<AlarmListResponseDTO> result = alarmService.alarmDetail(alarmDetailRequestDTO);

        System.out.println(alarmDetailRequestDTO);

        if (!result.isEmpty()) {
            return ResponseEntity.ok(Map.of("ok", true, "result", result));
        }

        return ResponseEntity.ok(Map.of("ok", false, "result", result));
    }

    @GetMapping("/report")
    public ResponseEntity<Resource> getPdfFile(@RequestParam("date") String date, @RequestParam("filename") String filename) {
        try {
            PdfRequestDTO pdfRequestDTO = new PdfRequestDTO();
            pdfRequestDTO.setDate(date);
            pdfRequestDTO.setFilename(filename);

//            String filePath = "C:\\data\\" + pdfRequestDTO.getDate() + "\\" + pdfRequestDTO.getFilename() + ".pdf";
            String filePath = "file://192.168.0.15/Data/Report/" + pdfRequestDTO.getDate() + "/" + pdfRequestDTO.getFilename() + ".pdf";
            Path fileLocation = Paths.get(new URI(filePath));
            Resource resource = new UrlResource(fileLocation.toUri());


            if (resource.exists() || resource.isReadable()) {
                String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8.toString());

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename)
                        .body(resource);
            } else {
                // 파일이 없거나 읽을 수 없는 경우 적절한 응답 반환
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.internalServerError().build();
        }
    }
}
