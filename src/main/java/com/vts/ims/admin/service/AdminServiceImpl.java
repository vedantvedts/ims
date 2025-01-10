package com.vts.ims.admin.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vts.ims.admin.dto.ApprovalAuthorityDto;
import com.vts.ims.admin.dto.AuditStampingDto;
import com.vts.ims.admin.dto.FormDetailDto;
import com.vts.ims.admin.dto.FormModuleDto;
import com.vts.ims.admin.dto.FormRoleDto;
import com.vts.ims.admin.dto.FormroleAccessDto;
import com.vts.ims.admin.dto.NotificationDto;
import com.vts.ims.admin.dto.UserManageAddEditDto;
import com.vts.ims.admin.dto.UserManagerListDto;
import com.vts.ims.admin.entity.ApprovalAuthority;
import com.vts.ims.admin.entity.AuditPatch;
import com.vts.ims.admin.entity.FormDetail;
import com.vts.ims.admin.entity.FormModule;
import com.vts.ims.admin.entity.FormRoleAccess;
import com.vts.ims.admin.repository.ApprovalAuthorityRepository;
import com.vts.ims.admin.repository.AuditStampingRepo;
import com.vts.ims.admin.repository.FormDetailRepo;
import com.vts.ims.admin.repository.FormModuleRepo;
import com.vts.ims.admin.repository.FormRoleAccessRepo;
import com.vts.ims.admin.repository.ImsFormRoleRepo;
import com.vts.ims.admin.repository.UserManagerRepo;
import com.vts.ims.audit.dto.AuditPatchDto;
import com.vts.ims.audit.repository.AuditPatchRepository;
import com.vts.ims.login.Login;
import com.vts.ims.login.LoginRepository;
import com.vts.ims.master.dao.MasterClient;
import com.vts.ims.master.dto.EmployeeDto;
import com.vts.ims.master.dto.LoginDetailsDto;
import com.vts.ims.master.service.MasterService;
import com.vts.ims.model.ImsNotification;
import com.vts.ims.model.LoginStamping;
import com.vts.ims.repository.NotificationRepository;



