package com.lendwise.loanservice.dto.global;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
/*
    @created 5/9/2025 3:30 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalApiRequestDto {

    private Object payload;
    private Pagination pagination;
    private Search search;
    private Sort sort;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pagination {
        private Integer pageIndex;
        private Integer pageSize;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Search {
        private String searchTerm;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Sort {
        private String sortBy;
        private SORT_DIRECTION sortDirection;
    }
}