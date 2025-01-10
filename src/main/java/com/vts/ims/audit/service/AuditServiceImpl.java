package com.vts.ims.audit.service;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.vts.ims.audit.dto.CheckListItem;
import com.vts.ims.audit.dto.IqaAuditeeDto;
import com.vts.ims.audit.dto.IqaAuditeeListDto;
import com.vts.ims.audit.dto.IqaDto;
import com.vts.ims.audit.dto.IqaScheduleDto;
import com.vts.ims.audit.model.AuditCheckList;
import com.vts.ims.audit.model.AuditClosure;
import com.vts.ims.audit.model.AuditCorrectiveAction;
import com.vts.ims.audit.model.AuditObservation;
import com.vts.ims.audit.model.AuditSchedule;
import com.vts.ims.audit.model.AuditScheduleRevision;
import com.vts.ims.audit.model.AuditTeam;
import com.vts.ims.audit.model.AuditTeamMembers;
import com.vts.ims.audit.model.AuditTransaction;
import com.vts.ims.audit.model.Auditee;
import com.vts.ims.audit.model.Auditor;
import com.vts.ims.audit.model.Iqa;
import com.vts.ims.audit.model.IqaAuditee;
import com.vts.ims.audit.repository.AuditCheckListRepository;
import com.vts.ims.audit.repository.AuditClosureRepository;
import com.vts.ims.audit.repository.AuditCorrectiveActionRepository;
import com.vts.ims.audit.repository.AuditObservationRepository;
import com.vts.ims.audit.repository.AuditScheduleRepository;
import com.vts.ims.audit.repository.AuditScheduleRevRepository;
import com.vts.ims.audit.repository.AuditTransactionRepository;
import com.vts.ims.audit.repository.AuditeeRepository;
import com.vts.ims.audit.repository.AuditorRepository;
import com.vts.ims.audit.repository.IqaAuditeeRepository;
import com.vts.ims.audit.repository.IqaRepository;
import com.vts.ims.audit.repository.TeamMemberRepository;
import com.vts.ims.audit.repository.TeamRepository;
import com.vts.ims.login.Login;
import com.vts.ims.login.LoginRepository;
import com.vts.ims.master.dao.MasterClient;
import com.vts.ims.master.dto.DivisionEmployeeDto;
import com.vts.ims.master.dto.DivisionGroupDto;
import com.vts.ims.master.dto.DivisionMasterDto;
import com.vts.ims.master.dto.EmployeeDto;
import com.vts.ims.master.dto.ProjectEmployeeDto;
import com.vts.ims.master.dto.ProjectMasterDto;
import com.vts.ims.model.ImsNotification;
import com.vts.ims.repository.NotificationRepository;
import com.vts.ims.util.DLocalConvertion;
import com.vts.ims.util.FormatConverter;
import com.vts.ims.util.NFormatConvertion;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


@Service
public class AuditServiceImpl implements AuditService{

	private static final Logger logger=LogManager.getLogger(AuditServiceImpl.class);
	
	@Autowired
	AuditorRepository auditRepository;
	
	@Autowired
	IqaRepository iqaRepository;
	
	@Autowired
	AuditeeRepository auditeeRepository;
	
	@Autowired
	IqaAuditeeRepository iqaAuditeeRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Autowired
	TeamMemberRepository teamMemberRepository;
	
	@Autowired
	private MasterClient masterClient;
	
	@Autowired
	private AuditScheduleRepository auditScheduleRepository;
	
	@Autowired
	private AuditScheduleRevRepository auditScheduleRevRepository;
	
	@Autowired
	private AuditTransactionRepository auditTransactionRepository;
	
	@Autowired
	LoginRepository loginRepo;
	
//	@Autowired
//	private JavaMailSender emailSender;
	
	@Autowired
	private NotificationRepository notificationRepo;
	
	@Value("${x_api_key}")
	private String xApiKey;
	
	@Autowired
	private AuditObservationRepository auditObservationRepository;
	
	@Autowired
	private AuditCheckListRepository auditCheckListRepository;
	
	@Value("${appStorage}")
	private String storageDrive ;
	
	@Autowired
	private LoginRepository loginRepository;
	

	@Autowired
	private AuditCorrectiveActionRepository auditCorrectiveActionRepository;
	
	@Value("${starttls}")
	private String starttls;
	
	@Value("${host}")
	private String host;
	
	@Value("${port}")
	private String port;
	
	@Value("${auth}")
	private String auth;
	
	@Value("${password}")
	private String password;
	
	@Value("${username1}")
	private String username1;
	
	@Autowired
	private AuditClosureRepository auditClosureRepository;
	