@Service
public class AdminServiceImpl implements AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
	
	@Autowired
	private FormModuleRepo formModuleRepo;
	
	@Autowired
	private FormDetailRepo formDetailRepo;
	
	@Autowired
	private MasterClient masterClient;
	
	@Autowired
	MasterService  masterservice;

    @Autowired
    private AuditStampingRepo auditStampingRepo;
    
	@Autowired
	UserManagerRepo userManagerRepo;

	@Autowired
	ImsFormRoleRepo imsFormRoleRepo;

    @Autowired
	FormRoleAccessRepo formRoleAccessRepo;
    
    @Autowired
    ApprovalAuthorityRepository approvalAuthorityRepo;
    
	
	@Autowired
	LoginRepository loginRepo;
	
	@Autowired
	NotificationRepository notificationRepo;
	
	
	@Autowired
	AuditPatchRepository auditPatchRepo;
	
	
	@Value("${x_api_key}")
	private String xApiKey;
	
	@Override
	public List<FormModuleDto> formModuleList(Long imsFormRoleId) throws Exception {
		logger.info(new Date() +" Inside formModuleList " );
		try {
			List<FormModuleDto> formModuleDtoList = new ArrayList<FormModuleDto>();
			List<FormModule> formModuleList = formModuleRepo.findDistinctFormModulesByRoleId(imsFormRoleId);
			
			formModuleList.forEach(detail -> {
				FormModuleDto formModuleDto = FormModuleDto.builder()
						.FormModuleId(detail.getFormModuleId())
						.FormModuleName(detail.getFormModuleName())
						.ModuleUrl(detail.getModuleUrl())
						.ModuleIcon(detail.getModuleIcon())
						.SerialNo(detail.getSerialNo())
						.IsActive(detail.getIsActive())
						.build();
				
				formModuleDtoList.add(formModuleDto);
			});
			
			return formModuleDtoList;
		} catch (Exception e) {
			logger.error(new Date() +" Inside formModuleList ", e );
			e.printStackTrace();
			return new ArrayList<FormModuleDto>();
		}
	}
	
	
	@Override
	public List<FormDetailDto> formModuleDetailList(Long imsFormRoleId) throws Exception {
		logger.info(new Date() +" Inside formModuleDetailList " );
		try {
			List<FormDetailDto> formDetailDtoList = new ArrayList<FormDetailDto>();
			List<FormDetail> formDetailList = formDetailRepo.findDistinctFormModulesDetailsByRoleId(imsFormRoleId);
			
			formDetailList.forEach(detail -> {
				FormDetailDto formModuleDto = FormDetailDto.builder()
						.FormModuleId(detail.getFormModuleId())
						.FormName(detail.getFormName())
						.FormUrl(detail.getFormUrl())
						.FormDispName(detail.getFormDispName())
						.FormSerialNo(detail.getFormSerialNo())
						.FormColor(detail.getFormColor())
						.ModifiedBy(detail.getModifiedBy())
						.ModifiedDate(detail.getModifiedDate())
						.IsActive(detail.getIsActive())
						.build();
				
				formDetailDtoList.add(formModuleDto);
			});
			
			return formDetailDtoList;
		} catch (Exception e) {
			logger.error(new Date() +" Inside formModuleDetailList ", e );
			e.printStackTrace();
			return new ArrayList<FormDetailDto>();
		}
	}
	
	
	//mdn connection established in below method
		@Override
		public List<LoginDetailsDto> loginDetailsList(String username) {
			 logger.info(new Date() + " AdminServiceImpl Inside method loginDetailsList");
			 List<LoginDetailsDto> list =null;
			 try {
		
				 list = masterservice.loginDetailsList(username);
				 
			 } catch (Exception e) {
		    	 logger.info(new Date() + " AdminServiceImpl Inside method loginDetailsList"+ e.getMessage());
		        return new ArrayList<>(); // Return an empty list in case of an error
		    }
			 return list;
		}
		        
		        
		
		@Override
		public List<EmployeeDto> employeeList() throws Exception {
			logger.info(new Date() + " AuditServiceImpl Inside method employeeList()");
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
		public long loginStampingInsert(LoginStamping Stamping)throws Exception{
			logger.info(new Date() + " AdminServiceImpl Inside method LoginStampingInsert " );
			long result = 0;
			try{
				result =  auditStampingRepo.save(Stamping).getAuditStampingId();
			}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" error in AdminServiceImpl Inside method LoginStampingInsert "+ e.getMessage());
			}
			return result;
		}
		
		@Override
		public long  lastLoginStampingId(long loginId)throws Exception{
			logger.info(new Date() + " AdminServiceImpl Inside method LastLoginStampingId " );
			        try {
			            Optional<Long> result = auditStampingRepo.findLastLoginStampingId(loginId);
			            return result.isPresent() ? result.get() : 0L; 
			        } catch (Exception e) {
			        	logger.error(new Date() +" error in AdminServiceImpl Inside method LastLoginStampingId "+ e.getMessage());
			            throw new Exception("Error while fetching last login stamping ID", e);
			        }
			    
		}

		
		@Override
		public long loginStampingUpdate(LoginStamping stamping)throws Exception{

			logger.info(new Date() + " AdminServiceImpl Inside method LoginStampingUpdate " );
			long result = 0;
			try{
				LoginStamping prevStampingDetails = auditStampingRepo.findByAuditStampingId(stamping.getAuditStampingId());
				prevStampingDetails.setAuditStampingId(stamping.getAuditStampingId());
				prevStampingDetails.setLogOutType(stamping.getLogOutType());
				prevStampingDetails.setLogOutDateTime(stamping.getLogOutDateTime());
				
				result =  auditStampingRepo.save(prevStampingDetails).getAuditStampingId();
			}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" error in AdminServiceImpl Inside method LoginStampingUpdate "+ e.getMessage());
			}
			return result;
		}


	@Override
		public List<AuditStampingDto> getAuditStampinglist(AuditStampingDto stamping)throws Exception{
			
			 logger.info(new Date() + " AdminServiceImpl Inside method getAuditStampinglist " );
				List<Object[]> AuditStampinglist = null;
			 try {
			
		
			long loginId = 0;
			Long loginIdForEmpId  = userManagerRepo.findLoginIdByEmpId(Long.parseLong(stamping.getEmpId())) ;
			if(loginIdForEmpId != null) {
				loginId = loginIdForEmpId;
			}
			
			
			stamping.setLoginId(loginId);
			if(stamping.getLoginId()>=0) {
				   AuditStampinglist =  auditStampingRepo.getAuditStampingList(stamping.getLoginId(),stamping.getFromDate(),stamping.getToDate());
			   if(AuditStampinglist!=null && AuditStampinglist.size()>0) {
				   
				   return AuditStampinglist.stream()
			        		.map((resultData)  -> AuditStampingDto.builder()
			        			 .auditStampingId(resultData[0] != null ? Long.parseLong(resultData[0].toString()) : 0L)
			        			 .loginId(resultData[1] != null ? Long.parseLong(resultData[1].toString()) : 0L)
			        			 .username(resultData[2]!=null?resultData[2].toString():"")
			        		     .ipAddress(resultData[3]!=null?resultData[3].toString():"")
			                     .macAddress(resultData[4]!=null?resultData[4].toString():"")
			        			 .loginDate(resultData[5]!=null?resultData[5].toString():"")
			                     .loginDateTime(resultData[6]!=null?resultData[6].toString():"")
		                         .logOutDateTime(resultData[7]!=null?resultData[7].toString():"")
		                         .logOutType(resultData[8]!=null?resultData[8].toString():"")
		           
		                         .loginTime(resultData[9]!=null?resultData[9].toString():"")
		                         .logoutTypeDisp(resultData[10]!=null?resultData[10].toString():"")
		                         
		                     .build())
			                .collect(Collectors.toList());
				   
			   }else {
					return List.of();
			   }
			   
			}
			return List.of();
			
			 } catch (Exception e) {
		        	logger.error(new Date() +" error in AdminServiceImpl Inside method getAuditStampinglist "+ e.getMessage());
				         e.printStackTrace();
				     	return List.of();
				}
			
		
		}


	@Override
	public List<UserManagerListDto> UserManagerList(String username) throws Exception {
		logger.info(new Date() + " AdminServiceImpl Inside method UserManagerList " );

		try {
			//List<LoginDetailsDto> loginDtoList = masterservice.loginDetailsList(username);
			// Fetch login details and labCode
			String labCode = masterservice.loginDetailsList(username).stream()
					.filter(dto -> dto.getUsername().equals(username))
					.map(LoginDetailsDto::getLabCode)
					.findFirst()
					.orElse(null);


			List<EmployeeDto> allActiveEmployees = masterClient.getEmployeeMasterList(xApiKey);
			List<Object[]> userManagerList = userManagerRepo.getUserManagerMasterList();
			Map<Long, EmployeeDto> employeeMap = allActiveEmployees.stream()
					.collect(Collectors.toMap(EmployeeDto::getEmpId, emp -> emp));


			if (userManagerList != null && !userManagerList.isEmpty()) {
				// Filter and map user manager list
				return userManagerList.stream()
						.map(resultData -> {
							Long empId = resultData[3] != null ? Long.parseLong(resultData[3].toString()) : 0L;
							EmployeeDto employeeDto = employeeMap.get(empId);
							String empName = employeeDto != null ? employeeDto.getEmpName() : "";
							String empDesigCode = employeeDto != null ? employeeDto.getEmpDesigName() : "";
							// Print empDesigCode for debugging purposes

							String empLabCode = employeeDto != null ? employeeDto.getLabCode() : "";
							String empDivCode = employeeDto != null ? employeeDto.getEmpDivCode() : "";



							// Build UserManagerListDto
							UserManagerListDto userManager = UserManagerListDto.builder()
									.loginId(resultData[0] != null ? Long.parseLong(resultData[0].toString()) : 0L)
									.username(resultData[1] != null ? resultData[1].toString() : "")
									.password(resultData[2] != null ? resultData[2].toString() : "")
									.empId(empId)
									.divisionId(resultData[4] != null ? Long.parseLong(resultData[4].toString()) : 0L)
									.imsFormRoleId(resultData[6] != null ? Long.parseLong(resultData[6].toString()) : 0L)
									.loginType(resultData[7] != null ? resultData[7].toString() : "")
									.isActive(resultData[8] != null ? Integer.parseInt(resultData[8].toString()) : 0)
									.formRoleName(resultData[9] != null ? resultData[9].toString() : "")
									.empName(empName)
									.empDesig(empDesigCode)
									.empDivCode(empDivCode)
									.empLabCode(empLabCode)
									.build();

							return userManager;
						})
						.filter(userManager -> labCode == null || userManager.getEmpLabCode().equals(labCode)) // Filter based on labCode
						.collect(Collectors.toList());
			} else {
				return List.of();
			}

		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method UserManagerList "+ e.getMessage());
			e.printStackTrace();
			return List.of();
		}

	}

	@Override
	public List<FormRoleDto> roleList() {
		logger.info(new Date() + " AdminServiceImpl Inside method roleList " );
		List<Object[]> formRolesList = imsFormRoleRepo.getRoleDetails();

		try {
			if (formRolesList != null && !formRolesList.isEmpty()) {
				// Map raw data to FormRoleDto
				return formRolesList.stream()
						.map((resultData) -> FormRoleDto.builder()
								.roleId(resultData[0] != null ? Long.parseLong(resultData[0].toString()) : 0L)
								.roleName(resultData[1] != null ? resultData[1].toString() : "")
								.build())
						.collect(Collectors.toList());
			} else {
				return List.of();
			}

		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method roleList "+ e.getMessage());
			e.printStackTrace();
			return List.of();
		}

	}

	@Override
	public List<FormModuleDto> getformModulelist() throws Exception {
		logger.info(new Date() + " AdminServiceImpl Inside method getformModulelist " );
		List<FormModuleDto> FMlist = new ArrayList<FormModuleDto>();
		try {

			List<Object[]>  list =formModuleRepo.getformModulelist();
			if(list!=null) {
				for(Object[] O:list) {
					FormModuleDto dto = new FormModuleDto();
					dto.setFormModuleId(Long.parseLong(O[0].toString()));
					dto.setFormModuleName(O[1].toString());
					FMlist.add(dto);
				}
			}else {
				FMlist = null;
			}
		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method getformModulelist "+ e.getMessage());
			e.printStackTrace();
		}

		return FMlist;
	}

	@Override
	public List<FormroleAccessDto> getformRoleAccessList(String roleId, String formModuleId) {
		logger.info(new Date() + " AdminServiceImpl Inside method getformModulelist");
		List<FormroleAccessDto> FRAlist = new ArrayList<FormroleAccessDto>();
		try {

			List<Object[]> list = formRoleAccessRepo.getformroleAccessList( roleId, formModuleId);
			for(Object[] O:list) {
				FormroleAccessDto dto = new FormroleAccessDto();
				if(O[0]!=null) {
					dto.setFormRoleAccessId(Long.parseLong(O[0].toString()));
				}else {
					dto.setFormRoleAccessId(0L);
				}
				dto.setFormDispName(O[3].toString());
				if(O[1]!=null) {
					dto.setFormDetailId(Long.parseLong(O[1].toString()));
				}else {
					dto.setFormDetailId(0L);
				}
				if(O[2]!=null) {
					dto.setModuleId(Long.parseLong(O[2].toString()));
				}else {
					dto.setModuleId(0L);
				}
				String value =O[4]+"";
				if (value.equals("1")) {
					dto.setIsActive(true);
				} else {
					dto.setIsActive(false);
				}

				FRAlist.add(dto);
			}
		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method getformRoleAccessList "+ e.getMessage());
			e.printStackTrace();
		}
		return FRAlist;
	}

	@Override
	public String updateformroleaccess(FormroleAccessDto accessDto, String username) {
		logger.info(new Date() + " AdminServiceImpl Inside method updateformroleaccess");
		String updateResult = null;
		try {
			long result = formRoleAccessRepo.countByFormRoleIdAndDetailId(String.valueOf(accessDto.getRoleId()),String.valueOf(accessDto.getFormDetailId()));
			if(result == 0) {
				FormRoleAccess formrole = new FormRoleAccess();
				formrole.setImsFormRoleId(accessDto.getRoleId());
				formrole.setFormDetailId(accessDto.getFormDetailId());
				formrole.setIsActive(1);
				formrole.setCreatedBy(username);
				formrole.setCreatedDate(LocalDateTime.now());
				formRoleAccessRepo.save(formrole);
				updateResult = String.valueOf(formrole.getFormRoleAccessId());
			}else {
				Optional<FormRoleAccess> formRoleAccess = formRoleAccessRepo.findById(accessDto.getFormRoleAccessId());
				if(formRoleAccess.isPresent()){
					FormRoleAccess roleAccess = formRoleAccess.get();
                    roleAccess.setIsActive(String.valueOf(accessDto.isActive()).equalsIgnoreCase("true") ? 1 : 0);
					roleAccess.setModifiedBy(username);
					roleAccess.setModifiedDate(LocalDateTime.now());
					formRoleAccessRepo.save(roleAccess);
					updateResult = String.valueOf(roleAccess.getFormRoleAccessId());
				}
			}
		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method updateformroleaccess "+ e.getMessage());
			e.printStackTrace();
		}
		return updateResult;
	}

	@Override
	public UserManageAddEditDto UserManagerEditData(String loginId) throws Exception {
		logger.info(new Date() + " AdminServiceImpl Inside method UserManagerEditData " );
		UserManageAddEditDto userEditData =null;
		try {
			userEditData =new UserManageAddEditDto();
			Login login=userManagerRepo.findByLoginId(Long.parseLong(loginId));
			userEditData.setRoleId(login.getImsFormRoleId());
			userEditData.setDivisionId(login.getDivisionId());
			userEditData.setEmpId(login.getEmpId());
			userEditData.setLogintypeId(login.getLoginType());
			userEditData.setLoginId(login.getLoginId());
			userEditData.setUserName(login.getUsername());
		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method AdminServiceImpl "+ e.getMessage());
			e.printStackTrace();
		}
		return userEditData;
	}

	@Override
	public int UserManagerUpdate(UserManageAddEditDto userManageAdd, String name) {
		logger.info(new Date() + " AdminServiceImpl Inside method UserManagerUpdate " );
		long result = 0;
		try {
			Login login=userManagerRepo.findByLoginId(userManageAdd.getLoginId());
			login.setImsFormRoleId(userManageAdd.getRoleId());
			login.setLoginId(userManageAdd.getLoginId());
			login.setLoginType("NA");
			if(userManageAdd.getEmpId()!=null) {
				login.setEmpId(userManageAdd.getEmpId());
				List<EmployeeDto> empData = masterClient.getEmployee(xApiKey,userManageAdd.getEmpId());
				if (!empData.isEmpty()) {
					EmployeeDto eDto = empData.get(0); // Get the first element
					login.setDivisionId(eDto.getDivisionId()); // Now you can access the divisionId
				}else {
					login.setDivisionId(Long.parseLong("0"));
				}
			}else {
				login.setEmpId(Long.parseLong("0"));
				login.setDivisionId(Long.parseLong("0"));
			}
			login.setModifiedBy(name);
			login.setModifiedDate(LocalDateTime.now());
			userManagerRepo.save(login);
			result = login.getLoginId();
		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method UserManagerUpdate "+ e.getMessage());
			e.printStackTrace();
		}
		return (int) result;
	}

	@Override
	public long UserNamePresentCount(String userName) throws Exception {
		logger.info(new Date() + " AdminServiceImpl Inside method UserNamePresentCount " );
		try {
			Long count = userManagerRepo.countByUserNameAndActive(userName);
			return (count != null) ? count : 0L; // Return 0 if count is null
		} catch (Exception e) {
			logger.error(new Date() +" error in AdminServiceImpl Inside method UserNamePresentCount "+ e.getMessage());
			throw new Exception("Error while counting usernames", e);
		}
	}

	
	@Override
	public List<ApprovalAuthorityDto> approvalAuthorityList() throws Exception {
		logger.info(new Date() + " AdminServiceImpl Inside method approvalAuthorityList " );
		try {
			List<ApprovalAuthority> approvalAuthority = approvalAuthorityRepo.findAll();
			List<EmployeeDto> employeeList=masterClient.getEmployeeList(xApiKey);
		    Map<Long, EmployeeDto> employeeMap = employeeList.stream()
		            .filter(employee -> employee.getEmpId() != null)
		            .collect(Collectors.toMap(EmployeeDto::getEmpId, employee -> employee));
		    List<ApprovalAuthorityDto> finalDto = approvalAuthority.stream()
				    .map(obj -> {
				    	ApprovalAuthorityDto approvalAuthorityDto = new ApprovalAuthorityDto();
				       // EmployeeDto employeeDto = masterClient.getEmployee(xApiKey, obj.getEmpId()).get(0);
				        EmployeeDto employeeDto =  employeeMap.get(obj.getEmpId());
				        approvalAuthorityDto.setEmpId(obj.getEmpId());
				        approvalAuthorityDto.setEmpName(employeeDto.getEmpName()+", "+employeeDto.getEmpDesigName());
				        approvalAuthorityDto.setMrType(obj.getMRType());
				        approvalAuthorityDto.setMrFrom(obj.getMRFrom());
				        approvalAuthorityDto.setMrTo(obj.getMRTo());
				        approvalAuthorityDto.setMRsId(obj.getMRsId());
				        approvalAuthorityDto.setIsActive(obj.getIsActive());
				        approvalAuthorityDto.setSalutation(employeeDto.getSalutation());
				        approvalAuthorityDto.setMRsId(obj.getMRsId());
				        return approvalAuthorityDto;
				    })
				    .sorted(Comparator.comparingLong(ApprovalAuthorityDto::getMRsId).reversed()) 
				    .collect(Collectors.toList());
			return finalDto;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" error in AdminServiceImpl Inside method approvalAuthorityList "+ e.getMessage());
			return List.of();
		}
		// TODO Auto-generated method stub
	}

	
	@Override
	public long insertApprovalAuthority(ApprovalAuthorityDto approvalAuthorityDto, String username) throws Exception {
		logger.info(new Date() + " AdminServiceImpl Inside method insertApprovalAuthority " );
		long result=0;
		try {
			if(approvalAuthorityDto!=null) {
				if(approvalAuthorityDto.getMrRepEmpId()!=null && approvalAuthorityDto.getMrRepEmpId().length>0) {
					for(int i=0;i<approvalAuthorityDto.getMrRepEmpId().length;i++) {
						ApprovalAuthority model=new ApprovalAuthority();
						model.setEmpId(Long.parseLong(approvalAuthorityDto.getMrRepEmpId()[i].toString()));
						model.setMRType(approvalAuthorityDto.getMrType());
						model.setMRFrom(approvalAuthorityDto.getMrFrom());
						model.setMRTo(approvalAuthorityDto.getMrTo());
						model.setCreatedBy(username);
						model.setCreatedDate(LocalDateTime.now());
						model.setIsActive(1);
						result=approvalAuthorityRepo.save(model).getMRsId();
					}
				}else {
					ApprovalAuthority model=new ApprovalAuthority();
					model.setEmpId(approvalAuthorityDto.getMrempId());
					model.setMRType(approvalAuthorityDto.getMrType());
					model.setMRFrom(approvalAuthorityDto.getMrFrom());
					model.setMRTo(approvalAuthorityDto.getMrTo());
					model.setCreatedBy(username);
					model.setCreatedDate(LocalDateTime.now());
					model.setIsActive(1);
					result=approvalAuthorityRepo.save(model).getMRsId();
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" error in AdminServiceImpl Inside method insertApprovalAuthority "+ e.getMessage());
			return result;
		}
		
	}
	
	
	@Override
	public long approvalAuthorityInactive(ApprovalAuthorityDto approvalAuthorityDto, String username) throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method approvalAuthorityInactive()");
		long result=0;
		try {
			Optional<ApprovalAuthority> model =approvalAuthorityRepo.findById(approvalAuthorityDto.getMRsId());
			if(model.isPresent()) {
				ApprovalAuthority data = model.get();
				data.setIsActive(approvalAuthorityDto.getIsActive());
				data.setModifiedBy(username);
				data.setModifiedDate(LocalDateTime.now());
				result=approvalAuthorityRepo.save(data).getMRsId();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method approvalAuthorityInactive()"+ e);
			return 0;
		}
	}
	
	@Override
	public Long UpdateApprovalAuthority(ApprovalAuthorityDto approvalAuthorityDto, String username) throws Exception {
		logger.info(new Date() + " AuditServiceImpl Inside method UpdateApprovalAuthority()");
		long result=0;
		try {
			Optional<ApprovalAuthority> model =approvalAuthorityRepo.findById(approvalAuthorityDto.getMRsId());
			if(model.isPresent()) {
				ApprovalAuthority data = model.get();
				data.setMRFrom(approvalAuthorityDto.getMrFrom());
				data.setMRTo(approvalAuthorityDto.getMrTo());
				data.setModifiedBy(username);
				data.setModifiedDate(LocalDateTime.now());
				result=approvalAuthorityRepo.save(data).getMRsId();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method UpdateApprovalAuthority()"+ e);
			return result;
		}
	}
	
	@Override
	public List<NotificationDto> getNotifictionList(String username) throws Exception {
		List<LoginDetailsDto> loginDetails = masterservice.loginDetailsList(username);
	    LoginDetailsDto loginDetail =loginDetails.get(0); 
	    
	    
	    List<Object[]> notificationList = notificationRepo.getNotifictionList(loginDetail.getEmpId());
	    return notificationList.stream()
	            .map(list -> {
	         
	                return NotificationDto.builder()
	                        .notificationId(list[0] != null ? Long.parseLong(list[0].toString()) : 0)
	                        .empName(loginDetail.getEmpName())
	                        .empDesig(loginDetail.getEmpDesigCode())
	                        .notificationMessage(list[5] != null ? list[5].toString() : "")
	                        .notificationUrl(list[3] != null ? list[3].toString() : "")
	                        .build();
	            })
	            .collect(Collectors.toList());
	}
	
	@Override
	public Integer getNotifictionCount(String username) throws Exception {
		Login login=loginRepo.findByUsername(username);
		int count = 0;
		try {
			count  = notificationRepo.getNotifictionCount(login.getEmpId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AuditServiceImpl Inside method notifictionCount()"+ e);
		}
		return count;
	}
	
	@Override
	public long updateNotification(String userName, String notificationId) throws Exception {
	    logger.info(new Date() + " AdminServiceImpl Inside method updateNotification ");
	    try {
	        // Find the notification by ID
	        Optional<ImsNotification> notifOptional = notificationRepo.findById(Long.parseLong(notificationId));

	        if (notifOptional.isPresent()) {
	            // Get the notification object from the Optional
	            ImsNotification notification = notifOptional.get();

	            // Update the necessary fields
	            notification.setModifiedBy(userName);
	            notification.setModifiedDate(LocalDateTime.now());
	            notification.setIsActive(0); 

	            // Save the updated entity back to the repository
	            ImsNotification updatedNotification = notificationRepo.save(notification);

	            // Return the ID of the updated notification
	            return updatedNotification.getNotificationId();
	        } else {
	            logger.error("Notification with ID " + notificationId + " not found.");
	            throw new Exception("Notification not found");
	        }
	    } catch (Exception e) {
	        logger.error("Error in updateNotification: " + e.getMessage(), e);
	        throw e;
	    }
	}

	
	@Override
	public List<AuditPatchDto> getAuditPatchList() throws Exception {
		logger.info(new Date() + " AdminServiceImpl Inside method getAuditPatchList ");
		try {
			List<AuditPatchDto> auditPatchListDto = new ArrayList<AuditPatchDto>();
			List<AuditPatch> auditpatch = auditPatchRepo.findAll();
			
			auditpatch.forEach(data -> {
				AuditPatchDto formModuleDto = AuditPatchDto.builder()
						.auditPatchesId(data.getAuditPatchesId())
						.versionNo(data.getVersionNo())
						.patchDate(data.getPatchDate())
						.description(data.getDescription())
						.createdDate(data.getCreatedDate())
						.attachment(data.getAttachment())
						.modifiedBy(data.getModifiedBy())
						.modifiedDate(data.getModifiedDate())
						.build();
				
				auditPatchListDto.add(formModuleDto);
			});
			
			return auditPatchListDto;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() +" error in AdminServiceImpl Inside method getAuditPatchList "+ e.getMessage());
			return List.of();
			
		}
	}
	
	@Override
	public Long updateAuditPatch(AuditPatchDto auditPatchDto,String username) throws Exception {
		 logger.info(new Date() + " AdminServiceImpl Inside method updateAuditPatch ");
		try {
			 Optional<AuditPatch> auditOptional = auditPatchRepo.findById(auditPatchDto.getAuditPatchesId());
			 if (auditOptional.isPresent()) {
		            AuditPatch auditPatch = auditOptional.get();
		            auditPatch.setDescription(auditPatchDto.getDescription());
		            auditPatch.setModifiedBy(username);
		            auditPatch.setModifiedDate(LocalDateTime.now());
		            AuditPatch updateAuditPatch = auditPatchRepo.save(auditPatch);
		            return updateAuditPatch.getAuditPatchesId();
		        } else {
		            logger.error("Audit Patch with ID " + auditPatchDto.getAuditPatchesId() + " not found.");
		            throw new Exception("Audit Patch not found");
		        }
		} catch (Exception e) {
			 logger.error("Error in updateAuditPatch: " + e.getMessage(), e);
		        throw e;
		}
	}
	
}
