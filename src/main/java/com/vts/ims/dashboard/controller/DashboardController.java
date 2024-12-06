package com.vts.ims.dashboard.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.vts.ims.audit.dto.AuditorTeamDto;
import com.vts.ims.dashboard.dto.CheckListDetailsDto;
import com.vts.ims.dashboard.service.DashboardService;
import com.vts.ims.qms.dto.QmsQmRevisionRecordDto;
import com.vts.ims.qms.service.QmsService;

@RestController
@CrossOrigin("*")
public class DashboardController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DashboardService service;
	
	@PostMapping(value = "/get-qm-dashboard-list", produces = "application/json")
	public List<QmsQmRevisionRecordDto> getQmDashboardList(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-qm-dashboard-list " + username);
	    // Fetch the list from the service
	    List<QmsQmRevisionRecordDto> records = service.getQmVersionDetailedDtoList();

	    // Check the size of the list and return data accordingly
	    if (records!=null && records.size() > 0) {
	        if (records.size() > 1) {
	            // Return only the second row (index 1) if size > 1
	            return List.of(records.get(1));
	        } else {
	            // Return only the first row (index 0) if size <= 1
	            return List.of(records.get(0));
	        }
	    }

	    // Return an empty list if no records are available
	    return Collections.emptyList();
	}
	

	

	
	@PostMapping(value = "/get-no-of-active-auditors", produces = "application/json")
	public long noOfActiveAuditors(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-no-of-active-auditors " + username);
	    // Fetch the list from the service
	    long count = service.getNoOfActiveAuditors();

	    return count;
	}
	
	@PostMapping(value = "/get-no-of-active-auditee", produces = "application/json")
	public long noOfActiveAuditee(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-no-of-active-auditee " + username);
	    // Fetch the list from the service
	    long count = service.getNoOfActiveAuditees();

	    return count;
	}
	
	@PostMapping(value = "/get-no-of-active-teams", produces = "application/json")
	public long noOfActiveTeams(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-no-of-active-teams " + username);
	    // Fetch the list from the service
	    long count = service.getNoOfActiveTeams();

	    return count;
	}
	
	@PostMapping(value = "/get-no-of-active-schedules", produces = "application/json")
	public long noOfActiveSchedules(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-no-of-active-schedules " + username);
	    // Fetch the list from the service
	    long count = service.getNoOfActiveSchedules();

	    return count;
	}
	
	
	@PostMapping(value = "/get-checklist-detailed-list", produces = "application/json")
	public List<CheckListDetailsDto> getCheckListData(@RequestHeader  String username) throws Exception {
		logger.info(" get-checklist-detailed-list " + username);
	    // Fetch the list from the service
	    List<CheckListDetailsDto> records = service.getChecklistDetailedList();
	    // Return an empty list if no records are available
	    return Collections.emptyList();
	}
	


	

	
	
	
	
	
}
