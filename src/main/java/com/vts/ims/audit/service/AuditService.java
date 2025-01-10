package com.vts.ims.audit.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.vts.ims.audit.dto.AuditCarDTO;
import com.vts.ims.audit.dto.AuditCheckListDTO;
import com.vts.ims.audit.dto.AuditClosureDTO;
import com.vts.ims.audit.dto.AuditClosureDateDTO;
import com.vts.ims.audit.dto.AuditCorrectiveActionDTO;
import com.vts.ims.audit.dto.AuditRescheduleDto;
import com.vts.ims.audit.dto.AuditScheduleDto;
import com.vts.ims.audit.dto.AuditScheduleListDto;
import com.vts.ims.audit.dto.AuditScheduleRemarksDto;
import com.vts.ims.audit.dto.AuditTeamEmployeeDto;
import com.vts.ims.audit.dto.AuditTeamMembersDto;
import com.vts.ims.audit.dto.AuditTotalTeamMembersDto;
import com.vts.ims.audit.dto.AuditTranDto;
import com.vts.ims.audit.dto.AuditeeDto;
import com.vts.ims.audit.dto.AuditorDto;
import com.vts.ims.audit.dto.AuditorTeamDto;
import com.vts.ims.audit.dto.CheckListDto;
import com.vts.ims.audit.dto.IqaAuditeeDto;
import com.vts.ims.audit.dto.IqaAuditeeListDto;
import com.vts.ims.audit.dto.IqaDto;
import com.vts.ims.audit.dto.IqaScheduleDto;
import com.vts.ims.audit.model.AuditClosure;
import com.vts.ims.audit.model.AuditObservation;
import com.vts.ims.audit.model.AuditTeam;
import com.vts.ims.master.dto.DivisionGroupDto;
import com.vts.ims.master.dto.DivisionMasterDto;
import com.vts.ims.master.dto.EmployeeDto;
import com.vts.ims.master.dto.ProjectMasterDto;

public interface AuditService {

	public List<AuditorDto> getAuditorList() throws Exception;

	public List<EmployeeDto> getEmployelist() throws Exception;

	public long insertAuditor(String[] empIds,String username) throws Exception;

	public long updateAuditor(AuditorDto auditordto, String username) throws Exception;

	public List<IqaDto> getIqaList() throws Exception;

	public long insertIqa(IqaDto iqadto, String username) throws Exception;

	public List<AuditeeDto> getAuditeeList() throws Exception;

	public List<DivisionMasterDto> getDivisionMaster() throws Exception;

	public List<DivisionGroupDto> getDivisionGroupList() throws Exception;

	public List<ProjectMasterDto> getProjectMasterList() throws Exception;

	public long insertAuditee(AuditeeDto auditeedto, String username) throws Exception;
	
	public List<AuditTeam> getTeamList()throws Exception;

	public long updateAuditee(String auditeeId, String username) throws Exception;

	public long insertAuditSchedule(AuditScheduleDto auditScheduleDto, String username)throws Exception;
	public long editAuditSchedule(AuditScheduleDto auditScheduleDto, String username)throws Exception;
	public long insertAuditReSchedule(AuditRescheduleDto auditRescheduleDto, String username)throws Exception;

	public List<AuditScheduleListDto> getScheduleList()throws Exception;
	public List<AuditScheduleListDto> getScheduleApprovalList(String username)throws Exception;

	public long forwardSchedule(List<Long> auditScheduleIds, String username)throws Exception;
	
	public long auditorForward(AuditScheduleListDto auditScheduleListDto, String username)throws Exception;
	
	public long approveSchedule(AuditScheduleListDto auditScheduleListDto, String username)throws Exception;
	
	public long returnSchedule(AuditScheduleListDto auditScheduleListDto, String username)throws Exception;

	public long scheduleMailSend(List<AuditScheduleListDto> auditScheduleListDto, String username)throws Exception;

	public List<AuditTotalTeamMembersDto> getTotalTeamMembersList()throws Exception;
	
	public long rescheduleMailSend(AuditRescheduleDto auditRescheduleDto, String username)throws Exception;

	public List<AuditorTeamDto> getAuditTeamMainList() throws Exception;

	public List<AuditorDto> getAuditorIsActiveList() throws Exception;

	public List<AuditTeamMembersDto> getTeamMmberIsActiveList() throws Exception;

	public long insertAuditTeam(AuditorTeamDto auditormemberteamdto, String username) throws Exception;

	public List<AuditTeamEmployeeDto> getauditteammemberlist() throws Exception;

	public List<AuditScheduleRemarksDto> getScheduleRemarks() throws Exception;

	public List<AuditTranDto> scheduleTran(AuditTranDto auditTranDto) throws Exception;

	public List<IqaAuditeeDto> getIqaAuditeeList(Long iqaId) throws Exception;

	public long insertIqaAuditee(IqaAuditeeDto iqaAuditeeDto, String username) throws Exception;

	public List<IqaAuditeeListDto> getIqaAuditeelist()throws Exception;

	public List<AuditObservation> getObservation()throws Exception;

	public int addAuditCheckList(AuditCheckListDTO auditCheckListDTO, String username)throws Exception;
	
	public long addAuditeeRemarks(List<MultipartFile> files,AuditCheckListDTO auditCheckListDTO, String username)throws Exception;
	
	public long updateAuditCheckList(AuditCheckListDTO auditCheckListDTO, String username)throws Exception;
	
	public int updateAuditeeRemarks(List<MultipartFile> files,AuditCheckListDTO auditCheckListDTO, String username)throws Exception;

	public List<CheckListDto> getAuditCheckList(String scheduleId)throws Exception;
	
	public List<AuditCorrectiveActionDTO> getCarList()throws Exception;

	public long uploadCheckListImage(MultipartFile file, Map<String, Object> response, String username)throws Exception;

	public String getCheckListimg(AuditScheduleListDto auditScheduleListDto)throws Exception;

	public Long checkAuditorPresent(String auditorId)throws Exception;

	public int deleteAuditor(String auditorId)throws Exception;

	public List<CheckListDto> getAuditCheckListbyObsIds()throws Exception;

	public List<CheckListDto> getMostFrequentNC() throws Exception;
	
	public int insertCorrectiveAction(List<AuditCarDTO> auditCarDTO, String username)throws Exception;

	public int updateCorrectiveAction(AuditCarDTO auditCarDTO, String username)throws Exception;

	public long uploadCarAttachment(MultipartFile file, Map<String, Object> response, String username)throws Exception;
	
	public long forwardCar(AuditCorrectiveActionDTO auditCorrectiveActionDTO, String username)throws Exception;

	public List<AuditTranDto> carApproveEmpData(String carId)throws Exception;
	
	public long returnCarReport(AuditCorrectiveActionDTO auditCorrectiveActionDTO, String username)throws Exception;
	
	public List<CheckListDto> getMostFqNCMocDes(Long scheduleId, Integer auditObsId, Long iqaId)throws Exception;
	
	public List<IqaScheduleDto> getIqaScheduleList() throws Exception;

	public long addAuditClosure(AuditClosureDTO auditClosureDTO, String username)throws Exception;

	public List<AuditClosure> getAuditClosureList()throws Exception;
	
	public long updateAuditClosure(AuditClosureDTO auditClosureDTO, String username)throws Exception;
	
	public long uploadAuditClosureFile(MultipartFile file, Map<String, Object> response)throws Exception;
	
	public List<AuditClosureDateDTO> getClosureDate()throws Exception;

	public List<CheckListDto> getMostFreqNCDetails(Long mocId)throws Exception;



}
