package com.vts.ims.dashboard.dto;


import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckListDetailsDto {


    private Long auditCheckListId;
    private Long iqaId;
    private String iqaNo;
    private Long auditObsId;
    private Long scheduleId;
    private LocalDateTime scheduleDate;
    private Long auditeeId;
    private Long empId;
    private Long divisionId;
    private Long groupId;
    private Long projectId;
	
}
