package org.farm.fireflyserver.domain.led.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.entity.LedData;
import org.farm.fireflyserver.domain.led.web.dto.request.SaveLedDataDto;
import org.farm.fireflyserver.domain.led.persistence.repository.LedDataRepository;
import org.farm.fireflyserver.domain.led.web.mapper.LedMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedService {
    private final LedDataRepository ledDataRepository;
    private final LedMapper ledMapper;

    @Transactional
    public void saveLedData(SaveLedDataDto dto) {
        System.out.println("LED 데이터 저장: " + dto.snsrSn());
        System.out.println("LED 데이터 저장: " + dto.trgSn());
        LedData ledData = dto.toEntity(dto);
        ledDataRepository.save(ledData);
    }
}