	@Override
	public List<AuditorDto> getAuditorList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getAuditorList()");
		try {
			List<Auditor> auditors = auditRepository.findAll();
			List<EmployeeDto> employeeList=masterClient.getEmployeeList(xApiKey);
		    Map<Long, EmployeeDto> employeeMap = employeeList.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
			List<AuditorDto> finalDto = auditors.stream()
				    .map(obj -> {
				        AuditorDto auditorDto = new AuditorDto();
				       // EmployeeDto employeeDto = masterClient.getEmployee(xApiKey, obj.getEmpId()).get(0);
				        EmployeeDto employeeDto =  employeeMap.get(obj.getEmpId());
				        auditorDto.setEmpId(obj.getEmpId());
				        auditorDto.setEmpName(employeeDto.getEmpName());
				        auditorDto.setDesignation(employeeDto.getEmpDesigName());
				        auditorDto.setDivisionName(employeeDto.getEmpDivCode());
				        auditorDto.setAuditorId(obj.getAuditorId());
				        auditorDto.setIsActive(obj.getIsActive());
				        return auditorDto;
				    })
				    .sorted(Comparator.comparingLong(AuditorDto::getAuditorId).reversed()) 
				    .collect(Collectors.toList());
			return finalDto;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditorList()"+ e);
			return Collections.emptyList();
		}
	}
	
	
	@Override
	public List<EmployeeDto> getEmployelist() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getEmployelist()");
		try {

			List<EmployeeDto> empdto=masterClient.getEmployeeList(xApiKey);
			 Comparator<EmployeeDto> comparator = Comparator
				        .comparingLong((EmployeeDto dto) -> dto.getSrNo() == 0 ? 1 : 0) 
				        .thenComparingLong(EmployeeDto::getSrNo);

				    return empdto.stream()
				                 .filter(dto -> dto.getIsActive() == 1) // Filter for isActive == 1
				                 .sorted(comparator) // Sort after filtering
				                 .collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getEmployelist()"+ e);
			return Collections.emptyList();
		}
	}
	
	
	@Override
	public long insertAuditor(String[] empIds, String username) throws Exception {
	    logger.info(new Date() + " AuditServiceImpl Inside method insertAuditor()");
	    long result = 0;
	    try {
	        if (empIds != null && empIds.length > 0) {
	            List<Object[]> inactiveAuditors = auditRepository.isActiveAuditorList(0);
	            Map<Long, Long> inactiveAuditorMap = inactiveAuditors.stream()
	                    .collect(Collectors.toMap(auditor -> (Long) auditor[0], auditor -> (Long) auditor[1]));
	            for (String empIdStr : empIds) {
	                Long empId = Long.parseLong(empIdStr);
	                if (inactiveAuditorMap.containsKey(empId)) {
	                    Long auditorId = inactiveAuditorMap.get(empId);
	                    Optional<Auditor> model = auditRepository.findById(auditorId); 
	                    if (model.isPresent()) {
	                        Auditor data = model.get();
	                        data.setIsActive(1);
	                        data.setModifiedBy(username);
	                        data.setModifiedDate(LocalDateTime.now());
	                        result = auditRepository.save(data).getAuditorId();
	                    }
	                } else {
	                    Auditor model = new Auditor();
	                    model.setEmpId(empId);
	                    model.setCreatedBy(username);
	                    model.setCreatedDate(LocalDateTime.now());
	                    model.setIsActive(1);
	                    result = auditRepository.save(model).getAuditorId();
	                }
	            }
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method insertAuditor() " + e);
	    }
	    return result;
	}

	
	
	@Override
	public long updateAuditor(AuditorDto auditordto, String username) throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method updateAuditor()");
		long result=0;
		try {
			Optional<Auditor> model =auditRepository.findById(auditordto.getAuditorId());
			if(model.isPresent()) {
				Auditor data = model.get();
				data.setIsActive(auditordto.getIsActive());
				data.setModifiedBy(username);
				data.setModifiedDate(LocalDateTime.now());
				result=auditRepository.save(data).getAuditorId();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method updateAuditor()"+ e);
			return 0;
		}
	}
	
	@Override
	public List<IqaDto> getIqaList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getIqaList()");
		try {
			List<Iqa> iqalist = iqaRepository.findAll();
			List<IqaDto> finalIqaDtoList = iqalist.stream()
				    .map(obj -> {
				        IqaDto dto = new IqaDto();
				        dto.setIqaId(obj.getIqaId());
				        dto.setIqaNo(obj.getIqaNo());
				        dto.setFromDate(obj.getFromDate());
				        dto.setToDate(obj.getToDate());
				        dto.setScope(obj.getScope());
				        dto.setDescription(obj.getDescription());
				        return dto;
				    })
				    .sorted(Comparator.comparingLong(IqaDto::getIqaId).reversed()) 
				    .collect(Collectors.toList());
			return finalIqaDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getIqaList()"+ e);
			 return Collections.emptyList();
		}
	}
	
	@Override
	public long insertIqa(IqaDto iqadto, String username) throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method insertIqa()");
		long result=0;
		try {
			if(iqadto!=null && iqadto.getIqaId()!=null) {
				Optional<Iqa> iqaEntity = iqaRepository.findById(iqadto.getIqaId());
				if(iqaEntity.isPresent()) {
				Iqa	model =iqaEntity.get();
				model.setIqaNo(iqadto.getIqaNo());
				model.setFromDate(iqadto.getFromDate());
				model.setToDate(iqadto.getToDate());
				model.setScope(iqadto.getScope().trim());
				model.setDescription(iqadto.getDescription().trim());
				model.setModifiedBy(username);
				model.setModifiedDate(LocalDateTime.now());
				result=iqaRepository.save(model).getIqaId();
				}
			}else {
				List<Iqa> iq=iqaRepository.findAll();
				String IqaNo="";
				if(iq.size()==0) {
					IqaNo="IQA-"+iqadto.getIqaNo();
				}else{
					IqaNo=iqadto.getIqaNo();
				}
				Iqa model=new Iqa();
				model.setIqaNo(IqaNo);
				model.setFromDate(iqadto.getFromDate());
				model.setToDate(iqadto.getToDate());
				model.setScope(iqadto.getScope().trim());
				model.setDescription(iqadto.getDescription().trim());
				model.setCreatedBy(username);
				model.setCreatedDate(LocalDateTime.now());
				model.setIsActive(1);
				result=iqaRepository.save(model).getIqaId();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getIqaList()"+ e);
			return result ;
		}
	}
	
	
	@Override
	public List<AuditeeDto> getAuditeeList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getAuditeeList()");
		try {
			List<Auditee> auditeeList = auditeeRepository.findAllByIsActive(1); 
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			List<DivisionMasterDto> divisionMaster = masterClient.getDivisionMaster(xApiKey);
			List<ProjectMasterDto> totalProject = masterClient.getProjectMasterList(xApiKey);
			List<DivisionGroupDto> groupList = masterClient.getDivisionGroupList(xApiKey);
			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
		    Map<Long,DivisionMasterDto> divisionMap = divisionMaster.stream()
		    		.filter(division -> division.getDivisionId() !=null)
		    		.collect(Collectors.toMap(DivisionMasterDto::getDivisionId, division -> division));
		    
		    Map<Long,ProjectMasterDto> projectMap = totalProject.stream()
		    		.filter(project -> project.getProjectId()!=null)
		    		.collect(Collectors.toMap(ProjectMasterDto::getProjectId, project -> project));
		    
		    Map<Long,DivisionGroupDto> groupMap = groupList.stream()
		    		.filter(group -> group.getGroupId() !=null)
		    		.collect(Collectors.toMap(DivisionGroupDto::getGroupId, group -> group));
			
			List<AuditeeDto> finalAuditeeDtoList = auditeeList.stream()
					.map(obj -> {
					    EmployeeDto employee =	obj.getEmpId() != null?employeeMap.get(obj.getEmpId()):null;
					    DivisionMasterDto division = (obj.getDivisionId()!=null && !obj.getDivisionId().toString().equalsIgnoreCase("0"))?divisionMap.get(obj.getDivisionId()):null;
					    DivisionGroupDto group = (obj.getGroupId()!=null && !obj.getGroupId().toString().equalsIgnoreCase("0"))?groupMap.get(obj.getGroupId()):null;
					    ProjectMasterDto project = (obj.getProjectId()!=null && !obj.getProjectId().toString().equalsIgnoreCase("0"))?projectMap.get(obj.getProjectId()):null;
				    
				    	AuditeeDto dto = new AuditeeDto();
				    	dto.setAuditeeId(obj.getAuditeeId());
				    	dto.setEmpId(obj.getEmpId());
				    	dto.setGroupId(obj.getGroupId());
				    	dto.setDivisionId(obj.getDivisionId());
				    	dto.setProjectId(obj.getProjectId());
				    	dto.setCreatedBy(obj.getCreatedBy());
				    	dto.setCreatedDate(obj.getCreatedDate());
				    	dto.setModifiedBy(obj.getModifiedBy());
				    	dto.setModifiedDate(obj.getModifiedDate());
				    	dto.setIsActive(obj.getIsActive());
				    	dto.setAuditee(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"");
				    	dto.setDivisionName(division !=null?division.getDivisionCode():"");
				    	dto.setGroupName(group !=null?group.getGroupCode():"");
				    	dto.setProjectName(project !=null?project.getProjectName():"");
				    	dto.setProjectShortName(project !=null?project.getProjectShortName():"");
				    	dto.setProjectCode(project !=null?project.getProjectCode():"");
				        return dto;
				    })
				    .sorted(Comparator.comparingLong(AuditeeDto::getAuditeeId).reversed()) 
				    .collect(Collectors.toList());
			return finalAuditeeDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditeeList()"+ e);
			 return Collections.emptyList();
		}
	}
	
	
	@Override
	public List<DivisionMasterDto> getDivisionMaster() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getDivisionMaster()");
		try {

			List<DivisionMasterDto> divisiondto=masterClient.getDivisionMaster(xApiKey);
			 return divisiondto.stream()
                     .filter(dto -> dto.getIsActive() == 1)
                     .collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getDivisionMaster()"+ e);
			return Collections.emptyList();
		}
	}
	
	
	@Override
	public List<DivisionGroupDto> getDivisionGroupList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getDivisionGroupList()");
		try {
			List<DivisionGroupDto> divisiongroupdto=masterClient.getDivisionGroupList(xApiKey);
			return divisiongroupdto.stream()
					.filter(dto -> dto.getIsActive()== 1)
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getDivisionGroupList()"+ e);
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<ProjectMasterDto> getProjectMasterList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getProjectMasterList()");
		try {
			List<ProjectMasterDto> projectmasterdto=masterClient.getProjectMasterList(xApiKey);
			return projectmasterdto.stream()
					.filter(dto -> dto.getIsActive() == 1)
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getProjectMasterList()"+ e);
			return Collections.emptyList();
		}
	}
	
	@Override
	public long insertAuditee(AuditeeDto auditeedto, String username) throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method insertAuditee()");
		long result=0;
		try {
			if(auditeedto!=null && auditeedto.getAuditeeId()!=null) {
				Optional<Auditee> auditeeEntity = auditeeRepository.findById(auditeedto.getAuditeeId());
				if(auditeeEntity.isPresent()) {
					Auditee model=auditeeEntity.get();
					model.setAuditeeId(auditeedto.getAuditeeId());
					model.setEmpId(auditeedto.getEmpId());
					if(auditeedto.getHeadType().equalsIgnoreCase("D")){
						model.setDivisionId(auditeedto.getDivisionId());
						model.setGroupId(0L);
						model.setProjectId(0L);
					}else if(auditeedto.getHeadType().equalsIgnoreCase("G")) {
						model.setGroupId(auditeedto.getGroupId());
						model.setProjectId(0L);
						model.setDivisionId(0L);
					}else if (auditeedto.getHeadType().equalsIgnoreCase("P")) {
						model.setProjectId(auditeedto.getProjectId());
						model.setDivisionId(0L);
						model.setGroupId(0L);
					}
					model.setModifiedBy(username);
					model.setModifiedDate(LocalDateTime.now());
					model.setIsActive(1);
					result=auditeeRepository.save(model).getAuditeeId();
					}
			}else {
				Auditee model=new Auditee();
				model.setEmpId(auditeedto.getEmpId());
				if(auditeedto.getHeadType().equalsIgnoreCase("D")){
					model.setDivisionId(auditeedto.getDivisionId());
					model.setGroupId(0L);
					model.setProjectId(0L);
				}else if(auditeedto.getHeadType().equalsIgnoreCase("G")) {
					model.setGroupId(auditeedto.getGroupId());
					model.setProjectId(0L);
					model.setDivisionId(0L);
				}else if (auditeedto.getHeadType().equalsIgnoreCase("P")) {
					model.setProjectId(auditeedto.getProjectId());
					model.setDivisionId(0L);
					model.setGroupId(0L);
				}
				model.setCreatedBy(username);
				model.setCreatedDate(LocalDateTime.now());
				model.setIsActive(1);
				result=auditeeRepository.save(model).getAuditeeId();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method insertAuditee()"+ e);
			return result ;
		}
	}
	
	@Override
	public List<AuditTeam> getTeamList() throws Exception {
	    logger.info(new Date() + " AuditServiceImpl Inside method getTeamList()");
	    try {
	    	return teamRepository.findAllByIsActive(1);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method getTeamList() " + e);
	        return List.of();
	    }
	}
	
	
	@Override
	public long updateAuditee(String auditeeId, String username) throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method updateAuditee()");
		long result=0;
		try {
			Optional<Auditee> model =auditeeRepository.findById(Long.parseLong(auditeeId));
			if(model.isPresent()) {
				Auditee data = model.get();
				data.setIsActive(0);
				data.setModifiedBy(username);
				data.setModifiedDate(LocalDateTime.now());
				result=auditeeRepository.save(data).getAuditeeId();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method updateAuditee()"+ e);
			return 0;
		}
	}
	
	@Override
	public long insertAuditSchedule(AuditScheduleDto auditScheduleDto, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method insertAuditSchedule()");
	    long result = 0;
	    try {
			Login login = loginRepo.findByUsername(username);
	        auditScheduleDto.setScheduleDate(DLocalConvertion.converLocalTime(auditScheduleDto.getScheduleDate()));
	    	
	    	AuditSchedule schedule = new AuditSchedule();
	    	schedule.setAuditeeId(auditScheduleDto.getAuditeeId());
	    	schedule.setScheduleDate(auditScheduleDto.getScheduleDate());
	    	schedule.setTeamId(auditScheduleDto.getTeamId());
	    	schedule.setIqaId(auditScheduleDto.getIqaId());
	    	schedule.setActEmpId(login.getEmpId());
	    	schedule.setScheduleStatus("INI");
	    	schedule.setCreatedBy(username);
	    	schedule.setCreatedDate(LocalDateTime.now());
	    	schedule.setIsActive(1);
	    	result = auditScheduleRepository.save(schedule).getScheduleId();
	    	
	    	AuditTransaction trans = new AuditTransaction();
			trans.setEmpId(login.getEmpId());
			trans.setScheduleId(result);
			trans.setTransactionDate(LocalDateTime.now());
			trans.setRemarks("NA");
			trans.setAuditStatus("INI");
			trans.setAuditType("S");
			
			auditTransactionRepository.save(trans);
			
	    	AuditScheduleRevision scheduleRev = new AuditScheduleRevision();
	    	scheduleRev.setScheduleId(result);
	    	scheduleRev.setAuditeeId(auditScheduleDto.getAuditeeId());
	    	scheduleRev.setScheduleDate(auditScheduleDto.getScheduleDate());
	    	scheduleRev.setTeamId(auditScheduleDto.getTeamId());
	    	scheduleRev.setIqaId(auditScheduleDto.getIqaId());
	    	scheduleRev.setActEmpId(login.getEmpId());
	    	scheduleRev.setRemarks(auditScheduleDto.getRemarks());
	    	scheduleRev.setCreatedBy(username);
	    	scheduleRev.setCreatedDate(LocalDateTime.now());
	    	scheduleRev.setIsActive(1);
	    	scheduleRev.setRevisionNo(0);
	    	result = auditScheduleRevRepository.save(scheduleRev).getRevScheduleId();
	    	
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method insertAuditSchedule() " + e);
	    }
	    return result;
	}
	
	
	@Override
	public long editAuditSchedule(AuditScheduleDto auditScheduleDto, String username) throws Exception {
	    logger.info(" AuditServiceImpl Inside method editAuditSchedule()");
	    long result = 0;
	    try {
	    	auditScheduleDto.setScheduleDate(DLocalConvertion.converLocalTime(auditScheduleDto.getScheduleDate()));
	    	AuditSchedule schedule = auditScheduleRepository.findById(auditScheduleDto.getScheduleId()).get();
	    	schedule.setAuditeeId(auditScheduleDto.getAuditeeId());
	    	schedule.setScheduleDate(auditScheduleDto.getScheduleDate());
	    	schedule.setTeamId(auditScheduleDto.getTeamId());
	    	schedule.setModifiedBy(username);
	    	schedule.setModifiedDate(LocalDateTime.now());
	    	result = auditScheduleRepository.save(schedule).getScheduleId();
	    	
	    	AuditScheduleRevision scheduleRev = auditScheduleRevRepository.findByScheduleId(auditScheduleDto.getScheduleId());
	    	scheduleRev.setScheduleId(result);
	    	scheduleRev.setAuditeeId(auditScheduleDto.getAuditeeId());
	    	scheduleRev.setScheduleDate(auditScheduleDto.getScheduleDate());
	    	scheduleRev.setTeamId(auditScheduleDto.getTeamId());
	    	scheduleRev.setModifiedBy(username);
	    	scheduleRev.setModifiedDate(LocalDateTime.now());
	    	result = auditScheduleRevRepository.save(scheduleRev).getRevScheduleId();
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method editAuditSchedule() " + e);
	    }
	    return result;
	}
	
	@Override
	public long insertAuditReSchedule(AuditRescheduleDto auditRescheduleDto, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method insertAuditReSchedule()");
	    long result = 0;
	    try {
	    	Login login = loginRepo.findByUsername(username);
	    	AuditScheduleDto auditScheduleDto = auditRescheduleDto.getAuditScheduleDto();
	    	AuditScheduleListDto auditScheduleListDto = auditRescheduleDto.getAuditScheduleListDto();
		
	    	auditScheduleDto.setScheduleDate(DLocalConvertion.converLocalTime(auditScheduleDto.getScheduleDate()));
	    	AuditSchedule schedule = auditScheduleRepository.findById(auditScheduleDto.getScheduleId()).get();
	    	schedule.setAuditeeId(auditScheduleDto.getAuditeeId());
	    	schedule.setScheduleDate(auditScheduleDto.getScheduleDate());
	    	schedule.setTeamId(auditScheduleDto.getTeamId());
	    	schedule.setIqaId(auditScheduleListDto.getIqaId());
	    	schedule.setActEmpId(login.getEmpId());
	    	schedule.setScheduleStatus("ARF");
	    	schedule.setModifiedBy(username);
	    	schedule.setModifiedDate(LocalDateTime.now());
	    	result = auditScheduleRepository.save(schedule).getScheduleId();
	    	
	    	AuditTransaction trans = new AuditTransaction();
			trans.setEmpId(login.getEmpId());
			trans.setScheduleId(result);
			trans.setTransactionDate(LocalDateTime.now());
			trans.setRemarks("NA");
			trans.setAuditStatus("ARF");
			trans.setAuditType("S");
			
			auditTransactionRepository.save(trans);
	    	
	    	AuditScheduleRevision scheduleRev = new AuditScheduleRevision();
	    	scheduleRev.setScheduleId(result);
	    	scheduleRev.setAuditeeId(auditScheduleDto.getAuditeeId());
	    	scheduleRev.setRemarks(auditScheduleDto.getRemarks());
	    	scheduleRev.setScheduleDate(auditScheduleDto.getScheduleDate());
	    	scheduleRev.setTeamId(auditScheduleDto.getTeamId());
	    	scheduleRev.setIqaId(auditScheduleListDto.getIqaId());
	    	scheduleRev.setActEmpId(login.getEmpId());
	    	scheduleRev.setCreatedBy(username);
	    	scheduleRev.setCreatedDate(LocalDateTime.now());
	    	scheduleRev.setIsActive(1);
	    	scheduleRev.setRevisionNo(auditScheduleDto.getRevision()+1);
	    	result = auditScheduleRevRepository.save(scheduleRev).getRevScheduleId();
	    	
	    	
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method insertAuditReSchedule() " + e);
	    }
	    return result;
	}
	
	@Override
	public List<AuditScheduleListDto> getScheduleList() throws Exception {
		logger.info( " AuditServiceImpl Inside method getScheduleList()");
		try {
			 List<Object[]> scheduleList = auditScheduleRepository.getScheduleList();
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			List<DivisionMasterDto> divisionMaster = masterClient.getDivisionMaster(xApiKey);
			List<ProjectMasterDto> totalProject = masterClient.getProjectMasterList(xApiKey);
			List<DivisionGroupDto> groupList = masterClient.getDivisionGroupList(xApiKey);
			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
		    Map<Long,DivisionMasterDto> divisionMap = divisionMaster.stream()
		    		.filter(division -> division.getDivisionId() !=null)
		    		.collect(Collectors.toMap(DivisionMasterDto::getDivisionId, division -> division));
		    
		    Map<Long,ProjectMasterDto> projectMap = totalProject.stream()
		    		.filter(project -> project.getProjectId()!=null)
		    		.collect(Collectors.toMap(ProjectMasterDto::getProjectId, project -> project));
		    
		    Map<Long,DivisionGroupDto> groupMap = groupList.stream()
		    		.filter(group -> group.getGroupId() !=null)
		    		.collect(Collectors.toMap(DivisionGroupDto::getGroupId, group -> group));
		    
			 List<AuditScheduleListDto> finalScheduleDtoList = Optional.ofNullable(scheduleList).orElse(Collections.emptyList()).stream()
				    .map(obj -> {
					    EmployeeDto employee =	obj[5] != null?employeeMap.get(Long.parseLong(obj[5].toString())):null;
					    DivisionMasterDto division = (obj[6]!=null && !obj[6].toString().equalsIgnoreCase("0"))?divisionMap.get(Long.parseLong(obj[6].toString())):null;
					    DivisionGroupDto group = (obj[7]!=null && !obj[7].toString().equalsIgnoreCase("0"))?groupMap.get(Long.parseLong(obj[7].toString())):null;
					    ProjectMasterDto project = (obj[8]!=null && !obj[8].toString().equalsIgnoreCase("0"))?projectMap.get(Long.parseLong(obj[8].toString())):null;
					    	 
					    	AuditScheduleListDto dto = new AuditScheduleListDto();

					    	dto.setScheduleId(obj[0] != null ? Long.parseLong(obj[0].toString()) : 0L);
					    	dto.setScheduleDate(obj[1] != null ? obj[1].toString() : "");
					    	dto.setAuditeeId(obj[2] != null ? Long.parseLong(obj[2].toString()) : 0L);
					    	dto.setTeamId(obj[3] != null ? Long.parseLong(obj[3].toString()) : 0L);
					    	dto.setTeamCode(obj[4] != null ? obj[4].toString() : "");
					    	dto.setAuditeeEmpId(obj[5] != null ? Long.parseLong(obj[5].toString()) : 0L);
					    	dto.setDivisionId(obj[6] != null ? Long.parseLong(obj[6].toString()) : 0L);
					    	dto.setGroupId(obj[7] != null ? Long.parseLong(obj[7].toString()) : 0L);
					    	dto.setProjectId(obj[8] != null ? Long.parseLong(obj[8].toString()) : 0L);
					    	dto.setRevision(obj[9] != null ? Integer.parseInt(obj[9].toString()) : 0);
					    	dto.setScheduleStatus(obj[10] != null ? obj[10].toString() : "");
					    	dto.setIqaId(obj[11] != null ? Long.parseLong(obj[11].toString()) : 0L);
					    	dto.setStatusName(obj[12] != null ? obj[12].toString() : "");
					    	dto.setIqaNo(obj[13] != null ? obj[13].toString() : "");
					    	dto.setRemarks(obj[14] != null ? obj[14].toString() : "NA");
					    	dto.setActEmpId(obj[15] != null ? Long.parseLong(obj[15].toString()) : 0L);
					    	dto.setAuditeeEmpName(employee != null ? employee.getEmpName() + ", " + employee.getEmpDesigName() : "");
					    	dto.setDivisionName(division != null ? division.getDivisionName() : "");
					    	dto.setGroupName(group != null ? group.getGroupName() : "");
					    	dto.setProjectName(project != null ? project.getProjectName() : "");
					    	dto.setProjectShortName(project != null ? project.getProjectShortName() : "");
					    	return dto;
				    })
				    .collect(Collectors.toList());
			return finalScheduleDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getScheduleList()"+ e);
			 return Collections.emptyList();
		}
	}


	@Override
	public long forwardSchedule(List<Long> auditScheduleIds, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method forwardSchedule()");
	    long result = 0;
	    try {
	    	Login login = loginRepo.findByUsername(username);
	    	for(Long scheduleId : auditScheduleIds) {
		    	AuditSchedule schedule = auditScheduleRepository.findById(scheduleId).get();
	
		    	schedule.setScheduleStatus("FWD");
		    	schedule.setModifiedBy(username);
		    	schedule.setModifiedDate(LocalDateTime.now());
		    	result = auditScheduleRepository.save(schedule).getScheduleId();
		    	
		    	AuditTransaction trans = new AuditTransaction();
				trans.setEmpId(login.getEmpId());
				trans.setScheduleId(result);
				trans.setTransactionDate(LocalDateTime.now());
				trans.setRemarks("NA");
				trans.setAuditStatus("FWD");
				trans.setAuditType("S");
				
				auditTransactionRepository.save(trans);
	    	}
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method forwardSchedule() " + e);
	    }
	    return result;
	}
	
	@Override
	public long auditorForward(AuditScheduleListDto auditScheduleListDto, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method auditorForward()");
	    long result = 0;
	    try {
	    	Login login = loginRepo.findByUsername(username);
	    	AuditTransaction trans = new AuditTransaction();
			EmployeeDto employeeLogIn = masterClient.getEmployee(xApiKey,login.getEmpId()).get(0);
			List<String> ncObs = Arrays.asList("2","3","4");
			AuditSchedule schedule = auditScheduleRepository.findById(auditScheduleListDto.getScheduleId()).get();
		    	if(schedule.getScheduleStatus().equalsIgnoreCase("AES") || schedule.getScheduleStatus().equalsIgnoreCase("RBA")){
			    	schedule.setScheduleStatus("ARS");
					trans.setAuditStatus("ARS");
		    	}else {
		    		List<Object[]> checkList = auditCheckListRepository.getAuditCheckList(auditScheduleListDto.getScheduleId().toString());
		    		List<Object[]> ncList = checkList.stream().filter(data -> ncObs.contains(data[4].toString())).collect(Collectors.toList());
		    		Integer ncCount = auditCorrectiveActionRepository.getActionCount(auditScheduleListDto.getIqaId(),auditScheduleListDto.getScheduleId(),"N");	    		
		    		Integer obsCount = auditCorrectiveActionRepository.getActionCount(auditScheduleListDto.getIqaId(),auditScheduleListDto.getScheduleId(),"O");	    		
		    		Integer ofiCount = auditCorrectiveActionRepository.getActionCount(auditScheduleListDto.getIqaId(),auditScheduleListDto.getScheduleId(),"F");	    		
		    		
	    			String divisionGroupName = !auditScheduleListDto.getDivisionName().equalsIgnoreCase("") ? auditScheduleListDto.getDivisionName(): (!auditScheduleListDto.getGroupName().equalsIgnoreCase("")?auditScheduleListDto.getGroupName() : auditScheduleListDto.getProjectShortName()); 
	    			
		    		for(Object[] obj : ncList) {
		    			AuditCorrectiveAction action = new AuditCorrectiveAction();
		    			String ObsCode = "NC";
		    			String carFlag = "N";
		    			if(obj[4].toString().equalsIgnoreCase("2")) {
		    				ObsCode = "NC";
		    				carFlag = "N";
			    			action.setCarRefNo(auditScheduleListDto.getIqaNo()+"/"+divisionGroupName+"/"+ObsCode+"/"+ (++ncCount));
		    			}else if(obj[4].toString().equalsIgnoreCase("3")) {
		    				ObsCode = "OBS";
		    				carFlag = "O";
			    			action.setCarRefNo(auditScheduleListDto.getIqaNo()+"/"+divisionGroupName+"/"+ObsCode+"/"+ (++obsCount));
		    			}else {
		    				ObsCode = "OFI";
		    				carFlag = "F";
			    			action.setCarRefNo(auditScheduleListDto.getIqaNo()+"/"+divisionGroupName+"/"+ObsCode+"/"+ (++ofiCount));
		    			} 					
		    			action.setAuditCheckListId(Long.parseLong(obj[0].toString()));	 
		    			action.setCarDescription(obj[10].toString());	 
		    			action.setCarFlag(carFlag);
		    			action.setIqaId(auditScheduleListDto.getIqaId());	    			
		    			action.setScheduleId(auditScheduleListDto.getScheduleId());	    			
		    			action.setCreatedBy(username);
		    			action.setCreatedDate(LocalDateTime.now());
		    			action.setIsActive(1);
		    			auditCorrectiveActionRepository.save(action);
		    		}
		    		
			    	schedule.setScheduleStatus("ABA");
					trans.setAuditStatus("ABA");
		    	}
	
		    	schedule.setModifiedBy(username);
		    	schedule.setModifiedDate(LocalDateTime.now());
		    	result = auditScheduleRepository.save(schedule).getScheduleId();
		    	
		
				trans.setEmpId(login.getEmpId());
				trans.setScheduleId(result);
				trans.setTransactionDate(LocalDateTime.now());
				if(auditScheduleListDto.getScheduleStatus().equalsIgnoreCase("RBA")) {
					trans.setRemarks(auditScheduleListDto.getMessage());
				}else {
					trans.setRemarks("NA");
				}
				trans.setAuditType("S");
				auditTransactionRepository.save(trans);
				
				if(schedule.getScheduleStatus().equalsIgnoreCase("AES") || schedule.getScheduleStatus().equalsIgnoreCase("RBA")){
					String NotiMsg = auditScheduleListDto.getIqaNo()+" Of Audit Schedule CheckList Forwarded by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					insertScheduleNomination(auditScheduleListDto.getAuditeeEmpId(),login.getEmpId(),username,"/schedule-approval",NotiMsg);
		    	}else {
		    		List<Object[]> teamMemberDetails = teamRepository.getTeamMemberDetails(auditScheduleListDto.getTeamId());
					String NotiMsg = auditScheduleListDto.getIqaNo()+" Of Audit Schedule CheckList Accepted by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					for(Object[] obj : teamMemberDetails) {
						result = insertScheduleNomination(Long.parseLong(obj[1].toString()),login.getEmpId(),username,"/schedule-approval",NotiMsg);
					}
		    	}
				
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method auditorForward() " + e);
	    }
	    return result;
	}
	
	@Override
	public long approveSchedule(AuditScheduleListDto auditScheduleListDto, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method approveSchedule()");
	    long result = 0;
	    try {
	    	Login login = loginRepo.findByUsername(username);
		    	AuditSchedule schedule = auditScheduleRepository.findById(auditScheduleListDto.getScheduleId()).get();
		    	AuditTransaction trans = new AuditTransaction();
		    	if(auditScheduleListDto.getAuditeeEmpId().equals(login.getEmpId())){
		    		if(schedule.getScheduleStatus().equalsIgnoreCase("AAL")) {
				    	schedule.setScheduleStatus("AAA");
		    		}else {
				    	schedule.setScheduleStatus("ASA");
		    		}
					trans.setAuditStatus("ASA");
		    	}else if(auditScheduleListDto.getLeadEmpId().equals(login.getEmpId())){
		    		if(schedule.getScheduleStatus().equalsIgnoreCase("ASA")) {
				    	schedule.setScheduleStatus("AAA");
		    		}else {
				    	schedule.setScheduleStatus("AAL");
		    		}
					trans.setAuditStatus("AAL");
		    	}
		    	schedule.setModifiedBy(username);
		    	schedule.setModifiedDate(LocalDateTime.now());
		    	result = auditScheduleRepository.save(schedule).getScheduleId();
		    	
				trans.setEmpId(login.getEmpId());
				trans.setScheduleId(result);
				trans.setTransactionDate(LocalDateTime.now());
				trans.setRemarks("NA");
				trans.setAuditType("S");
				
				auditTransactionRepository.save(trans);
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method approveSchedule() " + e);
	    }
	    return result;
	}
	
	@Override
	public long returnSchedule(AuditScheduleListDto auditScheduleListDto, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method returnSchedule()");
	    long result = 0;
	    try {
	    	    Login login = loginRepo.findByUsername(username);
			    EmployeeDto employeeLogIn = masterClient.getEmployee(xApiKey,login.getEmpId()).get(0);
		    	AuditSchedule schedule = auditScheduleRepository.findById(auditScheduleListDto.getScheduleId()).get();
		    	AuditTransaction trans = new AuditTransaction();
		    	if(schedule.getScheduleStatus().equalsIgnoreCase("ARS")){
			    	schedule.setScheduleStatus("RBA");
					trans.setAuditStatus("RBA");
		    	}else {
			    	if(auditScheduleListDto.getAuditeeEmpId().equals(login.getEmpId())){
				    	schedule.setScheduleStatus("ASR");
						trans.setAuditStatus("ASR");
			    	}else if(auditScheduleListDto.getLeadEmpId().equals(login.getEmpId())){
				    	schedule.setScheduleStatus("ARL");
						trans.setAuditStatus("ARL");
			    	}
		    	}
		    	schedule.setModifiedBy(username);
		    	schedule.setModifiedDate(LocalDateTime.now());
		    	result = auditScheduleRepository.save(schedule).getScheduleId();
		    	
				trans.setEmpId(login.getEmpId());
				trans.setScheduleId(result);
				trans.setTransactionDate(LocalDateTime.now());
				trans.setRemarks(auditScheduleListDto.getMessage());
				trans.setAuditType("S");
				
				auditTransactionRepository.save(trans);
				String url= "/schedule-list";
			 	if(schedule.getScheduleStatus().equalsIgnoreCase("ARS")){
		    		List<Object[]> teamMemberDetails = teamRepository.getTeamMemberDetails(auditScheduleListDto.getTeamId());
					String NotiMsg = auditScheduleListDto.getIqaNo()+" Of Audit Schedule CheckList Rejected by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					for(Object[] obj : teamMemberDetails) {
						result = insertScheduleNomination(Long.parseLong(obj[1].toString()),login.getEmpId(),username,"/schedule-approval",NotiMsg);
					}
			 	}else {
					String NotiMsg = auditScheduleListDto.getIqaNo()+" Of Audit Schedule Returned by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					result = insertScheduleNomination(auditScheduleListDto.getActEmpId(),login.getEmpId(),username,url,NotiMsg);
			 	}
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method returnSchedule() " + e);
	    }
	    return result;
	}

	@Override 
	public long scheduleMailSend(List<AuditScheduleListDto> auditScheduleListDto, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method scheduleMailSend()");
	    long result = 0;
	    try {
	    	Login login = loginRepo.findByUsername(username);
	    	Long iqaId = auditScheduleListDto.get(0).getIqaId();
	    	String iqaNo = auditScheduleListDto.get(0).getIqaNo();
			EmployeeDto employeeLogIn = masterClient.getEmployee(xApiKey,login.getEmpId()).get(0);
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			List<Object[]> auditorsByIqa = teamRepository.getAuditorsByIqa(iqaId);
							
			Map<Long, List<AuditScheduleListDto>> auditeeMap = auditScheduleListDto.stream().collect(Collectors.groupingBy(AuditScheduleListDto::getAuditeeEmpId));
			Map<Long, List<AuditScheduleListDto>> teamMap = auditScheduleListDto.stream().collect(Collectors.groupingBy(AuditScheduleListDto::getTeamId));			
			
			String url= "/schedule-approval";
			String NotiMsg = auditScheduleListDto.get(0).getIqaNo()+" Of Audit Schedule Forwarded by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
			result = sendTeamMail(teamMap,username,login,url,NotiMsg,totalEmployee,auditorsByIqa,iqaNo);
			result = sendAuditeeMail(auditeeMap,username,login,url,NotiMsg,totalEmployee,auditorsByIqa,iqaNo);
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method scheduleMailSend() " + e);
	    }
	    return result;
	}
	
	public long sendAuditeeMail(Map<Long, List<AuditScheduleListDto>> auditeeMap,String username,Login login,String url,String NotiMsg,List<EmployeeDto> totalEmployee,List<Object[]> auditorsByIqa,String iqaNo) throws Exception {
		
		String heading   = "<span>Dear Sir/Madam,</span>"
							+ "<br /><br /><span>Please find details of your Audit Schedul of "+iqaNo+"</span>"
						    + "<br /><br />";
		String  note = "<br /><br /><span>Important Note: This is an automated message. Kindly avoid responding.</span>"
					    + "<br /><br /><span>Regards,</span> <br />"
						+ "<span>LRDE-IMS Team</span>";
		  try {			  
			auditeeMap.forEach((key, value) -> {
				    StringBuilder tableContent = new StringBuilder();
				    tableContent.append("<table border='1' style='border-collapse: collapse; width: 100%;'>")
					            .append("<tr><th style='width: 5%;'>SI No</th>")
					            .append("<th style='width: 10%;'>Date & Time (Hrs)</th>")
					            .append("<th style='width: 20%;'>Division/Group/Project</th>")
					            .append("<th style='width: 30%;'>Auditor</th>")
					            .append("<th style='width: 25%;'>Auditee</th>")
					            .append("<th style='width: 10%;'>Team</th></tr>");

				    AtomicInteger index = new AtomicInteger(1);
				    value.forEach(dto -> {
				    	try {
							List<Object[]> teamMemberDetails = teamRepository.getTeamMemberDetails(dto.getTeamId());
					        
					        tableContent.append("<tr><td style='text-align: center;'>").append(index.getAndIncrement()).append("</td>")
							            .append("<td style='text-align: center;'>").append(FormatConverter.getDateTimeFormat(dto.getScheduleDate())).append("</td>")
							            .append("<td>").append((dto.getGroupName() != ""?dto.getGroupName():dto.getDivisionName() != ""?dto.getDivisionName():dto.getProjectName() != ""?dto.getProjectName():" - ")).append("</td>")
							            .append("<td>");
				            for( int i = 0;i<teamMemberDetails.size();i++){
				         		EmployeeDto employee =	NFormatConvertion.getEmployeeDetails(Long.parseLong(teamMemberDetails.get(i)[1].toString()),totalEmployee);
				         		if(i < (teamMemberDetails.size() - 1)) {
				         			tableContent.append(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName()+",":"").append("<br />");
				         		}else {
				         			tableContent.append(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"");
				         		}
				             }
							                   
				            tableContent.append("</td>")
							                   .append("<td>").append(dto.getAuditeeEmpName()).append("</td>")
							                   .append("<td>").append(dto.getTeamCode()).append("</td></tr>");
							
						} catch (Exception e) {
							logger.error( " Inside sendAuditeeMail Service "+e );
							e.printStackTrace();
						}
				    });

				    tableContent.append("</table>");

					EmployeeDto employee =	NFormatConvertion.getEmployeeDetails(key,totalEmployee);
					if(employee!=null && employee.getEmail() !=null && !(employee.getEmail().equalsIgnoreCase("a@lrde.com"))) {	
			            sendHtmlMessage(employee.getEmail(), "Audit Schedule of " + iqaNo, tableContent.toString(), heading, note);
					}
		            insertScheduleNomination(key,login.getEmpId(),username,url,NotiMsg);
				});

		} catch (Exception e) {
			e.printStackTrace();
			logger.error( " Inside sendAuditeeMail Service "+e );
		}
		return 1;
	}
	
	public long sendTeamMail(Map<Long, List<AuditScheduleListDto>> teamMap,String username,Login login,String url,String NotiMsg,List<EmployeeDto> totalEmployee,List<Object[]> auditorsByIqa,String iqaNo) throws Exception {
		
		String heading   = "<span>Dear Sir/Madam,</span>"
							+ "<br /><br /><span>Please find details of your Audit Schedule of "+iqaNo+"</span>"
						    + "<br /><br />";
		String  note = "<br /><br /><span>Important Note: This is an automated message. Kindly avoid responding.</span>"
					    + "<br /><br /><span>Regards,</span> <br />"
						+ "<span>LRDE-IMS Team</span>";
		  try {			  
			  teamMap.forEach((key, value) -> {
				    StringBuilder tableContent = new StringBuilder();
				    tableContent.append("<table border='1' style='border-collapse: collapse; width: 100%;'>")
				                .append("<tr><th style='width: 5%;'>SI No</th>")
				                .append("<th style='width: 10%;'>Date & Time (Hrs)</th>")
				                .append("<th style='width: 20%;'>Division/Group</th>")
				                .append("<th style='width: 30%;'>Project</th>")
				                .append("<th style='width: 25%;'>Auditee</th>")
				                .append("<th style='width: 10%;'>Team</th></tr>");

				    AtomicInteger index = new AtomicInteger(1);
				    value.forEach(dto -> {
				        tableContent.append("<tr><td style='text-align: center;'>").append(index.getAndIncrement()).append("</td>")
				                    .append("<td style='text-align: center;'>").append(FormatConverter.getDateTimeFormat(dto.getScheduleDate())).append("</td>")
				                    .append("<td>").append(((dto.getGroupName() == "" && dto.getDivisionName() == "") ? " - " : (dto.getGroupName() != "" && dto.getDivisionName() != "") ? dto.getGroupName() +"/"+dto.getDivisionName():dto.getGroupName() != ""?dto.getGroupName():dto.getDivisionName() != ""?dto.getDivisionName():" - ")).append("</td>")
				                    .append("<td>").append(dto.getProjectName().isEmpty() ? " - " : dto.getProjectName()).append("</td>")
				                    .append("<td>").append(dto.getAuditeeEmpName()).append("</td>")
				                    .append("<td>").append(dto.getTeamCode()).append("</td></tr>");
				    });

				    tableContent.append("</table>");

				    for (Object[] obj : auditorsByIqa) {
				        if (key.equals(Long.parseLong(obj[1].toString()))) {
							EmployeeDto employee =	NFormatConvertion.getEmployeeDetails(Long.parseLong(obj[0].toString()),totalEmployee);
							if(employee!=null && employee.getEmail() !=null && !(employee.getEmail().equalsIgnoreCase("a@lrde.com"))) {	
					            sendHtmlMessage(employee.getEmail(), "Audit Schedule of " + iqaNo, tableContent.toString(), heading, note);
							}
				            insertScheduleNomination(Long.parseLong(obj[0].toString()),login.getEmpId(),username,url,NotiMsg);
				        }
				    }
				});

		} catch (Exception e) {
			e.printStackTrace();
			logger.error( " Inside sendTeamMail Service " );
		}
		return 1;
	}
	
	public long sendRescheduleMail(AuditScheduleListDto dto,String username,Login login,String url,String NotiMsg,List<EmployeeDto> totalEmployee) throws Exception {
		long result = 0;
		  try {
			  
			String heading   = "<span>Dear Sir/Madam,</span>"
								+ "<br /><br /><span>Please find details of your Audit Schedule of "+dto.getIqaNo()+"</span>"
							    + "<br /><br />";
			String  note = "<br /><br /><span>Important Note: This is an automated message. Kindly avoid responding.</span>"
						    + "<br /><br /><span>Regards,</span> <br />"
							+ "<span>LRDE-IMS Team</span>";
			
			List<Object[]> teamMemberDetails = teamRepository.getTeamMemberDetails(dto.getTeamId());
			
		    StringBuilder tableContent = new StringBuilder();
		    tableContent.append("<table border='1' style='border-collapse: collapse; width: 100%;'>")
		                .append("<tr><th style='width: 5%;'>SI No</th>")
		                .append("<th style='width: 10%;'>Date & Time (Hrs)</th>")
		                .append("<th style='width: 20%;'>Division/Group</th>")
		                .append("<th style='width: 30%;'>Project</th>")
		                .append("<th style='width: 25%;'>Auditee</th>")
		                .append("<th style='width: 10%;'>Team</th></tr>");


	        tableContent.append("<tr><td style='text-align: center;'>").append(1).append("</td>")
			            .append("<td style='text-align: center;'>").append(FormatConverter.getDateTimeFormat(dto.getScheduleDate())).append("</td>")
			            .append("<td>").append(((dto.getGroupName() == "" && dto.getDivisionName() == "") ? " - " : (dto.getGroupName() != "" && dto.getDivisionName() != "") ? dto.getGroupName() +"/"+dto.getDivisionName():dto.getGroupName() != ""?dto.getGroupName():dto.getDivisionName() != ""?dto.getDivisionName():" - ")).append("</td>")
			            .append("<td>").append(dto.getProjectName().isEmpty() ? " - " : dto.getProjectName()).append("</td>")
			            .append("<td>").append(dto.getAuditeeEmpName()).append("</td>")
			            .append("<td>").append(dto.getTeamCode()).append("</td></tr>");

		    tableContent.append("</table>");
		    
		    StringBuilder AuditeetableContent = new StringBuilder();
			
		    AuditeetableContent.append("<table border='1' style='border-collapse: collapse; width: 100%;'>")
            .append("<tr><th style='width: 5%;'>SI No</th>")
            .append("<th style='width: 10%;'>Date & Time (Hrs)</th>")
            .append("<th style='width: 30%;'>Division/Group/Project</th>")
            .append("<th style='width: 30%;'>Auditor</th>")
            .append("<th style='width: 25%;'>Auditee</th>");

			
		    AuditeetableContent.append("<tr><td style='text-align: center;'>").append(1).append("</td>")
            .append("<td style='text-align: center;'>").append(FormatConverter.getDateTimeFormat(dto.getScheduleDate())).append("</td>")
            .append("<td>").append((dto.getGroupName() != ""?dto.getGroupName():dto.getDivisionName() != ""?dto.getDivisionName():dto.getProjectName() != ""?dto.getProjectName():" - ")).append("</td>")
            .append("<td>").append("<span style='font-weight: bolder;'>"+dto.getTeamCode()+"</span><br />");
            for( int i = 0;i<teamMemberDetails.size();i++){
         		EmployeeDto employee =	NFormatConvertion.getEmployeeDetails(Long.parseLong(teamMemberDetails.get(i)[1].toString()),totalEmployee);
         		if(i < (teamMemberDetails.size() - 1)) {
             		AuditeetableContent.append(employee != null?"<li>"+employee.getEmpName()+", "+employee.getEmpDesigName()+","+"</li>":"");
         		}else {
             		AuditeetableContent.append(employee != null?"<li>"+employee.getEmpName()+", "+employee.getEmpDesigName()+"</li>":"");
         		}
             }
			                   
		    AuditeetableContent.append("</td>")
			                   .append("<td>").append(dto.getAuditeeEmpName()).append("</td></tr>");
			
		    AuditeetableContent.append("</table>");

	    	Auditee auditee = auditeeRepository.findById(dto.getAuditeeId()).get();
	    	result = insertScheduleNomination(auditee.getEmpId(),login.getEmpId(),username,url,NotiMsg);
			EmployeeDto auditeeDetails =	NFormatConvertion.getEmployeeDetails(auditee.getEmpId(),totalEmployee);
			if(auditeeDetails!=null && auditeeDetails.getEmail() !=null && !(auditeeDetails.getEmail().equalsIgnoreCase("a@lrde.com"))) {	
				sendHtmlMessage(auditeeDetails.getEmail(),"Audit Schedule of "+dto.getIqaNo(), AuditeetableContent.toString(), heading, note);
			}
    	
			for(Object[] obj : teamMemberDetails) {
				result = insertScheduleNomination(Long.parseLong(obj[1].toString()),login.getEmpId(),username,url,NotiMsg);
				EmployeeDto employee =	NFormatConvertion.getEmployeeDetails(Long.parseLong(obj[1].toString()),totalEmployee);
				if(employee!=null && employee.getEmail() !=null && !(employee.getEmail().equalsIgnoreCase("a@lrde.com"))) {	
					sendHtmlMessage(employee.getEmail(),"Audit Schedule of "+dto.getIqaNo(), tableContent.toString(), heading, note);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error( " Inside sendRescheduleMail Service " );
		}
		return result;
	}
	
	public void sendHtmlMessage(String to, String subject, String tableContent, String heading, String note) {
	    try {
	    	
	        String htmlMessage = "<html>"
            + "<body>"
            + heading
            + tableContent 
            + note
            + "</body>"
            + "</html>";
	        
			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", host);
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

			
			Session session = Session.getInstance(properties,new jakarta.mail.Authenticator() {
					    							protected PasswordAuthentication getPasswordAuthentication() {
					    							return new PasswordAuthentication(username1, password);
					                               }});

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username1));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(htmlMessage);
			message.setContent(htmlMessage, "text/html");
		
		Transport.send(message);
		
		Thread.sleep(10000);
	    }catch (MessagingException | InterruptedException mex) {
				mex.printStackTrace();
		        logger.error("Inside sendHtmlMessage Service", mex);
		}
	}

	
