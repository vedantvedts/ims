package com.vts.ims.audit.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditClosureDTO {
	                                 
    private Long closureId;
    private Long iqaId;
    private String content;
    private LocalDateTime completionDate;
    private String attchmentName;
    private String iqaNo;
    private String oldAttchmentName;

}

