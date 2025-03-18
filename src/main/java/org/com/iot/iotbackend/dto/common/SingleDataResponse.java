package org.com.iot.iotbackend.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 제외
public class SingleDataResponse<T> {
    private MetaData metaData;
    private T data;

    public SingleDataResponse(MetaData metaData) {
        this.metaData = metaData;
        this.data = null;
    }

    public SingleDataResponse(MetaData metaData, T data) {
        this.metaData = metaData;
        this.data = data;
    }
}