package com.vts.ims.dashboard.service;

import java.util.List;

import com.vts.ims.audit.dto.AuditorTeamDto;
import com.vts.ims.dashboard.dto.CheckListDetailsDto;
import com.vts.ims.qms.dto.QmsQmRevisionRecordDto;

public interface DashboardService {
	
	public List<QmsQmRevisionRecordDto> getQmVersionDetailedDtoList() throws Exception;

	public long getNoOfActiveAuditees() throws Exception;
	public long getNoOfActiveAuditors() throws Exception;
	public long getNoOfActiveTeams() throws Exception;
	public long getNoOfActiveSchedules() throws Exception;
	
	public List<CheckListDetailsDto> getChecklistDetailedList() throws Exception;
	
}
