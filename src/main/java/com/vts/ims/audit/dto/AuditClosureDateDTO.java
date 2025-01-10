package com.vts.ims.audit.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditClosureDateDTO {
	                                 
    private Long iqaId;
    private String completionDate;
    private String fromDate;

}

