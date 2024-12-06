package com.vts.ims.qms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckListMasterDto {
	                                                                                       
	private Long mocId;
	private String description;
	private String sectionNo;
	private int level;
	private String clauseNo;
	
}
