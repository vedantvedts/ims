package com.vts.ims.dashboard.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.vts.ims.audit.dto.AuditorTeamDto;
import com.vts.ims.audit.dto.IqaDto;
import com.vts.ims.dashboard.dto.CheckListObsCountDto;
import com.vts.ims.dashboard.service.DashboardService;
import com.vts.ims.qms.dto.DwpRevisionRecordDto;
import com.vts.ims.qms.dto.QmsDocTypeDto;
import com.vts.ims.qms.dto.QmsQmRevisionRecordDto;
import com.vts.ims.qms.service.QmsService;

@RestController
@CrossOrigin("*")
public class DashboardController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DashboardService service;
	
	@PostMapping(value = "/iqa-list-for-dashboard", produces = "application/json")
	public ResponseEntity<List<IqaDto>> iqalistForDashboard(@RequestHeader String username) throws Exception {
		try {
			logger.info(new Date() + " Inside iqalistForDashboard" );
			List<IqaDto> dto=service.getIqaListForDashboard();
			return new ResponseEntity<List<IqaDto>>( dto,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error fetching iqalistForDashboard: ", e);
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/dwp-revision-list-for-dashboard", produces = "application/json")
	public ResponseEntity<List<DwpRevisionRecordDto>> getDwpVersionRecordDtoListForDahboard(@RequestHeader String username) throws Exception {
		try {
			logger.info(new Date() + " Inside getDwpVersionRecordDtoListForDahboard" );
			List<DwpRevisionRecordDto> dto=service.getAllDwpRevisionListForDashboard();
			return new ResponseEntity<List<DwpRevisionRecordDto>>( dto,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error fetching getDwpVersionRecordDtoListForDahboard: ", e);
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
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
	    long count = service.getNoOfActiveAuditors();

	    return count;
	}
	
	@PostMapping(value = "/get-no-of-active-auditee", produces = "application/json")
	public long noOfActiveAuditee(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-no-of-active-auditee " + username);
	    long count = service.getNoOfActiveAuditees();

	    return count;
	}
	
	@PostMapping(value = "/get-no-of-active-teams", produces = "application/json")
	public long noOfActiveTeams(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-no-of-active-teams " + username);
	    long count = service.getNoOfActiveTeams();

	    return count;
	}
	
	@PostMapping(value = "/get-no-of-active-schedules", produces = "application/json")
	public long noOfActiveSchedules(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-no-of-active-schedules " + username);
	    long count = service.getNoOfActiveSchedules();
	    return count;
	}
	
	
	@PostMapping(value = "/get-total-obs-count-by-iqa", produces = "application/json")
	public List<CheckListObsCountDto> getTotalChecklistObsCountByIqa(@RequestHeader  String username) throws Exception {
		logger.info("get-total-obs-count-by-iqa" + username);
	    List<CheckListObsCountDto> records = service.getTotalObsCountByIqa();
	    return records;
	}
	
	
	@PostMapping(value = "/get-checklist-by-observation", produces = "application/json")
	public List<CheckListObsCountDto> get(@RequestHeader  String username) throws Exception {
		logger.info("get-checklist-by-observation" + username);
	    List<CheckListObsCountDto> records = service.getCheckListDataByObservation();
	    return records;
	}
	
	

	@PostMapping(value = "/get-all-version-record-list", produces = "application/json")
	public List<DwpRevisionRecordDto> getAllVersionRecordDtoList(@RequestBody QmsDocTypeDto qmsDocTypeDto, @RequestHeader  String username) throws Exception {
		logger.info(" Inside get-all-version-record-list");
		return service.getAllVersionRecordDtoList(qmsDocTypeDto);
	}
	


	

	
	
	
	
	
}
