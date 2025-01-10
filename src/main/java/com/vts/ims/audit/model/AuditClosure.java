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
@Table(name="ims_audit_closure")
public class AuditClosure {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ClosureId")
	private Long closureId;
	
	@Column(name = "IqaId")
	private Long iqaId;
		
	@Column(name = "ClosureDate")
	private LocalDateTime closureDate;
	
	@Column(name = "ActEmpId")
	private Long actEmpId;
	
	@Column(name = "AttachmentName")
	private String attachmentName;
	
	@Column(name = "Remarks")
	private String remarks;
	
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