//	public void sendSimpleMessage(String to, String subject, String text) {
//	    try {
//		 SimpleMailMessage message = new SimpleMailMessage(); 
//		        message.setTo(to); 
//		        message.setSubject(subject); 
//		        message.setText(text);
//		    emailSender.send(message);
//		       
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error( " Inside sendSimpleMessage Service " );
//		}	        
//	}
	
	public long insertScheduleNomination(Long id,Long LoginEmpId  , String username, String url, String message) {
		try {			
			ImsNotification notification=new ImsNotification();

			notification.setNotificationby(LoginEmpId);
			notification.setIsActive(1);
			notification.setCreatedBy(username);
			notification.setCreatedDate(LocalDateTime.now());
			notification.setNotificationMessage(message);
			notification.setNotificationDate(LocalDateTime.now());
			notification.setEmpId(id);
			notification.setNotificationUrl(url);
				
			return notificationRepo.save(notification).getNotificationId();
			}catch (Exception e) {
			e.printStackTrace();
			logger.error(" Inside insertRepNomination Service " + username);
			return 0;
		}
		
	}
	
	public List<String> getProjects(Long empId,List<ProjectMasterDto> totalProject,List<ProjectEmployeeDto> projectEmp) {
		
		Set<Long> projectIds = projectEmp.stream().filter(data -> data.getEmpId().equals(empId)).map(ProjectEmployeeDto::getProjectId).collect(Collectors.toSet());
		return totalProject.stream().filter(data -> projectIds.contains(data.getProjectId())).map(ProjectMasterDto::getProjectCode).collect(Collectors.toList());
	}
	
	public List<String> getDivisions(Long empId,List<DivisionMasterDto> divisionMaster,List<DivisionEmployeeDto> divisionEmp) {
		Set<Long> divisions = divisionEmp.stream().filter(data -> data.getEmpId().equals(empId)).map(DivisionEmployeeDto::getDivisionId).collect(Collectors.toSet());
		return divisionMaster.stream().filter(data -> divisions.contains(data.getDivisionId())).map(DivisionMasterDto::getDivisionCode).collect(Collectors.toList());
	}
	
	public List<String> getGroups(Long empId,List<DivisionGroupDto> groupList) {
		return groupList.stream().filter(data -> data.getGroupHeadId().equals(empId)).map(DivisionGroupDto::getGroupCode).collect(Collectors.toList());
	}


	@Override
	public List<AuditTotalTeamMembersDto> getTotalTeamMembersList() throws Exception {
		logger.info( " AuditServiceImpl Inside method getTotalTeamMembersList()");
		try {
			List<Object[]> totalTeamMEmbersList = teamRepository.getTotalTeamMemberDetails();
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			List<DivisionMasterDto> divisionMaster = masterClient.getDivisionMaster(xApiKey);
			List<ProjectMasterDto> totalProject = masterClient.getProjectMasterList(xApiKey);
			List<DivisionGroupDto> groupList = masterClient.getDivisionGroupList(xApiKey);
			
			List<DivisionEmployeeDto> divisionEmp = masterClient.getDivisionEmpDetailsById(xApiKey);
			List<ProjectEmployeeDto> projectEmp = masterClient.getProjectEmpDetailsById(xApiKey);
			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
			 List<AuditTotalTeamMembersDto> result = Optional.ofNullable(totalTeamMEmbersList).orElse(Collections.emptyList()).stream()
				    .map(obj -> {
					    EmployeeDto employee =	obj[0] != null?employeeMap.get(Long.parseLong(obj[0].toString())):null;
					    List<String> groups =  obj[0] != null?getGroups(Long.parseLong(obj[0].toString()),groupList):null;
					    List<String> divisions =  obj[0] != null?getDivisions(Long.parseLong(obj[0].toString()),divisionMaster,divisionEmp):null;
					    List<String> projects =  obj[0] != null?getProjects(Long.parseLong(obj[0].toString()),totalProject,projectEmp):null;
					    	return AuditTotalTeamMembersDto.builder()
					    		.empId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
					    		.iqaId(obj[1]!=null?Long.parseLong(obj[1].toString()):0L)
				    			.teamId(obj[2]!=null?Long.parseLong(obj[2].toString()):0L)
				    			.isLead(obj[3]!=null?Long.parseLong(obj[3].toString()):0L)
				    			.auditorId(obj[4]!=null?Long.parseLong(obj[4].toString()):0L)
				    			.teamMemberId(obj[5]!=null?Long.parseLong(obj[5].toString()):0L)
				    			.empName(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"")
				    			.groups(groups!=null?groups:Collections.emptyList())
				    			.divisions(divisions!=null?divisions:Collections.emptyList())
				    			.projects(projects!=null?projects:Collections.emptyList())
				    			.build();
				    })
				    .collect(Collectors.toList());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getTotalTeamMembersList()"+ e);
			 return Collections.emptyList();
		}
	}

	@Override
	public long rescheduleMailSend(AuditRescheduleDto auditRescheduleDto, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method rescheduleMailSend()");
	    long result = 0;
	    try {
	    	Login login = loginRepo.findByUsername(username);
			EmployeeDto employeeLogIn = masterClient.getEmployee(xApiKey,login.getEmpId()).get(0);
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
	    	
	    	AuditScheduleListDto auditScheduleListDto = auditRescheduleDto.getAuditScheduleListDto();
	    	
			String url= "/schedule-approval";
			String NotiMsg = auditScheduleListDto.getIqaNo()+" Of Audit Schedule Reforwarded by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
			
			result = sendRescheduleMail(auditScheduleListDto,username,login,url,NotiMsg,totalEmployee);

	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method rescheduleMailSend() " + e);
	    }
	    return result;
	}

	public List<AuditorTeamDto> getAuditTeamMainList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getAuditTeamList()");
		try {
			List<AuditTeam> auditTeamList=teamRepository.findAllByIsActive(1);
			List<AuditorTeamDto> finalAuditTeamList  = auditTeamList.stream()
				    .map(obj -> {
				    	AuditorTeamDto dto = new AuditorTeamDto();
				        dto.setIqaId(obj.getIqaId());
				        dto.setTeamId(obj.getTeamId());
				        dto.setTeamCode(obj.getTeamCode());
				        dto.setIsActive(obj.getIsActive());
				        return dto;
				    })
				    .collect(Collectors.toList());
			return finalAuditTeamList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditTeamList()"+ e);
			return Collections.emptyList();
		}
	}
	
	
	@Override
	public List<AuditorDto> getAuditorIsActiveList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getAuditorList()");
		try {
			List<Auditor> auditors = auditRepository.findAllByIsActive(1);
			List<EmployeeDto> employeeList=masterClient.getEmployeeList("VTS");
		    Map<Long, EmployeeDto> employeeMap = employeeList.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
			List<AuditorDto> finalDto = auditors.stream()
				    .map(obj -> {
				        AuditorDto auditorDto = new AuditorDto();
				       // EmployeeDto employeeDto = masterClient.getEmployee("VTS", obj.getEmpId()).get(0);
				        EmployeeDto employeeDto =  employeeMap.get(obj.getEmpId());
				        auditorDto.setEmpId(obj.getEmpId());
				        auditorDto.setEmpName(employeeDto.getEmpName());
				        auditorDto.setDesignation(employeeDto.getEmpDesigName());
				        auditorDto.setDivisionName(employeeDto.getEmpDivCode());
				        auditorDto.setAuditorId(obj.getAuditorId());
				        return auditorDto;
				    })
				    .collect(Collectors.toList());
			return finalDto;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditorList()"+ e);
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<AuditTeamMembersDto> getTeamMmberIsActiveList() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getTeamMmberIsActiveList()");
		try {
			List<AuditTeamMembers> teamMembers = teamMemberRepository.findAllByIsActive(1);
			List<AuditTeam> auditTeamList=teamRepository.findAllByIsActive(1);
			Map<Long, AuditTeam> auditTeamMap = auditTeamList.stream()
		            .filter(auditteam -> auditteam.getTeamId() != null)
		            .collect(Collectors.toMap(AuditTeam::getTeamId, auditteam -> auditteam));
			List<AuditTeamMembersDto> finalDto = teamMembers.stream()
				    .map(obj -> {
				    	AuditTeam team= auditTeamMap.get(obj.getTeamId());
				    	AuditTeamMembersDto teamMemberDto= new AuditTeamMembersDto();
				    	teamMemberDto.setTeamMemberId(obj.getTeamMemberId());
				    	teamMemberDto.setAuditorId(obj.getAuditorId());
				    	teamMemberDto.setTeamId(obj.getTeamId());
				    	teamMemberDto.setIIsLead(obj.getIIsLead());	
				    	teamMemberDto.setIqaId(team.getIqaId());
				    	return teamMemberDto;
				    })
				    .collect(Collectors.toList());
			return finalDto;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getTeamMmberIsActiveList()"+ e);
			return Collections.emptyList();
		}
		
	}
	
	
	@Modifying
    @Transactional
	@Override
	public long insertAuditTeam(AuditorTeamDto auditormemberteamdto, String username) throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method insertAuditTeam()");
		long result=0;
		try {
			if(auditormemberteamdto!=null && auditormemberteamdto.getTeamId()!=null) {
				int delete =teamMemberRepository.deleteByTeamId(auditormemberteamdto.getTeamId());
				if(delete>0) {
					AuditTeamMembers teamleadmodel=new AuditTeamMembers();
					teamleadmodel.setTeamId(auditormemberteamdto.getTeamId());
					teamleadmodel.setAuditorId(auditormemberteamdto.getTeamLeadEmpId());
					teamleadmodel.setIIsLead(1L);
					teamleadmodel.setCreatedBy(username);
					teamleadmodel.setCreatedDate(LocalDateTime.now());
					teamleadmodel.setIsActive(1);
					teamleadmodel.setIsActive(1);
					result=teamMemberRepository.save(teamleadmodel).getTeamMemberId();
					if(auditormemberteamdto.getTeamMemberEmpId()!=null && auditormemberteamdto.getTeamMemberEmpId().length>0) {
						for(int i=0;i<auditormemberteamdto.getTeamMemberEmpId().length;i++) {
							AuditTeamMembers teammembermodel=new AuditTeamMembers();
							teammembermodel.setTeamId(auditormemberteamdto.getTeamId());
							teammembermodel.setAuditorId(auditormemberteamdto.getTeamMemberEmpId()[i]);
							teammembermodel.setIIsLead(0L);
							teammembermodel.setCreatedBy(username);
							teammembermodel.setCreatedDate(LocalDateTime.now());
							teammembermodel.setIsActive(1);
							teamMemberRepository.save(teammembermodel);
						}
					}
				}
			}else {
				AuditTeam model=new AuditTeam();
				model.setTeamCode(auditormemberteamdto.getTeamCode());
				model.setIqaId(auditormemberteamdto.getIqaId());
				model.setCreatedBy(username);
				model.setCreatedDate(LocalDateTime.now());
				model.setIsActive(1);
				result=teamRepository.save(model).getTeamId();
				if(result>0) {
				AuditTeamMembers teamleadmodel=new AuditTeamMembers();
				teamleadmodel.setTeamId(result);
				teamleadmodel.setAuditorId(auditormemberteamdto.getTeamLeadEmpId());
				teamleadmodel.setIIsLead(1L);
				teamleadmodel.setCreatedBy(username);
				teamleadmodel.setCreatedDate(LocalDateTime.now());
				teamleadmodel.setIsActive(1);
				teamleadmodel.setIsActive(1);
				teamMemberRepository.save(teamleadmodel);
				if(auditormemberteamdto.getTeamMemberEmpId()!=null && auditormemberteamdto.getTeamMemberEmpId().length>0) {
					for(int i=0;i<auditormemberteamdto.getTeamMemberEmpId().length;i++) {
						AuditTeamMembers teammembermodel=new AuditTeamMembers();
						teammembermodel.setTeamId(result);
						teammembermodel.setAuditorId(auditormemberteamdto.getTeamMemberEmpId()[i]);
						teammembermodel.setIIsLead(0L);
						teammembermodel.setCreatedBy(username);
						teammembermodel.setCreatedDate(LocalDateTime.now());
						teammembermodel.setIsActive(1);
						teamMemberRepository.save(teammembermodel);
					}
				}
			  }
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method insertAuditTeam()"+ e);
			return result;
		}
	}
	
	
	@Override
	public List<AuditTeamEmployeeDto> getauditteammemberlist() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getauditteammemberlist()");
		try {
			List<Object[]> getAuditTeamMemberList=teamRepository.getAuditTeamMemberList();
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList("VTS");
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
			List<AuditTeamEmployeeDto> finalAuditTeamMemberList  = getAuditTeamMemberList.stream()
					.map(obj -> {
					    EmployeeDto employee =	obj[0] != null?employeeMap.get(obj[0]):null;
					    AuditTeamEmployeeDto dto = new AuditTeamEmployeeDto();
					    dto.setTeamMemberIds(Long.parseLong(obj[6].toString()));
					    dto.setTeamId(Long.parseLong(obj[5].toString()));
					    dto.setTeamMembers(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"");
					    dto.setIsLead(Long.parseLong(obj[3].toString()));
					    dto.setAuditorId(Long.parseLong(obj[4].toString()));
				        return dto;
				    })
				    .collect(Collectors.toList());
			return finalAuditTeamMemberList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getauditteammemberlist()"+ e);
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<AuditScheduleListDto> getScheduleApprovalList(String username) throws Exception {
		logger.info( " AuditServiceImpl Inside method getScheduleApprovalList()");
		try {
			Login login = loginRepo.findByUsername(username);
			List<Object[]> scheduleList = auditScheduleRepository.getScheduleApprovalList(login.getEmpId(),login.getImsFormRoleId());
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			List<DivisionMasterDto> divisionMaster = masterClient.getDivisionMaster(xApiKey);
			List<ProjectMasterDto> totalProject = masterClient.getProjectMasterList(xApiKey);
			List<DivisionGroupDto> groupList = masterClient.getDivisionGroupList(xApiKey);
			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
		    Map<Long,DivisionMasterDto> divisionMap = divisionMaster.stream()
		    		.filter(division -> division.getDivisionId() !=null)
		    		.collect(Collectors.toMap(DivisionMasterDto::getDivisionId, division -> division));
		    
		    Map<Long,ProjectMasterDto> projectMap = totalProject.stream()
		    		.filter(project -> project.getProjectId()!=null)
		    		.collect(Collectors.toMap(ProjectMasterDto::getProjectId, project -> project));
		    
		    Map<Long,DivisionGroupDto> groupMap = groupList.stream()
		    		.filter(group -> group.getGroupId() !=null)
		    		.collect(Collectors.toMap(DivisionGroupDto::getGroupId, group -> group));
		    
			 List<AuditScheduleListDto> finalScheduleDtoList = Optional.ofNullable(scheduleList).orElse(Collections.emptyList()).stream()
				    .map(obj -> {
					    EmployeeDto employee =	obj[5] != null?employeeMap.get(Long.parseLong(obj[5].toString())):null;
					    DivisionMasterDto division = (obj[6]!=null && !obj[6].toString().equalsIgnoreCase("0"))?divisionMap.get(Long.parseLong(obj[6].toString())):null;
					    DivisionGroupDto group = (obj[7]!=null && !obj[7].toString().equalsIgnoreCase("0"))?groupMap.get(Long.parseLong(obj[7].toString())):null;
					    ProjectMasterDto project = (obj[8]!=null && !obj[8].toString().equalsIgnoreCase("0"))?projectMap.get(Long.parseLong(obj[8].toString())):null;
					    	
					    AuditScheduleListDto dto = new AuditScheduleListDto();

					    dto.setScheduleId(obj[0] != null ? Long.parseLong(obj[0].toString()) : 0L);
					    dto.setScheduleDate(obj[1] != null ? obj[1].toString() : "");
					    dto.setAuditeeId(obj[2] != null ? Long.parseLong(obj[2].toString()) : 0L);
					    dto.setTeamId(obj[3] != null ? Long.parseLong(obj[3].toString()) : 0L);
					    dto.setTeamCode(obj[4] != null ? obj[4].toString() : "");
					    dto.setAuditeeEmpId(obj[5] != null ? Long.parseLong(obj[5].toString()) : 0L);
					    dto.setDivisionId(obj[6] != null ? Long.parseLong(obj[6].toString()) : 0L);
					    dto.setGroupId(obj[7] != null ? Long.parseLong(obj[7].toString()) : 0L);
					    dto.setProjectId(obj[8] != null ? Long.parseLong(obj[8].toString()) : 0L);
					    dto.setRevision(obj[9] != null ? Integer.parseInt(obj[9].toString()) : 0);
					    dto.setScheduleStatus(obj[10] != null ? obj[10].toString() : "");
					    dto.setIqaId(obj[11] != null ? Long.parseLong(obj[11].toString()) : 0L);
					    dto.setStatusName(obj[12] != null ? obj[12].toString() : "");
					    dto.setIqaNo(obj[13] != null ? obj[13].toString() : "");
					    dto.setRemarks(obj[14] != null ? obj[14].toString() : "NA");
					    dto.setActEmpId(obj[15] != null ? Long.parseLong(obj[15].toString()) : 0L);
					    dto.setLoginEmpId(obj[16] != null ? Long.parseLong(obj[16].toString()) : 0L);
					    dto.setLeadEmpId(obj[17] != null ? Long.parseLong(obj[17].toString()) : 0L);
					    dto.setAuditeeFlag(obj[18] != null ? obj[18].toString() : "");
					    dto.setFwdFlag(obj[19] != null ? Long.parseLong(obj[19].toString()) : 0L);
					    dto.setAuditeeEmpName(employee != null ? employee.getEmpName() + ", " + employee.getEmpDesigName() : "");
					    dto.setDivisionName(division != null ? division.getDivisionName() : "");
					    dto.setGroupName(group != null ? group.getGroupName() : "");
					    dto.setProjectName(project != null ? project.getProjectName() : "");
					    dto.setDivisionCode(division != null ? division.getDivisionCode() : "");
					    dto.setGroupCode(group != null ? group.getGroupCode() : "");
					    dto.setProjectCode(project != null ? project.getProjectCode() : "");
					    dto.setProjectShortName(project != null ? project.getProjectShortName() : "");
					    dto.setScope(obj[20] != null ? obj[20].toString() : "");

					    
					    return dto;
				    })
				    .collect(Collectors.toList());
			return finalScheduleDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getScheduleApprovalList()"+ e);
			 return Collections.emptyList();
		}
	}


	@Override
	public List<AuditScheduleRemarksDto> getScheduleRemarks() throws Exception {
		logger.info( " AuditServiceImpl Inside method getScheduleRemarks()");
		try {
			List<Object[]> scheduleRemarks = auditTransactionRepository.getScheduleRemarks();
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
			 List<AuditScheduleRemarksDto> finalscheduleRemarksDto = Optional.ofNullable(scheduleRemarks).orElse(Collections.emptyList()).stream()
				    .map(obj -> {
					    EmployeeDto employee =	obj[0] != null?employeeMap.get(Long.parseLong(obj[0].toString())):null;
					    	return AuditScheduleRemarksDto.builder()
				    			.empId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
				    			.empName(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"")
				    			.transactionDate(obj[1]!=null?obj[1].toString():"")
				    			.remarks(obj[2]!=null?obj[2].toString():"")
				    			.StatusName(obj[3]!=null?obj[3].toString():"")
				    			.scheduleId(obj[4]!=null?Long.parseLong(obj[4].toString()):0L)
				    			.build();
				    })
				    .collect(Collectors.toList());
			return finalscheduleRemarksDto;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getScheduleRemarks()"+ e);
			 return Collections.emptyList();
		}
	}


	@Override
	public List<AuditTranDto> scheduleTran(AuditTranDto auditTranDto) throws Exception {
		logger.info( " AuditServiceImpl Inside method scheduleTran()");
		try {
			List<Object[]> tranList = auditScheduleRepository.getScheduleTran(auditTranDto.getId(),auditTranDto.getAuditType());
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);

			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
			 List<AuditTranDto> auditTranDtoList = Optional.ofNullable(tranList).orElse(Collections.emptyList()).stream()
				    .map(obj -> {
					    EmployeeDto employee =	obj[0] != null?employeeMap.get(Long.parseLong(obj[0].toString())):null;

					    	return AuditTranDto.builder()
				    			.empId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
				    			.auditStatus(obj[1]!=null?obj[1].toString():"")
				    			.transactionDate(obj[2]!=null?obj[2].toString():"")
				    			.remarks(obj[3]!=null?obj[3].toString():"")
				    			.statusName(obj[4]!=null?obj[4].toString():"")
				    			.empName(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"")
				    			.build();
				    })
				    .collect(Collectors.toList());
			return auditTranDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method scheduleTran()"+ e);
			 return Collections.emptyList();
		}
	}
	
	
	@Override
	public List<IqaAuditeeDto> getIqaAuditeeList(Long iqaId) throws Exception {
		logger.info( " AuditServiceImpl Inside method getAuditeeList()");
		try {
			List<Object[]> iqaAuditeeList = iqaAuditeeRepository.iqaAuditeeList(iqaId); 
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			List<DivisionMasterDto> divisionMaster = masterClient.getDivisionMaster(xApiKey);
			List<ProjectMasterDto> totalProject = masterClient.getProjectMasterList(xApiKey);
			List<DivisionGroupDto> groupList = masterClient.getDivisionGroupList(xApiKey);
			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
		    Map<Long,DivisionMasterDto> divisionMap = divisionMaster.stream()
		    		.filter(division -> division.getDivisionId() !=null)
		    		.collect(Collectors.toMap(DivisionMasterDto::getDivisionId, division -> division));
		    
		    Map<Long,ProjectMasterDto> projectMap = totalProject.stream()
		    		.filter(project -> project.getProjectId()!=null)
		    		.collect(Collectors.toMap(ProjectMasterDto::getProjectId, project -> project));
		    
		    Map<Long,DivisionGroupDto> groupMap = groupList.stream()
		    		.filter(group -> group.getGroupId() !=null)
		    		.collect(Collectors.toMap(DivisionGroupDto::getGroupId, group -> group));
			
			List<IqaAuditeeDto> finalAuditeeDtoList = iqaAuditeeList.stream()
					.map(obj -> {
					    EmployeeDto employee =	obj[0] != null?employeeMap.get(obj[0]):null;
					    DivisionMasterDto division = (obj[3]!=null && !obj[3].toString().equalsIgnoreCase("0"))?divisionMap.get(obj[3]):null;
					    DivisionGroupDto group = (obj[4]!=null && !obj[4].toString().equalsIgnoreCase("0"))?groupMap.get(obj[4]):null;
					    ProjectMasterDto project = (obj[5]!=null && !obj[5].toString().equalsIgnoreCase("0"))?projectMap.get(obj[5]):null;
				    
					    IqaAuditeeDto dto = new IqaAuditeeDto();
					    dto.setAuditeeId(Long.parseLong(obj[1].toString()));
				    	dto.setAuditee(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"");
				    	dto.setDivisionName(division !=null?division.getDivisionCode():"");
				    	dto.setGroupName(group !=null?group.getGroupCode():"");
				    	dto.setProjectName(project !=null?project.getProjectName():"");
				    	dto.setProjectCode(project !=null?project.getProjectCode():"");
				        return dto;
				    })
				    .collect(Collectors.toList());
			return finalAuditeeDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditeeList()"+ e);
			 return Collections.emptyList();
		}
	}
	
	@Override
	public long insertIqaAuditee(IqaAuditeeDto iqaAuditeeDto, String username) throws Exception {
		logger.info(" AuditServiceImpl Inside method insertIqaAuditee()");
		long result=0;
		try {
			if(iqaAuditeeDto!=null) {
			List<Object[]> iqaAuditeeList = iqaAuditeeRepository.iqaAuditeeList(iqaAuditeeDto.getIqaId()); 
			if(iqaAuditeeList.size()>0) {
				iqaAuditeeRepository.deleteByIqaId(iqaAuditeeDto.getIqaId());
			}
			 if (iqaAuditeeDto.getAuditeeIds() != null && iqaAuditeeDto.getAuditeeIds().length > 0) {
		            List<IqaAuditee> iqaAuditeeInsertList = Arrays.stream(iqaAuditeeDto.getAuditeeIds())
		                .map(auditeeId -> {
		                    IqaAuditee model = new IqaAuditee();
		                    model.setAuditeeId(Long.parseLong(auditeeId));
		                    model.setIqaId(iqaAuditeeDto.getIqaId());
		                    model.setCreatedBy(username);
		                    model.setCreatedDate(LocalDateTime.now());
		                    return model;
		                })
		                .collect(Collectors.toList());
		            result = iqaAuditeeRepository.saveAll(iqaAuditeeInsertList).size();
		        } 	
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method insertIqaAuditee()"+ e);
			return result;
		}
	}


	
	@Override
	public List<IqaAuditeeListDto> getIqaAuditeelist() throws Exception {
		logger.info(" AuditServiceImpl Inside method getAuditeeList()");
		try {
			List<Object[]> result = auditeeRepository.getIqaAuditeeList(); 
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
			List<DivisionMasterDto> divisionMaster = masterClient.getDivisionMaster(xApiKey);
			List<ProjectMasterDto> totalProject = masterClient.getProjectMasterList(xApiKey);
			List<DivisionGroupDto> groupList = masterClient.getDivisionGroupList(xApiKey);
			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
		    Map<Long,DivisionMasterDto> divisionMap = divisionMaster.stream()
		    		.filter(division -> division.getDivisionId() !=null)
		    		.collect(Collectors.toMap(DivisionMasterDto::getDivisionId, division -> division));
		    
		    Map<Long,ProjectMasterDto> projectMap = totalProject.stream()
		    		.filter(project -> project.getProjectId()!=null)
		    		.collect(Collectors.toMap(ProjectMasterDto::getProjectId, project -> project));
		    
		    Map<Long,DivisionGroupDto> groupMap = groupList.stream()
		    		.filter(group -> group.getGroupId() !=null)
		    		.collect(Collectors.toMap(DivisionGroupDto::getGroupId, group -> group));
			
			List<IqaAuditeeListDto> finalAuditeeDtoList = result.stream()
					.map(obj -> {
					    EmployeeDto employee       =	obj[1] != null?employeeMap.get(Long.parseLong(obj[1].toString())):null;
					    DivisionMasterDto division =	(obj[2] != null && !obj[2].toString().equalsIgnoreCase("0"))?divisionMap.get(Long.parseLong(obj[2].toString())):null;
					    DivisionGroupDto group     =	(obj[3] != null && !obj[3].toString().equalsIgnoreCase("0"))?groupMap.get(Long.parseLong(obj[3].toString())):null;
					    ProjectMasterDto project   =	(obj[4] != null && !obj[4].toString().equalsIgnoreCase("0"))?projectMap.get(Long.parseLong(obj[4].toString())):null;
				    
					   return  IqaAuditeeListDto.builder()
					    	  .auditeeId(obj[0] != null?Long.parseLong(obj[0].toString()):0L)
					    	  .empId(obj[1] != null?Long.parseLong(obj[1].toString()):0L)
					    	  .groupId(obj[2] != null?Long.parseLong(obj[2].toString()):0L)
					    	  .divisionId(obj[3] != null?Long.parseLong(obj[3].toString()):0L)
					    	  .projectId(obj[4] != null?Long.parseLong(obj[4].toString()):0L)
					    	  .iqaId(obj[5] != null?Long.parseLong(obj[5].toString()):0L)
					    	  .iqaAuditeeId(obj[6] != null?Long.parseLong(obj[6].toString()):0L)
					    	  .auditee(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"")
					    	  .divisionName(division !=null?division.getDivisionCode():"")
					    	  .divisionFullName(division !=null?division.getDivisionName():"")
					    	  .groupName(group !=null?group.getGroupCode():"")
					    	  .groupFullName(group !=null?group.getGroupName():"")
					    	  .projectName(project !=null?project.getProjectName():"")
					    	  .projectShortName(project !=null?project.getProjectShortName():"")
					    	  .projectCode(project !=null?project.getProjectCode():"")
					    	  .divisionHeadName(division !=null?division.getDivHeadName()+", "+division.getDivHeadDesig():"")
					    	  .groupHeadName(group !=null?group.getGroupHeadName()+", "+group.getGroupHeadDesig():"")
					    	  .projectDirectorName(project !=null?project.getPrjDirectorName()+", "+project.getPrjDirectorDesig():"")
					    	  .divisionHeadId(division !=null?division.getDivisionHeadId():0L)
					    	  .groupHeadId(group !=null?group.getGroupHeadId():0L)
					    	  .projectDirectorId(project !=null?project.getProjectDirector():0L)
					    	  .build();
				    })
				    .collect(Collectors.toList());
				
			return finalAuditeeDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditeeList()"+ e);
			 return Collections.emptyList();
		}
	}


	@Override
	public List<AuditObservation> getObservation() throws Exception {
		logger.info( " AuditServiceImpl Inside method getObservation()");
		try {

			return auditObservationRepository.findByIsActive(1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getObservation()"+ e);
			return List.of();
		}
	}

	@Override
	public int addAuditCheckList(AuditCheckListDTO auditCheckListDTO, String username) throws Exception {
		int result = 1;
		logger.info(" AuditServiceImpl Inside method addAuditCheckList()");
		try {

			for(CheckListItem item  : auditCheckListDTO.getCheckListMap()){
//				AuditCheckList checkList = new AuditCheckList();
//				checkList.setScheduleId((long)auditCheckListDTO.getScheduleId());			
//				checkList.setIqaId((long)auditCheckListDTO.getIqaId());			
//				checkList.setMocId((long)item.getMocId());			
//				checkList.setAuditObsId((long)item.getObservation());
//				checkList.setAuditorRemarks(item.getAuditorRemarks());
//				checkList.setCreatedBy(username);
//				checkList.setCreatedDate(LocalDateTime.now());
//				checkList.setIsActive(1);
				result = auditCheckListRepository.updateAuditorRemarks(item.getObservation(),item.getAuditorRemarks(),username,LocalDateTime.now(),item.getAuditCheckListId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method addAuditCheckList()"+ e);
		}
		return result;
	}
	
	@Override
	public long addAuditeeRemarks(List<MultipartFile> files,AuditCheckListDTO auditCheckListDTO, String username) throws Exception {
		long result = 1;
		logger.info( " AuditServiceImpl Inside method addAuditeeRemarks()");
		try {

			Timestamp instant = Timestamp.from(Instant.now());
			String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");
			for(CheckListItem item  : auditCheckListDTO.getCheckListMap()){
				AuditCheckList checkList = new AuditCheckList();
				checkList.setScheduleId((long)auditCheckListDTO.getScheduleId());			
				checkList.setIqaId((long)auditCheckListDTO.getIqaId());			
				checkList.setMocId((long)item.getMocId());			
				checkList.setAuditeeRemarks(item.getAuditeeRemarks());
				checkList.setMocDescription(item.getMocDescription());
				if(item.getAttachment().equalsIgnoreCase("")){
					checkList.setAttachment(item.getAttachment());
				}else {
					String[] attachment = item.getAttachment().split("\\.");
					checkList.setAttachment(attachment[0]+timestampstr+"."+attachment[1]);
				}
				checkList.setCreatedBy(username);
				checkList.setCreatedDate(LocalDateTime.now());
				checkList.setIsActive(1);
				
				result = auditCheckListRepository.save(checkList).getAuditCheckListId();
			}
			saveDocFilesUpload(files,auditCheckListDTO,timestampstr);
			int auditeeAdd = auditCheckListRepository.checkAuditeeFinalAdd(auditCheckListDTO.getScheduleId());
			if(auditeeAdd == 1) {
	    	    Login login = loginRepo.findByUsername(username);
			    EmployeeDto employeeLogIn = masterClient.getEmployee(xApiKey,login.getEmpId()).get(0);
			    
		    	AuditSchedule schedule = auditScheduleRepository.findById((long)auditCheckListDTO.getScheduleId()).get();
		    	AuditTransaction trans = new AuditTransaction();
			    	
					
				schedule.setScheduleStatus("AES");
		    	schedule.setModifiedBy(username);
		    	schedule.setModifiedDate(LocalDateTime.now());
		    	result = auditScheduleRepository.save(schedule).getScheduleId();
		    	
		    	trans.setAuditStatus("AES");
				trans.setEmpId(login.getEmpId());
				trans.setScheduleId(result);
				trans.setTransactionDate(LocalDateTime.now());
				trans.setRemarks("NA");
				trans.setAuditType("S");
				
				auditTransactionRepository.save(trans);
				
				List<Object[]> members = teamMemberRepository.getTeamMembersByScheduleId(auditCheckListDTO.getScheduleId());
				String url= "/schedule-approval";
				String NotiMsg = members.get(0)[0] +" Of Audit Schedule Submitted by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
				
				for(Object[] obj: members) {
					result = insertScheduleNomination(Long.parseLong(obj[1].toString()),login.getEmpId(),username,url,NotiMsg);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method addAuditeeRemarks()"+ e);
		}
		return result;
	}

	@Override
	public long updateAuditCheckList(AuditCheckListDTO auditCheckListDTO, String username) throws Exception {
		long result = 1;
		logger.info( " AuditServiceImpl Inside method updateAuditCheckList()");
		try {

			for(CheckListItem item  : auditCheckListDTO.getCheckListMap()){
				AuditCheckList checkList = auditCheckListRepository.findById((long)item.getAuditCheckListId()).get();
				checkList.setMocId((long)item.getMocId());			
				checkList.setAuditObsId((long)item.getObservation());
				checkList.setAuditorRemarks(item.getAuditorRemarks());
				checkList.setModifiedBy(username);
				checkList.setModifiedDate(LocalDateTime.now());
				
				result = auditCheckListRepository.save(checkList).getAuditCheckListId();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method updateAuditCheckList()"+ e);
		}
		return result;
	}
	
	@Override
	public int updateAuditeeRemarks(List<MultipartFile> files,AuditCheckListDTO auditCheckListDTO, String username) throws Exception {
		int result = 1;
		logger.info( " AuditServiceImpl Inside method updateAuditeeRemarks()");
		try {
			Timestamp instant = Timestamp.from(Instant.now());
			String timestampstr = instant.toString().replace(" ", "").replace(":", "").replace("-", "").replace(".", "");
			for(CheckListItem item  : auditCheckListDTO.getCheckListMap()){
				AuditCheckList checkList = auditCheckListRepository.findById((long)item.getAuditCheckListId()).get();
	
				
				if(item.getAttachment().equalsIgnoreCase("")) {
					checkList.setAuditeeRemarks(item.getAuditeeRemarks());
					checkList.setModifiedBy(username);
					checkList.setModifiedDate(LocalDateTime.now());
					auditCheckListRepository.save(checkList);
					//result = auditCheckListRepository.updateAuditeeRemarksWithoutAttachment(item.getAuditeeRemarks(),username,LocalDateTime.now(),item.getAuditCheckListId());
				}else {		
					String[] attachment = item.getAttachment().split("\\.");
					String attachmentName = attachment[0]+timestampstr+"."+attachment[1];
					
					if(checkList.getAttachment().equalsIgnoreCase("")) {
						List<MultipartFile> fileNames = files.stream().filter(data -> data.getOriginalFilename().equalsIgnoreCase(item.getAttachment())).collect(Collectors.toList());
						if(fileNames.size() > 0) {
							saveEachFilesUpload(fileNames.get(0),auditCheckListDTO,timestampstr,"");
						}
					}else {
						List<MultipartFile> fileNames = files.stream().filter(data -> data.getOriginalFilename().equalsIgnoreCase(item.getAttachment())).collect(Collectors.toList());
						if(fileNames.size() > 0) {
							saveEachFilesUpload(fileNames.get(0),auditCheckListDTO,timestampstr,checkList.getAttachment());
						}
					}
					
					checkList.setAttachment(attachmentName);
					checkList.setAuditeeRemarks(item.getAuditeeRemarks());
					checkList.setModifiedBy(username);
					checkList.setModifiedDate(LocalDateTime.now());
					auditCheckListRepository.save(checkList);
					auditCheckListRepository.save(checkList);
							
					//result = auditCheckListRepository.updateAuditeeRemarks(item.getAuditeeRemarks(),attachmentName,username,LocalDateTime.now(),item.getAuditCheckListId());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method updateAuditeeRemarks()"+ e);
		}
		return result;
	}
	
	public long saveEachFilesUpload(MultipartFile file, AuditCheckListDTO auditCheckListDTO, String timestamp,String oldFile)throws Exception {
		long result = 1;
		logger.info( " AuditServiceImpl Inside method saveDocFilesUpload()");
		try {
				
				String orgNameExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String attachmentname = file.getOriginalFilename();
				if(!(attachmentname.equalsIgnoreCase("blob"))) {
				String iqaNo= auditCheckListDTO.getIqaNo().replace("/", "_")+" - "+auditCheckListDTO.getScheduleId();
				
					Path filePath = null;
					filePath = Paths.get(storageDrive,"CheckListUploads",iqaNo);
					
					logger.info(" Inside saveDocFilesUpload " +filePath);
			        File theDir = filePath.toFile();
			        if (!theDir.exists()){
					     theDir.mkdirs();
					 }
			        String[] fileName = attachmentname.split("\\.");
			        Path fileToSave = filePath.resolve(fileName[0]+timestamp + "." + orgNameExtension);
			        file.transferTo(fileToSave.toFile());
			        
			        if(!(oldFile.equalsIgnoreCase(""))){
				   		 File fileR = Paths.get(storageDrive,"CheckListUploads",iqaNo,oldFile).toFile();
				   		fileR.delete();
				     }
			        }
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method saveDocFilesUpload()"+ e);
		}
		return result;
	}


	@Override
	public List<CheckListDto> getAuditCheckList(String scheduleId) throws Exception {
		logger.info( " AuditServiceImpl Inside method getAuditCheckList()");
		try {
			 List<Object[]> result = auditCheckListRepository.getAuditCheckList(scheduleId);
			 			 
			return Optional.ofNullable(result).orElse(Collections.emptyList()).stream()
					    .map(obj -> {
						    	return CheckListDto.builder()
					    			.auditCheckListId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
					    			.scheduleId(obj[1]!=null?Long.parseLong(obj[1].toString()):0L)
					    			.iqaId(obj[2]!=null?Long.parseLong(obj[2].toString()):0L)
					    			.mocId(obj[3]!=null?Long.parseLong(obj[3].toString()):0L)
					    			.auditObsId(obj[4]!=null?Long.parseLong(obj[4].toString()):0L)
					    			.auditorRemarks(obj[5]!=null?obj[5].toString():"")
					    			.clauseNo(obj[6]!=null?obj[6].toString():"")
					    			.sectionNo(obj[7]!=null?obj[7].toString():"")
					    			.mocParentId(obj[8]!=null?Long.parseLong(obj[8].toString()):0L)
					    			.isForCheckList(obj[9]!=null?obj[9].toString():"")
					    			.description(obj[10]!=null?obj[10].toString():"")
					    			.auditeeRemarks(obj[11]!=null?obj[11].toString():"")
					    			.scheduleStatus(obj[12]!=null?obj[12].toString():"")
					    			.obsName(obj[13]!=null?obj[13].toString():"")
					    			.attachmentName(obj[14]!=null?obj[14].toString():"")
					    			.build();
					    })
					    .collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditCheckList()"+ e);
			return List.of();
		}
	}
	
	@Override
	public List<AuditCorrectiveActionDTO> getCarList() throws Exception {
		logger.info( " AuditServiceImpl Inside method getCarList()");
		try {
			 List<Object[]> result = auditCorrectiveActionRepository.getActionTotalList();
			 List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
			 
			return Optional.ofNullable(result).orElse(Collections.emptyList()).stream()
					    .map(obj -> {
					       EmployeeDto employee   =	obj[6] != null?employeeMap.get(Long.parseLong(obj[6].toString())):null;
						    	return AuditCorrectiveActionDTO.builder()
					    			.correctiveActionId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
					    			.auditCheckListId(obj[1]!=null?Long.parseLong(obj[1].toString()):0L)
					    			.iqaId(obj[2]!=null?Long.parseLong(obj[2].toString()):0L)
					    			.carRefNo(obj[3]!=null?obj[3].toString():"")
					    			.carDescription(obj[4]!=null?obj[4].toString():"")
					    			.actionPlan(obj[5]!=null?obj[5].toString():"")
					    			.responsibility(obj[6]!=null?Long.parseLong(obj[6].toString()):0L)
					    			.targetDate(obj[7]!=null?obj[7].toString():"")
					      			.scheduleId(obj[8]!=null?Long.parseLong(obj[8].toString()):0L)
					    			.auditeeId(obj[9]!=null?Long.parseLong(obj[9].toString()):0L)
					    			.carAttachment(obj[10]!=null?obj[10].toString():"")
					    			.rootCause(obj[11]!=null?obj[11].toString():"")
					    			.carCompletionDate(obj[12]!=null?obj[12].toString():"")
					    			.carDate(obj[13]!=null?obj[13].toString():"")
					    			.correctiveActionTaken(obj[14]!=null?obj[14].toString():"")
					    			.auditStatus(obj[15]!=null?obj[15].toString():"")
					    			.auditStatusName(obj[16]!=null?obj[16].toString():"")
					    			.auditeeEmpId(obj[17]!=null?Long.parseLong(obj[17].toString()):0L)
					    			.executiveName(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"")
					    			.build();
					    })
					    .collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getCarList()"+ e);
			return List.of();
		}
	}


	@Override
	public long uploadCheckListImage(MultipartFile file, Map<String, Object> response, String username)
			throws Exception {
		long result = 1;
		logger.info( " AuditServiceImpl Inside method uploadCheckListImage()");
		try {
			String orgNameExtension = FilenameUtils.getExtension(file.getOriginalFilename());
			String Attachmentname = FilenameUtils.removeExtension(response.get("checkListAttachementName").toString());
			String iqaNo= response.get("iqaNo").toString().replace("/", "_")+" - "+response.get("scheduleId").toString().replace("/", "_");
			
			List<Object[]> checkListUpload = auditCheckListRepository.getCheckListUpload(response.get("scheduleId").toString());
			if(checkListUpload.size() > 0) {
				 File fileR = Paths.get(storageDrive,"CheckListUploads",iqaNo,checkListUpload.get(0)[1].toString()).toFile();
				 if(fileR.delete()) {
					 auditCheckListRepository.updateUpload(response.get("checkListAttachementName").toString(),username,LocalDateTime.now(),checkListUpload.get(0)[0].toString());
				 }
				
			}else {
				AuditCheckList checkList = new AuditCheckList();
				checkList.setScheduleId(Long.parseLong(response.get("scheduleId").toString()));			
				checkList.setIqaId(Long.parseLong(response.get("iqaId").toString()));			
				checkList.setMocId(Long.parseLong(response.get("mocId").toString()));			
				checkList.setMocDescription(response.get("mocDescription").toString());			
				checkList.setAttachment(response.get("checkListAttachementName").toString());			
				checkList.setAuditObsId(0L);
				checkList.setAuditorRemarks("NA"); 
				checkList.setAuditeeRemarks("NA"); 
				checkList.setCreatedBy(username);
				checkList.setCreatedDate(LocalDateTime.now());
				checkList.setIsActive(1);
				
				result = auditCheckListRepository.save(checkList).getAuditCheckListId();
			}
				
				Path filePath = null;
				filePath = Paths.get(storageDrive,"CheckListUploads",iqaNo);
				
				logger.info(" Inside uploadCheckListImage " +filePath);
		        File theDir = filePath.toFile();
		        if (!theDir.exists()){
				     theDir.mkdirs();
				 }
		        Path fileToSave = filePath.resolve(Attachmentname + "." + orgNameExtension);
		        file.transferTo(fileToSave.toFile());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method uploadCheckListImage()"+ e);
		}
		return result;
	}


	@Override
	public String getCheckListimg(AuditScheduleListDto auditScheduleListDto) throws Exception {
		String result = null;
		logger.info( " AuditServiceImpl Inside method getCheckListimg()");
		try {
			List<Object[]> checkListUpload = auditCheckListRepository.getCheckListUpload(auditScheduleListDto.getScheduleId().toString());
			if(checkListUpload.size() > 0) {
				String iqaNo= auditScheduleListDto.getIqaNo().replace("/", "_")+" - "+auditScheduleListDto.getScheduleId();
	            Path imagePath = Paths.get(storageDrive,"CheckListUploads",iqaNo,checkListUpload.get(0)[1].toString());
	            byte[] imageBytes = Files.readAllBytes(imagePath);
	            result = "data:image/jpeg;base64,"+java.util.Base64.getEncoder().encodeToString(imageBytes);
			}else {
				result = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getCheckListimg()"+ e);
		}
		return result;
	}


	@Override
	public Long checkAuditorPresent(String auditorId) throws Exception {
		Long result = null;
		logger.info( " AuditServiceImpl Inside method checkAuditorPresent()");
		try {
			result = teamMemberRepository.countTeamMembersByAuditorId(auditorId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method checkAuditorPresent()"+ e);
		}
		return result;
	}


	@Override
	public int deleteAuditor(String auditorId) throws Exception {
		int result = 0;
		logger.info( " AuditServiceImpl Inside method deleteAuditor()");
		try {
			auditRepository.deleteById(Long.parseLong(auditorId));
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method deleteAuditor()"+ e);
		}
		return result;
	}

	@Override
	public List<CheckListDto> getAuditCheckListbyObsIds() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getAuditCheckListbyObsIds()");
		try {
			 List<Object[]> result = auditCheckListRepository.getAuditCheckListbyObsIds();
			 
			return Optional.ofNullable(result).orElse(Collections.emptyList()).stream()
					    .map(obj -> {
						    	return CheckListDto.builder()
					    			.auditCheckListId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
					    			.scheduleId(obj[1]!=null?Long.parseLong(obj[1].toString()):0L)
					    			.iqaId(obj[2]!=null?Long.parseLong(obj[2].toString()):0L)
					    			.mocId(obj[3]!=null?Long.parseLong(obj[3].toString()):0L)
					    			.auditObsId(obj[4]!=null?Long.parseLong(obj[4].toString()):0L)
					    			.auditorRemarks(obj[5]!=null?obj[5].toString():"")
					    			.clauseNo(obj[6]!=null?obj[6].toString():"")
					    			.sectionNo(obj[7]!=null?obj[7].toString():"")
					    			.mocParentId(obj[8]!=null?Long.parseLong(obj[8].toString()):0L)
					    			.isForCheckList(obj[9]!=null?obj[9].toString():"")
					    			.description(obj[10]!=null?obj[10].toString():"")
					    			.auditeeRemarks(obj[11]!=null?obj[11].toString():"")
					    			.scheduleStatus(obj[12]!=null?obj[12].toString():"")
					    			.carRefNo(obj[13]!=null?obj[13].toString():"")
					    			.build();
					    })
					    .collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditCheckListbyObsIds()"+ e);
			return List.of();
		}
	}
	@Override
	public List<CheckListDto> getMostFqNCMocDes(Long scheduleId, Integer auditObsId, Long iqaId) throws Exception {
	    logger.info(new Date() + " Inside method getMostFqNCMocDes()");
	    try {
	        List<Object[]> result = auditCheckListRepository.getMostFqNCMocDes(scheduleId, auditObsId, iqaId);
	        return result.stream()
	            .map(obj -> CheckListDto.builder()
	                .clauseNo(obj[0] != null ? obj[0].toString() : "")
	                .description(obj[1] != null ? obj[1].toString() : "")
	                .carRefNo(obj[2] != null ? obj[2].toString() : "")
	                .build()
	            ).collect(Collectors.toList());
	    } catch (Exception e) {
	        logger.error("Error in getMostFqNCMocDes: ", e);
	        throw e;
	    }
	}
	@Override
	public List<CheckListDto> getMostFreqNCDetails(Long mocId) throws Exception {
	    logger.info(new Date() + " Inside method getMostFreqNCDetails()");
	    try {
	        List<Object[]> result = auditCheckListRepository.getMostFreqNCDetails(mocId);
	        return result.stream()
	            .map(obj -> CheckListDto.builder()
	                .clauseNo(obj[4] != null ? obj[4].toString() : "")
	                .description(obj[3] != null ? obj[3].toString() : "")
	                .carRefNo(obj[6] != null ? obj[6].toString() : "")
	                .carStatus(obj[7] != null ? obj[7].toString() : "")
	                .auditorRemarks(obj[2] != null ? obj[2].toString() : "")
	                .build()
	            ).collect(Collectors.toList());
	    } catch (Exception e) {
	        logger.error("Error in getMostFreqNCDetails: ", e);
	        throw e;
	    }
	}


	@Override
	public List<CheckListDto> getMostFrequentNC() throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method getMostFrequentNC()");
		try {
			 List<Object[]> result = auditCheckListRepository.getMostFrequentNC();
			 System.out.println("result: " + result);
			return Optional.ofNullable(result).orElse(Collections.emptyList()).stream()
					    .map(obj -> {
						    	return CheckListDto.builder()
					    			//.auditCheckListId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
					    			//.scheduleId(obj[1]!=null?Long.parseLong(obj[1].toString()):0L)
					    			//.iqaId(obj[2]!=null?Long.parseLong(obj[2].toString()):0L)
					    			.mocId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
					    			//.auditObsId(obj[4]!=null?Long.parseLong(obj[4].toString()):0L)
					    			//.auditorRemarks(obj[5]!=null?obj[5].toString():"")
					    			.clauseNo(obj[3]!=null?obj[3].toString():"")
					    			.sectionNo(obj[4]!=null?obj[4].toString():"")
					    			.mocParentId(obj[5]!=null?Long.parseLong(obj[5].toString()):0L)
					    			.isForCheckList(obj[6]!=null?obj[6].toString():"")
					    			.description(obj[1]!=null?obj[1].toString():"")
					    			.ncCount(obj[2]!=null?Long.parseLong(obj[2].toString()):0L)
					    			//.auditeeRemarks(obj[11]!=null?obj[11].toString():"")
					    			//.scheduleStatus(obj[12]!=null?obj[12].toString():"")
					    			.build();
					    })
					    .collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getMostFrequentNC()"+ e);
			return List.of();
		}
	}

	@Override
	public int insertCorrectiveAction(List<AuditCarDTO> auditCarDTO, String username) throws Exception {
		int result = 0;
		logger.info( " AuditServiceImpl Inside method insertCorrectiveAction()");
		try {
			Login login = loginRepo.findByUsername(username);
			for(AuditCarDTO dto : auditCarDTO) {
			  result = auditCorrectiveActionRepository.updateActions(dto.getAction(),dto.getEmployee(),DLocalConvertion.converLocalTime(dto.getTargetDate()),LocalDateTime.now(),login.getEmpId(),username,LocalDateTime.now(),dto.getCorrectiveActionId());	
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method insertCorrectiveAction()"+ e);
		}
		return result;
	}
	@Override
	public int updateCorrectiveAction(AuditCarDTO auditCarDTO, String username) throws Exception {
		int result = 0;
		logger.info( " AuditServiceImpl Inside method updateCorrectiveAction()");
		try {
	
			  result = auditCorrectiveActionRepository.updateCarReport(auditCarDTO.getRootCause(),auditCarDTO.getCorrectiveActionTaken(),DLocalConvertion.converLocalTime(auditCarDTO.getCompletionDate()),username,LocalDateTime.now(),auditCarDTO.getCorrectiveActionId());	
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method updateCorrectiveAction()"+ e);
		}
		return result;

	}


	public long uploadCarAttachment(MultipartFile file, Map<String, Object> response, String username)throws Exception {
		logger.info(" Inside uploadCarAttachment ");
		Long count= 0L;

		try{
			Login login = loginRepo.findByUsername(username);
			String orgNameExtension = FilenameUtils.getExtension(file.getOriginalFilename());
			
			String Attachmentname=FilenameUtils.removeExtension(response.get("attachmentName").toString());   
			AuditCorrectiveAction car = auditCorrectiveActionRepository.findById(Long.parseLong(response.get("correctiveActionId").toString())).get();
			String refNo = response.get("carRefNo").toString().replace("/", "_");
			String oldFileName = car.getCarAttachment();
			if(oldFileName != null) {
				File fileR = Paths.get(storageDrive,"CAR",refNo,oldFileName).toFile();
				fileR.delete();
			}
			
			car.setCarAttachment(response.get("attachmentName").toString());
			car.setRootCause(response.get("rootCause").toString());
			if(!(car.getCarStatus() != null)) {
				car.setCarStatus("INI");
			}
			car.setCorrectiveActionTaken(response.get("correctiveActionTaken").toString());
			car.setCarCompletionDate(DLocalConvertion.converLocalTime(LocalDateTime.parse(response.get("completionDate").toString().replace("Z",""))));
			car.setModifiedBy(username);
			car.setModifiedDate(LocalDateTime.now());
			auditCorrectiveActionRepository.save(car);
			
	    	AuditTransaction trans = new AuditTransaction();
			trans.setEmpId(login.getEmpId());
			trans.setScheduleId(car.getCorrectiveActionId());
			trans.setTransactionDate(LocalDateTime.now());
			trans.setRemarks("NA");
			trans.setAuditStatus("INI");
			trans.setAuditType("C");
			
			auditTransactionRepository.save(trans);
			
			Path filePath = null;
	
				filePath = Paths.get( storageDrive,"CAR",refNo);
				
			logger.info(" Inside uploadCarAttachment " +filePath);
	        File theDir = filePath.toFile();
	        if (!theDir.exists()){
			     theDir.mkdirs();
			 }
	       Path fileToSave = filePath.resolve(Attachmentname + "." + orgNameExtension);
	        file.transferTo(fileToSave.toFile());

	        count=1L;

		   } 
		catch(Exception e){
			logger.error(" Inside uploadCarAttachment " +e);
		}
		
		return count;
	}
	
	@Override
	public long forwardCar(AuditCorrectiveActionDTO auditCorrectiveActionDTO, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method forwardCar()");
	    long result = 0;
	    try {
	    	Login login = loginRepo.findByUsername(username);
			String url= "/car-report";
			EmployeeDto employeeLogIn = masterClient.getEmployee(xApiKey,login.getEmpId()).get(0);
	    		if(auditCorrectiveActionDTO.getAuditStatus().equalsIgnoreCase("FWD")){
	    			
			    	auditCorrectiveActionRepository.updateCarStatus("CRM",username,LocalDateTime.now(),auditCorrectiveActionDTO.getCorrectiveActionId());
			    	
			    	AuditTransaction trans = new AuditTransaction();
					trans.setEmpId(login.getEmpId());
					trans.setScheduleId(auditCorrectiveActionDTO.getCorrectiveActionId());
					trans.setTransactionDate(LocalDateTime.now());
					trans.setRemarks(auditCorrectiveActionDTO.getMessage());
					trans.setAuditStatus("CRM");
					trans.setAuditType("C");
					
					auditTransactionRepository.save(trans);
					
					List<Login> loginList = loginRepository.findByIsActive(1);
					Set<Login> mrList = loginList.stream().filter(data -> data.getImsFormRoleId().equals(3L)).collect(Collectors.toSet());
					
					for(Login dto : mrList) {
					String NotiMsg = auditCorrectiveActionDTO.getCarRefNo()+" Of CAR Report Recommended by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					result = insertScheduleNomination(dto.getEmpId(),login.getEmpId(),username,url,NotiMsg);
					}
	    			
	    		}else if(auditCorrectiveActionDTO.getAuditStatus().equalsIgnoreCase("CRM")){
	    			
			    	auditCorrectiveActionRepository.updateCarStatus("CAP",username,LocalDateTime.now(),auditCorrectiveActionDTO.getCorrectiveActionId());
			    	
			    	AuditTransaction trans = new AuditTransaction();
					trans.setEmpId(login.getEmpId());
					trans.setScheduleId(auditCorrectiveActionDTO.getCorrectiveActionId());
					trans.setTransactionDate(LocalDateTime.now());
					trans.setRemarks(auditCorrectiveActionDTO.getMessage());
					trans.setAuditStatus("CAP");
					trans.setAuditType("C");
					
					auditTransactionRepository.save(trans);
					
					String NotiMsg = auditCorrectiveActionDTO.getCarRefNo()+" Of CAR Report Approved by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					result = insertScheduleNomination(auditCorrectiveActionDTO.getAuditeeEmpId(),login.getEmpId(),username,url,NotiMsg);
					
	    			
	    		}else{
			    	auditCorrectiveActionRepository.updateCarStatus("FWD",username,LocalDateTime.now(),auditCorrectiveActionDTO.getCorrectiveActionId());
			    	
			    	AuditTransaction trans = new AuditTransaction();
					trans.setEmpId(login.getEmpId());
					trans.setScheduleId(auditCorrectiveActionDTO.getCorrectiveActionId());
					trans.setTransactionDate(LocalDateTime.now());
					trans.setRemarks("NA");
					trans.setAuditStatus("FWD");
					trans.setAuditType("C");
					
					auditTransactionRepository.save(trans);
					
					String NotiMsg = auditCorrectiveActionDTO.getCarRefNo()+" Of CAR Report Forwarded by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					result = insertScheduleNomination(auditCorrectiveActionDTO.getHeadId(),login.getEmpId(),username,url,NotiMsg);
	    		}
				
				result = 1;
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method forwardCar() " + e);
	    }
	    return result;
	}


	@Override
	public List<AuditTranDto> carApproveEmpData(String carId) throws Exception {
		logger.info( " AuditServiceImpl Inside method carApproveEmpData()");
		try {
			List<Object[]> tranList = auditCorrectiveActionRepository.getApproveEmpDataList(carId);
			List<EmployeeDto> totalEmployee = masterClient.getEmployeeMasterList(xApiKey);

			
		    Map<Long, EmployeeDto> employeeMap = totalEmployee.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    
			 List<AuditTranDto> auditTranDtoList = Optional.ofNullable(tranList).orElse(Collections.emptyList()).stream()
				    .map(obj -> {
					    EmployeeDto employee =	obj[0] != null?employeeMap.get(Long.parseLong(obj[0].toString())):null;

					    	return AuditTranDto.builder()
				    			.empId(obj[0]!=null?Long.parseLong(obj[0].toString()):0L)
				    			.transactionDate(obj[1]!=null?obj[1].toString():"")
				    			.auditStatus(obj[2]!=null?obj[2].toString():"")
				    			.empName(employee != null?employee.getEmpName()+", "+employee.getEmpDesigName():"")
				    			.remarks(obj[3]!=null?obj[3].toString():"")
				    			.build();
				    })
				    .collect(Collectors.toList());
			return auditTranDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method carApproveEmpData()"+ e);
			 return Collections.emptyList();
		}
	}
	
	@Override
	public long returnCarReport(AuditCorrectiveActionDTO auditCorrectiveActionDTO, String username) throws Exception {
	    logger.info( " AuditServiceImpl Inside method returnCarReport()");
	    long result = 0;
	    try {
	    	    Login login = loginRepo.findByUsername(username);
			    EmployeeDto employeeLogIn = masterClient.getEmployee(xApiKey,login.getEmpId()).get(0);
			    AuditCorrectiveAction car = auditCorrectiveActionRepository.findById(auditCorrectiveActionDTO.getCorrectiveActionId()).get();
		    	AuditTransaction trans = new AuditTransaction();
		    	if(car.getCarStatus().equalsIgnoreCase("FWD")){
		    		car.setCarStatus("CRH");
					trans.setAuditStatus("CRH");
		    	}else {
		    		car.setCarStatus("CMR");
					trans.setAuditStatus("CMR");
		    	}
		    	car.setModifiedBy(username);
		    	car.setModifiedDate(LocalDateTime.now());
		    	result = auditCorrectiveActionRepository.save(car).getCorrectiveActionId();
		    	
				trans.setEmpId(login.getEmpId());
				trans.setScheduleId(result);
				trans.setTransactionDate(LocalDateTime.now());
				trans.setRemarks(auditCorrectiveActionDTO.getMessage());
				trans.setAuditType("C");
				
				auditTransactionRepository.save(trans);
				String url= "/schedule-list";
	
					String NotiMsg = auditCorrectiveActionDTO.getCarRefNo()+" Of CAR Report Returned by "+ employeeLogIn.getEmpName()+", "+employeeLogIn.getEmpDesigName();
					result = insertScheduleNomination(auditCorrectiveActionDTO.getAuditeeEmpId(),login.getEmpId(),username,url,NotiMsg);
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	        logger.error("AuditServiceImpl Inside method returnCarReport() " + e);
	    }
	    return result;
	}

	
	public long saveDocFilesUpload(List<MultipartFile> files, AuditCheckListDTO auditCheckListDTO, String timestamp)throws Exception {
		long result = 1;
		logger.info( " AuditServiceImpl Inside method saveDocFilesUpload()");
		try {
			for(int i=0; i<files.size(); i++) {
				
				String orgNameExtension = FilenameUtils.getExtension(files.get(i).getOriginalFilename());
				String attachmentname = files.get(i).getOriginalFilename();
				if(!(attachmentname.equalsIgnoreCase("blob"))) {
					String iqaNo= auditCheckListDTO.getIqaNo().replace("/", "_")+" - "+auditCheckListDTO.getScheduleId();
					
					Path filePath = null;
					filePath = Paths.get(storageDrive,"CheckListUploads",iqaNo);
					
					logger.info(" Inside saveDocFilesUpload " +filePath);
			        File theDir = filePath.toFile();
			        if (!theDir.exists()){
					     theDir.mkdirs();
					 }
			        String[] fileName = attachmentname.split("\\.");
			        Path fileToSave = filePath.resolve(fileName[0]+timestamp + "." + orgNameExtension);
			        files.get(i).transferTo(fileToSave.toFile());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method saveDocFilesUpload()"+ e);
		}
		return result;
	}
	
	@Override
	public List<IqaScheduleDto> getIqaScheduleList() throws Exception {
		logger.info(" AuditServiceImpl Inside method getIqaScheduleList()");
		try {
			List<Object[]> result = iqaRepository.getIqaScdList();
			
			result = result.stream().filter(data -> !data[4].toString().equalsIgnoreCase("0")).collect(Collectors.toList());			
			
			
			List<IqaScheduleDto> finalIqaDtoList = Optional.ofNullable(result).orElse(Collections.emptyList()).stream().map(rowData ->
			IqaScheduleDto.builder()
			.iqaId(rowData[0]!=null?Long.parseLong(rowData[0].toString()):0L)
			.iqaNo(rowData[1]!=null?rowData[1].toString():"")
			.fromDate(rowData[2]!=null?rowData[2].toString():"")
			.toDate(rowData[3]!=null?rowData[3].toString():"")
			.auditees(rowData[4]!=null?Long.parseLong(rowData[4].toString()):0L)
			.auditeeSub(rowData[5]!=null?Long.parseLong(rowData[5].toString()):0L)
			.auditorSub(rowData[6]!=null?Long.parseLong(rowData[6].toString()):0L)
			.auditeeAcp(rowData[7]!=null?Long.parseLong(rowData[7].toString()):0L)
			.build()
			).collect(Collectors.toList());
			return finalIqaDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getIqaScheduleList()"+ e);
			 return Collections.emptyList();
		}
	}


	@Override
	public long addAuditClosure(AuditClosureDTO auditClosureDTO, String username) throws Exception {
		long result = 0;
		logger.info( " AuditServiceImpl Inside method addAuditClosure()");
		try {
			String content = auditClosureDTO.getContent().replace("\\", "");
			if(content.startsWith("\"") && content.endsWith("\"")) {
				content = content.substring(1,content.length()-1);			
			}
			Login login = loginRepo.findByUsername(username);
			AuditClosure closure = new AuditClosure();
			
			closure.setClosureDate(DLocalConvertion.converLocalTime(auditClosureDTO.getCompletionDate()));
			closure.setIqaId(auditClosureDTO.getIqaId());
			closure.setRemarks(content);
			closure.setActEmpId(login.getEmpId());
			closure.setAttachmentName(auditClosureDTO.getAttchmentName());
			closure.setCreatedBy(username);
			closure.setCreatedDate(LocalDateTime.now());
			closure.setIsActive(1);
			
			result = auditClosureRepository.save(closure).getClosureId();
			} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method addAuditClosure()"+ e);
			
		}
		return result;
	}


	@Override
	public List<AuditClosure> getAuditClosureList() throws Exception {
		logger.info( " AuditServiceImpl Inside method getAuditClosureList()");
		try {
			return auditClosureRepository.findByIsActive(1);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getAuditClosureList()"+ e);
			 return Collections.emptyList();
		}
	}
	
	@Override
	public long updateAuditClosure(AuditClosureDTO auditClosureDTO, String username) throws Exception {
		long result = 0;
		logger.info( " AuditServiceImpl Inside method updateAuditClosure()");
		try {
			String content = auditClosureDTO.getContent().replace("\\", "");
			if(content.startsWith("\"") && content.endsWith("\"")) {
				content = content.substring(1,content.length()-1);			
			}
			AuditClosure closure =  null;
			Optional<AuditClosure> closureOptional = auditClosureRepository.findById(auditClosureDTO.getClosureId());
			if(closureOptional.isPresent()) {
				closure = closureOptional.get();
				closure.setClosureDate(DLocalConvertion.converLocalTime(auditClosureDTO.getCompletionDate()));
				closure.setRemarks(content);
				closure.setAttachmentName(auditClosureDTO.getAttchmentName());
				closure.setModifiedBy(username);
				closure.setModifiedDate(LocalDateTime.now());
				
				result = auditClosureRepository.save(closure).getClosureId();
			}
			
			} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method updateAuditClosure()"+ e);
			
		}
		return result;
	}
	
	@Override
	public long uploadAuditClosureFile(MultipartFile file, Map<String, Object> response)
			throws Exception {
		long result = 0;
		logger.info(" AuditServiceImpl Inside method uploadAuditClosureFile()");
		try {
			String orgNameExtension = FilenameUtils.getExtension(file.getOriginalFilename());
			String Attachmentname = FilenameUtils.removeExtension(response.get("attchmentName").toString());
			String iqaNo= response.get("iqaNo").toString().replace("/", "_");
			
			if(!response.get("closureId").toString().equalsIgnoreCase("0")) {
				 if(!response.get("oldAttchmentName").toString().equalsIgnoreCase("")) {
					 File fileR = Paths.get(storageDrive,"AuditClosure",iqaNo,response.get("oldAttchmentName").toString()).toFile();
					 if(fileR.delete()) {
						 result = 1;
					 }
				 }
			 }
				
				Path filePath = null;
				filePath = Paths.get(storageDrive,"AuditClosure",iqaNo);
				
				logger.info(" Inside uploadAuditClosureFile " +filePath);
		        File theDir = filePath.toFile();
		        if (!theDir.exists()){
				     theDir.mkdirs();
				 }
		        Path fileToSave = filePath.resolve(Attachmentname + "." + orgNameExtension);
		        file.transferTo(fileToSave.toFile());
		        result = 1;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method uploadAuditClosureFile()"+ e);
		}
		return result;
	}
	
	@Override
	public List<AuditClosureDateDTO> getClosureDate() throws Exception {
		logger.info( " AuditServiceImpl Inside method getClosureDate()");
		try {
			List<Object[]> result = auditClosureRepository.getClosureDate();
			
			List<AuditClosureDateDTO> finalIqaDtoList = Optional.ofNullable(result).orElse(Collections.emptyList()).stream().map(rowData ->
			AuditClosureDateDTO.builder()
			.iqaId(rowData[0]!=null?Long.parseLong(rowData[0].toString()):0L)
			.completionDate(rowData[1]!=null?rowData[1].toString():"")
			.fromDate(rowData[2]!=null?rowData[2].toString():"")
			.build()
			).collect(Collectors.toList());
			return finalIqaDtoList;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method getClosureDate()"+ e);
			 return Collections.emptyList();
		}
	}

}
