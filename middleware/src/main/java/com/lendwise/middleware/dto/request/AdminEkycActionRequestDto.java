package com.lendwise.middleware.dto.request;

import lombok.Data;

@Data
public class AdminEkycActionRequestDto {

    private Integer userId;
    private String action;              // APPROVED / REJECTED
    private Integer reviewedById;
    private String reviewerName;
    private String adminRemarks;
    private String rejectionReason;
    private String rejectionCategory;
    private Integer updatedById;
}
