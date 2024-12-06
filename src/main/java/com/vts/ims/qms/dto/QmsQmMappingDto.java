package com.vts.ims.qms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QmsQmMappingDto {
	                                                                                       
	private Long mocId;
	private String clauseNo;
	private String sectionNo;
	private Long mocParentId;
	private String isForCheckList;
	private String description;
	private Long documentId;
	private Long revisionRecordId;
	private Integer isActive;
	
}
