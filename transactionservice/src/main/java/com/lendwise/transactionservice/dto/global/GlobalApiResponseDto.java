package com.lendwise.transactionservice.dto.global;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
    @created 5/9/2025 3:30 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalApiResponseDto {
    @JsonProperty("apiResponseCode")
    private Integer apiResponseCode;

    @JsonProperty("apiResponseMessage")
    private String apiResponseMessage;

    @JsonProperty("apiResponseTimestamp")
    private LocalDateTime apiResponseTimestamp;

    @JsonProperty("apiResponseData")
    private ApiResponseData apiResponseData;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiResponseData {

        @JsonProperty("errors")
        private Object errors;

        @JsonProperty("data")
        private Object data;

        @JsonProperty("pageSize")
        private String pageSize;

        @JsonProperty("pageIndex")
        private String pageIndex;

        @JsonProperty("totalPages")
        private String totalPages;

        @JsonProperty("totalRecords")
        private String totalRecords;
    }
}
