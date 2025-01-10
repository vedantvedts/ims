package com.vts.ims.admin.controller;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vts.ims.admin.dto.*;
import com.vts.ims.master.dto.DivisionMasterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vts.ims.admin.service.AdminService;
import com.vts.ims.audit.dto.AuditPatchDto;
import com.vts.ims.master.dto.EmployeeDto;
import com.vts.ims.master.dto.LoginDetailsDto;
import com.vts.ims.login.Login;
import com.vts.ims.model.LoginStamping;
import com.vts.ims.login.LoginRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;


@RestController
@CrossOrigin("*")
public class AdminController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	private AdminService service;
	
	@Autowired
	LoginRepository loginRepo;
	
	
	@RequestMapping (value="header-module", method=RequestMethod.POST,produces="application/json")
	public List<FormModuleDto> headerModule(@RequestBody Long imsFormRoleId) throws Exception {
		logger.info(new Date() + " Inside header-module ");
		return service.formModuleList(imsFormRoleId);
	}
	
	
	@RequestMapping (value="header-detail", method=RequestMethod.POST,produces="application/json")
	public List<FormDetailDto> headerDetail(@RequestBody Long imsFormRoleId) throws Exception {
		logger.info(new Date() + " Inside header-detail ");
		return service.formModuleDetailList(imsFormRoleId);
	}
	
	
	

	@PostMapping(value="/login/{user}", produces="application/json")
	public ResponseEntity<List<LoginDetailsDto>> getLoginDetails(
			@PathVariable("user") String user,
			@RequestHeader("Username") String username) throws Exception {
		
		logger.info(new Date() +" Inside login data fetch ");
		List<LoginDetailsDto> details = null;
		try {
		if (username == null || !username.equals(user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		details = service.loginDetailsList(user);
		
	    } catch (Exception e) {
		  logger.error(new Date() +" error Inside login data fetch ");
	         e.printStackTrace();
	     }
		return ResponseEntity.ok(details);
	}
	  
	
	
	
	@PostMapping(value = "/employee-list", produces = "application/json")
	public ResponseEntity<List<EmployeeDto>> getEmployelist(@RequestHeader String username) throws Exception {
		try {
			logger.info(new Date() + " Inside getEmployelist" );
			List<EmployeeDto> dto=service.employeeList();
			return new ResponseEntity<List<EmployeeDto>>( dto,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error fetching getEmployelist: ", e);
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	 /* ---------------------------AuditStampinglist------------------------------ */
   
	@PostMapping(value="custom-audit-stamping-login" ,produces="application/json")
	public String logIn(@RequestBody String username, Authentication authentication, HttpServletRequest request)throws Exception {
		
		logger.info(new Date() +" Inside custom-audit-stamping-login " +authentication.getName());
		long result=0;
		
		
		username = username.replace("\"", "");
		String IpAddress="Not Available";

     		try{
     			IpAddress = request.getRemoteAddr();
     		 
	     		if("0:0:0:0:0:0:0:1".equalsIgnoreCase(IpAddress))
	     		{	     			
	     			InetAddress ip = InetAddress.getLocalHost();
	     			IpAddress= ip.getHostAddress();
	     		}
     		
     		}
     		catch(Exception e)
     		{
     		IpAddress="Not Available";	
     		e.printStackTrace();	
     		}
     		try{
     			Login login=loginRepo.findByUsername(username);
     			LoginStamping stamping=new LoginStamping();
		        stamping.setLoginId(login.getLoginId());
		        stamping.setLoginDate(new java.sql.Date(new Date().getTime()));
		        stamping.setUsername(login.getUsername());
		        stamping.setIpAddress(IpAddress);
		        stamping.setLoginDateTime(LocalDateTime.now());
		        result = service.loginStampingInsert(stamping);
     		}catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() +" error in custom-audit-stamping-login "+ e.getMessage());
			}
		
	
	 		return String.valueOf(result);
		
	}	
	
	
	 @PostMapping(value = "custom-audit-stamping-logout", produces = "application/json")
	 public String logout(@RequestBody JsonNode requestBody, Authentication authentication) throws Exception {
	     logger.info(new Date() + " Inside custom-auditStamping-logout " + authentication.getName());
	     long result = 0;
	     long loginid = 0;

	     String username = requestBody.get("username").asText();
	     String logoutType = requestBody.get("logoutType").asText();
	     Login login = loginRepo.findByUsername(username);
	     loginid = login.getLoginId();
	     try {
	         if (loginid > 0) {
	             LoginStamping stamping = new LoginStamping();
	             stamping.setAuditStampingId(service.lastLoginStampingId(loginid));
	             stamping.setLogOutType(logoutType);
	             stamping.setLogOutDateTime(LocalDateTime.now());
	             result = service.loginStampingUpdate(stamping);
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	         logger.error(new Date() +" error in custom-audit-stamping-logout "+ e.getMessage());
	     }
	     return String.valueOf(result);
	 }


	@PostMapping(value="audit-stamping-list",produces="application/json")
	 public ResponseEntity<List<AuditStampingDto>> AuditStampingList(@RequestBody AuditStampingDto stamping, @RequestHeader  String username)throws Exception{
    	logger.info(new Date() + " Inside audit-stamping-list " );
    	   List<AuditStampingDto> list = null;
    	try {
    		list =  service.getAuditStampinglist(stamping);
    	} catch (Exception e) {
			 logger.error(new Date() +"error in audit-stamping-list "+ e.getMessage());
			 e.printStackTrace();
		}
		 return new ResponseEntity<>(list, HttpStatus.OK);

	 }


	@PostMapping(value = "user-manager-list", produces="application/json")
	public ResponseEntity <List<UserManagerListDto>> UserManagerList(@RequestHeader  String username) throws Exception {
		logger.info(new Date() + " Inside user-manager-list " );

		List<UserManagerListDto> list=null;
		try {
			list=service.UserManagerList(username);
		} catch (Exception e) {
			logger.error(new Date() +" error in user-manager-list "+ e.getMessage());
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}


	@PostMapping(value = "roles-list", produces="application/json")
	public ResponseEntity<List<FormRoleDto>> RoleList() throws Exception{
		logger.info(new Date() + " Inside roles-list " );
		List<FormRoleDto> list= null;
		try {
			list=service.roleList();
		} catch (Exception e) {
			logger.error(new Date() +" error in roles-list "+ e.getMessage());
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PostMapping(value = "form-modules-list", produces="application/json")
	public ResponseEntity<List<FormModuleDto>> formModule()throws Exception{
		logger.info(new Date() + " Inside form-modules-list " );
		List<FormModuleDto> list = null;

		try {
			list =  service.getformModulelist();
		} catch (Exception e) {
			logger.error(new Date() +"error in form-modules-list "+ e.getMessage());
			e.printStackTrace();
		}

		return new ResponseEntity<>(list, HttpStatus.OK);
	}


	@PostMapping(value = "form-role-access-list", produces = "application/json")
	public ResponseEntity<List<FormroleAccessDto>> formRoleAccessList(@RequestBody Map<String, String> request) throws Exception {
		logger.info(new Date() + " Inside form-role-access-list");
		List<FormroleAccessDto> list = null;

		try {
			String roleId = request.get("roleId");
			String formModuleId = request.get("formModuleId");
			list = service.getformRoleAccessList(roleId, formModuleId);
		} catch (Exception e) {
			logger.error(new Date() +"error in form-role-access-list "+ e.getMessage());
			e.printStackTrace();
		}

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PostMapping(value="update-form-role-access", produces="application/json")
	public String updateFormRoleAccess(@RequestBody FormroleAccessDto accessDto, @RequestHeader  String username) throws Exception {
		logger.info(new Date() + " Inside Update update-form-role-access ");
		String result=null;
		try {
			result = service.updateformroleaccess(accessDto, username);
			return result;
		} catch (Exception e) {
			logger.error(new Date() +"error update-form-role-access "+ e.getMessage());
			e.printStackTrace();
			return result;
		}
	}

	@RequestMapping(value = "user-manager-edit-data", method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<UserManageAddEditDto> UserManagerEditData(@RequestBody String LoginId)
			throws Exception {
		logger.info(new Date() + " Inside user-manager-edit-data " );
		try {
			UserManageAddEditDto list = service.UserManagerEditData(LoginId);
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(new Date() +"error in user-manager-edit-data "+ e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(new UserManageAddEditDto(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "user-manager-edit-submit", method = RequestMethod.POST, produces="application/json")
	public String UserManagerEditSubmit(@RequestBody UserManageAddEditDto UserManageAdd,Authentication authentication)
			throws Exception {
		logger.info(new Date() + " Inside user-manager-edit-submit " );
		int count = 0;
		try {
			count = service.UserManagerUpdate(UserManageAdd, authentication.getName());

		} catch (Exception e) {
			logger.error(new Date() +"error in user-manager-edit-submit "+ e.getMessage());
			e.printStackTrace();
		}
		return String.valueOf(count);
	}

	@RequestMapping(value = "username-present-count", method = RequestMethod.POST,produces="application/json")
	public  String UserNamePresentCount(@RequestBody String userName) throws Exception {
		logger.info(new Date() + " Inside username-present-count " );
		userName = userName.replace("\"", "");
		long UserNamePresentCount = 0;
		try {
			UserNamePresentCount = service.UserNamePresentCount(userName);
		} catch (Exception e) {
			logger.error(new Date() +"error in username-present-count "+ e.getMessage());
			e.printStackTrace();
		}
		return String.valueOf(UserNamePresentCount);
	}
	
	
	
	@PostMapping(value = "approval-authority-list", produces="application/json")
	public ResponseEntity <List<ApprovalAuthorityDto>> approvalAuthorityList(@RequestHeader  String username) throws Exception {
		logger.info(new Date() + " Inside approval-authority-list" );

		List<ApprovalAuthorityDto> list=null;
		try {
			list=service.approvalAuthorityList();
		} catch (Exception e) {
			logger.error(new Date() +" error in approval-authority-list"+ e.getMessage());
			e.printStackTrace();
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/insert-approval-authority", produces = "application/json")
	public ResponseEntity<String> insertApprovalAuthority( @RequestBody ApprovalAuthorityDto approvalAuthorityDto,@RequestHeader String username) throws Exception {
		try {
			logger.info(new Date() + " Inside insert-approval-authority" );
			long insertApprovalAuthority=service.insertApprovalAuthority(approvalAuthorityDto,username);
			 if(insertApprovalAuthority > 0) {
				 return new ResponseEntity<String>("200" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("500" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error(new Date() +"error in insert-approval-authority"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}
	
	@RequestMapping (value="/approval_authority-inactive", method=RequestMethod.POST,produces="application/json")
    public ResponseEntity<String> approvalAuthorityInactive(@RequestBody ApprovalAuthorityDto approvalAuthorityDto, @RequestHeader  String username) throws Exception{
   		 try {
   			logger.info("{} Inside approval_authority-inactive");
   			long result=service.approvalAuthorityInactive(approvalAuthorityDto,username);
		    if(result > 0) {
		    	return new ResponseEntity<String>("200" , HttpStatus.OK);
		    }else {
		    	return new ResponseEntity<String>("500" , HttpStatus.BAD_REQUEST);
		    }
   		 } catch (Exception e) {
  			  logger.error(new Date() +" error in approval_authority-inactive");
  		         e.printStackTrace();
  		       return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
  		}
	}
	
	@PostMapping(value = "/update-approval-authority", produces = "application/json")	
	public ResponseEntity<String> UpdateApprovalAuthority(@RequestHeader String username, @RequestBody ApprovalAuthorityDto approvalAuthorityDto) throws Exception {
		try {
			logger.info("update-approval-authority");
			Long result=service.UpdateApprovalAuthority(approvalAuthorityDto,username);
			 if(result > 0) {
				 return new ResponseEntity<String>("200" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("500" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error("update-approval-authority"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}

	

	
	
	 @PostMapping(value = "get-notification-count", produces="application/json")
		public ResponseEntity<Integer> getNotificationCount(@RequestHeader  String username) throws Exception {
			logger.info(new Date() +" Inside get get-notification-count" +username);
			Integer result = service.getNotifictionCount(username);
			if (result != null) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}

	 
	 
	    @PostMapping(value = "/get-notification-list", produces="application/json")
		public ResponseEntity<List<NotificationDto>> getNotification(@RequestHeader  String username) throws Exception {
			logger.info(new Date() +" Inside get get-notification List " +username);
			List<NotificationDto> result = service.getNotifictionList(username);
			if (result != null) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}


	    @PostMapping(value = "/update-notification", produces="application/json")
		public ResponseEntity<Long> updateNotification(@RequestHeader  String username, @RequestBody String notificationId) throws Exception {
			logger.info(new Date() +" Inside  update-notification  " +username);
			long result = service.updateNotification(username,notificationId);
			if (result != 0) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	    
	    
	    
	    	
	    @GetMapping(value = "/get-audit-patch-list", produces="application/json")
		public ResponseEntity<List<AuditPatchDto>> getAuditPatchList(@RequestHeader  String username) throws Exception {
			logger.info(new Date() +" Inside get-audit-patch-list " +username);
			List<AuditPatchDto> result = service.getAuditPatchList();
			if (result != null) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	    
	    
	    @PostMapping(value = "/update-audit-patch", produces="application/json")
		public ResponseEntity<String> updateAuditPatch(@RequestHeader  String username, @RequestBody AuditPatchDto auditPatchDto) throws Exception {
			logger.info(new Date() +" Inside update-audit-patch" +username);
			try {
			Long result = service.updateAuditPatch(auditPatchDto,username);
			if(result > 0) {
				 return new ResponseEntity<String>("200" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("500" , HttpStatus.BAD_REQUEST);
			 }
			} catch (Exception e) {
				 logger.error("update-approval-authority"+ e.getMessage());
				 e.printStackTrace();
				 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
			}
		}
	
}
