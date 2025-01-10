package com.vts.ims.audit.dto;

import lombok.Data;
@Data
public class CheckListItem {
	private Integer auditCheckListId;
	private Integer mocId;
    private Integer observation;
    private String auditorRemarks;
    private String auditeeRemarks;
    private String attachment;
    private String mocDescription;

}
