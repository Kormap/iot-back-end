package org.com.iot.iotbackend.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 제외
public class CommonResponse<T> {
    private MetaData metaData;
    private T data;

    public CommonResponse(MetaData metaData) {
        this.metaData = metaData;
        this.data = null;
    }

    public CommonResponse(MetaData metaData, T data) {
        this.metaData = metaData;
        this.data = data;
    }
}