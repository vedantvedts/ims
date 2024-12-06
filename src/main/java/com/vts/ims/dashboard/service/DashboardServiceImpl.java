package com.vts.ims.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vts.ims.audit.dto.AuditScheduleListDto;
import com.vts.ims.audit.dto.AuditeeDto;
import com.vts.ims.audit.dto.AuditorDto;
import com.vts.ims.audit.dto.AuditorTeamDto;
import com.vts.ims.audit.service.AuditService;
import com.vts.ims.dashboard.dto.CheckListDetailsDto;
import com.vts.ims.qms.dto.QmsQmRevisionRecordDto;
import com.vts.ims.qms.model.QmsQmRevisionRecord;
import com.vts.ims.qms.repository.QmsQmRevisionRecordRepo;
import com.vts.ims.qms.service.QmsServiceImpl;

@Service
public class DashboardServiceImpl implements DashboardService {

	private static final Logger logger=LogManager.getLogger(QmsServiceImpl.class);


	@Autowired
	private QmsQmRevisionRecordRepo qmsQmRevisionRecordRepo;
	
	@Autowired
	private AuditService auditService;
	
	
	@Override
	public List<QmsQmRevisionRecordDto> getQmVersionDetailedDtoList() throws Exception {
		logger.info( " Inside getQmVersionDetailedDtoList() " );
		try {
			
			
			List<QmsQmRevisionRecordDto> qmsQmRevisionRecordDtoList = new ArrayList<QmsQmRevisionRecordDto>();
			List<QmsQmRevisionRecord> qmRevisionRecord = qmsQmRevisionRecordRepo.findAllActiveQmRecords();
			qmRevisionRecord.forEach(revison -> {
				QmsQmRevisionRecordDto qmsQmRevisionRecordDto = QmsQmRevisionRecordDto.builder()
						.RevisionRecordId(revison.getRevisionRecordId())
						.DocFileName(revison.getDocFileName())
						.DocFilepath(revison.getDocFilepath())
						.Description(revison.getDescription())
						.IssueNo(revison.getIssueNo())
						.RevisionNo(revison.getRevisionNo())
						.DateOfRevision(revison.getDateOfRevision())
						.StatusCode(revison.getStatusCode())
						.AbbreviationIdNotReq(revison.getAbbreviationIdNotReq())
						.CreatedBy(revison.getCreatedBy())
						.CreatedDate(revison.getCreatedDate())
						.ModifiedBy(revison.getModifiedBy())
						.ModifiedDate(revison.getModifiedDate())
						.IsActive(revison.getIsActive())
						.build();
				
				qmsQmRevisionRecordDtoList.add(qmsQmRevisionRecordDto);
			});
			
			return qmsQmRevisionRecordDtoList;
		} catch (Exception e) {
			logger.info( " Inside getQmVersionDetailedDtoList() "+ e );
			e.printStackTrace();
			return new ArrayList<QmsQmRevisionRecordDto>();
		}
	}
	
	
	public long getNoOfActiveAuditees() throws Exception {
	    logger.info("Inside getNoOfActiveAuditees()");
	    long count = 0; // Default to 0 if the list is empty or null
	    
	    try {
	        List<AuditeeDto> dto = auditService.getAuditeeList(); // Fetch the list
	        if (dto != null) {
	            count = dto.size(); // Set count to the size of the list
	        }
	    } catch (Exception e) {
	        logger.error("Exception in getNoOfActiveAuditees(): ", e); // Log error with details
	    }
	    
	    return count; // Return the count
	}


	
	public long getNoOfActiveAuditors() throws Exception{
		logger.info( " Inside getNoOfActiveAuditors() " );
		long count=0;
		try {
			List<AuditorDto> dto=auditService.getAuditorList();
		        if (dto != null) {
		            count = dto.size(); // Set count to the size of the list
		        }
			
	    } catch (Exception e) {
		logger.info( " Inside getNoOfActiveAuditors() "+ e );
	 	e.printStackTrace();
	  }
		return count;
	}
	

	
	public long getNoOfActiveTeams() throws Exception{
		logger.info( " Inside getNoOfActiveTeams() " );
		long count=0;
		try {
			List<AuditorTeamDto> dto=auditService.getAuditTeamMainList();
		        if (dto != null) {
		            count = dto.size(); // Set count to the size of the list
		        }
			
	    } catch (Exception e) {
		logger.info( " Inside getNoOfActiveTeams() "+ e );
	 	e.printStackTrace();
	  }
		return count;
	}
	
	
	public long getNoOfActiveSchedules() throws Exception{
		logger.info( " Inside getNoOfActiveSchedules() " );
		long count=0;
		try {
			  List<AuditScheduleListDto> dto=auditService.getScheduleList();
		        if (dto != null) {
		            count = dto.size(); 
		        }
		} catch (Exception e) {
		logger.info( " Inside getNoOfActiveSchedules() "+ e );
	 	e.printStackTrace();
	  }
		return count;
	}
	

	@Override
	public List<CheckListDetailsDto> getChecklistDetailedList() throws Exception {
		logger.info( " Inside getChecklistDetailedList() " );
		try {
			
			
			List<CheckListDetailsDto> qmsCheckListDetailedDtoList = new ArrayList<CheckListDetailsDto>();
			//List<CheckListDetailsDto> checkListDataList = qmsQmRevisionRecordRepo.findAllActiveQmRecords();
			
//			checkListDataList.forEach(revison -> {
//				CheckListDetailsDto checkListDetailsDto = CheckListDetailsDto.builder()
//						.build();
//				
//				qmsCheckListDetailedDtoList.add(checkListDetailsDto);
//			});
			
			return qmsCheckListDetailedDtoList;
		} catch (Exception e) {
			logger.info( " Inside getChecklistDetailedList() "+ e );
			e.printStackTrace();
			return new ArrayList<CheckListDetailsDto>();
		}
	}
	
	
	
}
