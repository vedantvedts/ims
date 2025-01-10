package com.vts.ims.audit.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="ims_audit_check_list")
public class AuditCheckList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AuditCheckListId")
	private Long auditCheckListId;
	  
	@Column(name = "ScheduleId")
	private Long scheduleId;
	
	@Column(name = "IqaId")
	private Long iqaId;
	
	@Column(name = "MocId")
	private Long mocId;
	
	@Column(name = "MocDescription")
	private String mocDescription;
	
	@Column(name = "AuditObsId")
	private Long auditObsId;
	
	@Column(name = "AuditeeRemarks")
	private String auditeeRemarks;
	
	@Column(name = "AuditorRemarks")
	private String auditorRemarks;
	
	@Column(name = "Attachment")
	private String attachment;
	
	@Column(name = "CreatedBy")
	private String createdBy;
	
	@Column(name = "CreatedDate")
	private LocalDateTime createdDate;
	
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	
	@Column(name = "ModifiedDate")
	private LocalDateTime modifiedDate;
	
	@Column(name = "IsActive")
	private int isActive;
}
