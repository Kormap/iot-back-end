package org.com.iot.iotbackend.dto.sensor;

import java.time.LocalDateTime;

/*
    로그인 사용자의 모니터링 페이지 임계값(설정값) 이상, 이하 DTO
    네이티브쿼리를 DTO 로 매핑하기 위한 인터페이스 기반 프로젝션
 */
public interface TodayWarningDTO {
    Long getId();
    String getWarningType();
    LocalDateTime getWarningAt();
    String getSensorType();
}
