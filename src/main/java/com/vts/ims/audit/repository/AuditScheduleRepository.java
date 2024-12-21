package com.vts.ims.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vts.ims.audit.model.AuditSchedule;

public interface AuditScheduleRepository extends JpaRepository<AuditSchedule, Long> {
	
	@Query(value="SELECT a.ScheduleId,a.ScheduleDate,a.AuditeeId,a.TeamId,c.TeamCode,d.EmpId,d.DivisionId,d.GroupId,d.ProjectId,(SELECT MAX(b.RevisionNo) FROM ims_audit_schedule_rev b WHERE a.ScheduleId= b.ScheduleId AND b.IsActive = 1) AS 'revision',a.ScheduleStatus,a.IqaId,e.StatusName,f.IqaNo,\r\n"
			+ "(SELECT g.Remarks FROM ims_audit_schedule_rev g WHERE a.ScheduleId= g.ScheduleId AND g.IsActive = 1 ORDER BY RevScheduleId DESC LIMIT 1) AS 'remarks',a.ActEmpId FROM ims_audit_schedule a,ims_audit_team c,ims_audit_auditee d,ims_audit_status e,ims_audit_iqa f \r\n"
			+ "WHERE a.IsActive = 1 AND a.IsActive = 1 AND a.TeamId = c.TeamId AND d.IsActive = 1 AND d.AuditeeId = a.AuditeeId AND a.ScheduleStatus = e.AuditStatus AND a.IqaId = f.IqaId ORDER BY a.ScheduleId DESC",nativeQuery = true)
	public List<Object[]> getScheduleList();
	
	@Query(value="SELECT a.ScheduleId,a.ScheduleDate,a.AuditeeId,a.TeamId,c.TeamCode,d.EmpId,d.DivisionId,d.GroupId,d.ProjectId,(SELECT MAX(b.RevisionNo) FROM ims_audit_schedule_rev b WHERE a.ScheduleId= b.ScheduleId AND b.IsActive = 1) AS 'revision',a.ScheduleStatus,a.IqaId,e.StatusName,f.IqaNo,(SELECT g.Remarks FROM ims_audit_schedule_rev g WHERE a.ScheduleId= g.ScheduleId AND g.IsActive = 1 ORDER BY RevScheduleId DESC LIMIT 1) AS 'remarks',\r\n"
			+ "a.ActEmpId,:empId,(SELECT j.EmpId FROM ims_audit_auditor j,ims_audit_team_members k WHERE j.AuditorId = k.AuditorId AND k.TeamId = c.TeamId AND j.IsActive = 1 AND k.IsActive = 1 AND k.IsLead =1) AS 'leadId',(CASE WHEN d.EmpId = :empId THEN 'A' ELSE 'L' END) AS 'flag',"
			+ "((SELECT COUNT(l.MocId) FROM ims_qms_qm_mapping_classes l WHERE l.IsActive = 1 AND l.IsForCheckList = 'Y') = (SELECT COUNT(m.AuditCheckListId) FROM ims_audit_check_list m WHERE m.IsActive = 1 AND m.ScheduleId = a.ScheduleId AND m.AuditorRemarks IS NOT NULL)) AS 'FwdFlag',f.Scope FROM ims_audit_schedule a,ims_audit_team c,ims_audit_auditee d,ims_audit_status e,ims_audit_iqa f \r\n"
			+ "WHERE a.IsActive = 1 AND a.IsActive = 1 AND a.TeamId = c.TeamId AND d.IsActive = 1 AND d.AuditeeId = a.AuditeeId AND a.ScheduleStatus = e.AuditStatus AND a.IqaId = f.IqaId AND a.ScheduleStatus NOT IN ('INI','ASR','ARL') AND((d.EmpId =:empId) OR (:empId IN (SELECT h.EmpId FROM ims_audit_auditor h,ims_audit_team_members i WHERE h.AuditorId = i.AuditorId AND i.TeamId = c.TeamId )) OR (:ImsFormRoleId IN ('1','2','3','4')))ORDER BY a.ScheduleId DESC",nativeQuery = true)
	public List<Object[]> getScheduleApprovalList(@Param("empId")Long empId,@Param("ImsFormRoleId")Long roleId);
	
	@Query(value="SELECT a.EmpId,a.AuditStatus,a.TransactionDate,a.Remarks,b.StatusName FROM ims_audit_trans a,ims_audit_status b WHERE a.ScheduleId = :scheduleId AND a.AuditStatus = b.AuditStatus ORDER BY a.AuditScheduleTransactionId",nativeQuery = true)
	public List<Object[]> getScheduleTran(@Param("scheduleId")String scheduleId);
}
