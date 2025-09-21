package org.farm.fireflyserver.domain.led.persistence.repository;

import org.farm.fireflyserver.domain.led.persistence.entity.LedState;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LedStateRepository extends JpaRepository<LedState, Long> {

    Optional<LedState> findByLedMtchnSnAndSensorGbn(String ledMtchnSn, SensorGbn sensorGbn);
    List<LedState> findByLedMtchnSn(String ledMtchnSn);

}