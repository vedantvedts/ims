package com.vts.ims.qms.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.vts.ims.qms.dto.CheckListMasterDto;
import com.vts.ims.qms.dto.DwpRevisionRecordDto;
import com.vts.ims.qms.dto.QmsQmChaptersDto;
import com.vts.ims.qms.dto.QmsQmDocumentSummaryDto;
import com.vts.ims.qms.dto.QmsQmMappingDto;
import com.vts.ims.qms.dto.QmsQmRevisionRecordDto;
import com.vts.ims.qms.dto.QmsQmSectionsDto;
import com.vts.ims.qms.model.DwpChapters;
import com.vts.ims.qms.model.DwpGwpDocumentSummary;
import com.vts.ims.qms.model.DwpRevisionRecord;
import com.vts.ims.qms.model.DwpSections;
import com.vts.ims.qms.model.QmsAbbreviations;
import com.vts.ims.qms.model.QmsQmDocumentSummary;
import com.vts.ims.qms.model.QmsQmRevisionRecord;

public interface QmsService {

	
	
	public List<QmsQmRevisionRecordDto> getQmVersionRecordDtoList() throws Exception;
	public List<QmsQmChaptersDto> getAllQMChapters() throws Exception;
	public List<QmsQmSectionsDto> getUnAddedQmSectionList() throws Exception;
	public Long addNewQmSection(String sectionName, String username) throws Exception;
	public Long qmUnAddListToAddList(long[] selectedSections, String username) throws Exception;
	public List<QmsQmChaptersDto> getQmSubChaptersById(Long chapterId) throws Exception;
	public Long addQmNewSubChapter(Long chapterId, String chapterName, String username) throws Exception;
	public Long updateQmChapterContent(Long chapterId, String chapterContent, String username) throws Exception;
	public Long updateQmChapterName(Long chapterId, String chapterName, String username) throws Exception;
	public Long addNewQmRevision(QmsQmRevisionRecordDto qmsQmRevisionRecordDto, String username) throws Exception;
//	public Long addQmMappingOfClasses(List<String[]> mocList, String username) throws Exception;
	public Long addQmDocSummary(QmsQmDocumentSummaryDto qmsQmDocumentSummaryDto, String username) throws Exception;
	public QmsQmDocumentSummaryDto getQmDocSummarybyId(long documentSummaryId) throws Exception;
	public long deleteQmChapterById(long chapterId , String username) throws Exception;
	public QmsQmChaptersDto getQmChapterById(long chapterId) throws Exception;
	public long updatechapterPagebreakAndLandscape(String[] chapterPagebreakOrLandscape, String username) throws Exception;
	public List<QmsAbbreviations> getAbbreviationList(String abbreviationIdNotReq) throws Exception;
	public QmsQmRevisionRecord getQmsQmRevisionRecord(Long revisionRecordId) throws Exception;
	public long updateNotReqQmAbbreviationIds(Long revisionRecordId, String AbbreviationIds, String username) throws Exception;
	public QmsQmDocumentSummary getQmDocSummarybyRevisionRecordId(long revisionRecordId) throws Exception;
	public Long addMappingOfClasses(Long revisionRecordId, List<String[]> mocList, String username) throws Exception;
	public List<Object[]> getMocList(Long revisionRecordId) throws Exception;
	public List<QmsQmMappingDto> getMoctotalList() throws Exception; 
	public List<DwpRevisionRecordDto> getDwpVersionRecordDtoList(Long divisionId) throws Exception;
	public List<DwpChapters> getAllDwpChapters(Long divisionId) throws Exception;
	public Long updateDwpChapterName(Long chapterId, String chapterName, String username) throws Exception;
	public List<DwpChapters> getDwpSubChaptersById(Long chapterId) throws Exception;
	public long deleteDwpChapterById(Long chapterId , String username) throws Exception;
	public Long addDwpNewSubChapter(Long chapterId, String chapterName, String username) throws Exception;
	public long updateDwpPagebreakAndLandscape(String[] chapterPagebreakOrLandscape, String username) throws Exception;
	public DwpChapters getDwpChapterById(long chapterId) throws Exception;
	public Long updateDwpChapterContent(Long chapterId, String chapterContent, String username) throws Exception;
	public List<DwpSections> getDwpUnAddedQmSectionList(Long divisionId) throws Exception;
	public Long addNewDwpSection(long divisionId, String sectionName, String username) throws Exception;
	public Long dwpUnAddListToAddList(@RequestBody long[] selectedSections, @RequestHeader  String username) throws Exception;
	public Integer updateChapterDescById(CheckListMasterDto checkListMasterDto, String username)throws Exception;
	public Integer deleteChapterDescById(String mocId, String username)throws Exception;
	public Integer deleteSubChapterDescById(String mocId, String username)throws Exception;
	public Long addNewChapter(CheckListMasterDto checkListMasterDto, String username)throws Exception;
	public int addChapterToMasters(List<String> mocIds, String username)throws Exception;
	public Integer updateCheckListChapters(List<Long> mocIds, String username)throws Exception;
	public DwpRevisionRecord getDwpRevisionRecord(Long revisionRecordId) throws Exception;
	public long updateNotReqDwpAbbreviationIds(Long revisionRecordId, String abbreviationIds, String username) throws Exception;
	public Long addDwpDocSummary(DwpGwpDocumentSummary documentSummary, String username) throws Exception;
	public DwpGwpDocumentSummary getDwpDocSummarybyRevisionRecordId(long revisionRecordId) throws Exception;
	
	
}
