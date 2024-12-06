package com.vts.ims.qms.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.vts.ims.qms.service.QmsService;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin("*")
@RestController
public class QmsController {

	private static final Logger logger = LogManager.getLogger(QmsController.class);
	
	
	
	@Value("${appStorage}")
	private String storageDrive ;
	
	@Autowired
	QmsService service;
	
	
	@PostMapping(value = "/get-qm-version-record-list", produces = "application/json")
	public List<QmsQmRevisionRecordDto> getQmVersionRecordDtoList(@RequestHeader  String username) throws Exception {
		logger.info(" Inside get-qm-version-record-list " + username);
		return service.getQmVersionRecordDtoList();
	}
	
	@PostMapping(value = "/get-all-qm-chapters", produces = "application/json")
	public List<QmsQmChaptersDto> getAllQMChapters(@RequestHeader String username) throws Exception {
		logger.info(" Inside get-all-qm-chapters " + username);
		return service.getAllQMChapters();
	}
	
	@PostMapping(value = "/un-added-qm-section-list", produces = "application/json")
	public List<QmsQmSectionsDto> unAddedQmSectionList(@RequestHeader String username) throws Exception {
		logger.info(" Inside un-added-qm-section-list " + username);
		return service.getUnAddedQmSectionList();
	}
	
	
	@PostMapping(value = "/add-new-qm-section", produces = "application/json")
	public Long addNewQmSection(@RequestBody String sectionName, @RequestHeader String username) throws Exception {
		logger.info(" Inside add-new-qm-section " + username);
		return service.addNewQmSection(sectionName, username);
	}
	
	@PostMapping(value = "/qm-unaddlist-to-addlist", produces = "application/json")
	public Long qmUnAddListToAddList(@RequestBody long[] selectedSections, @RequestHeader  String username) throws Exception {
		logger.info(" Inside qm-unaddlist-to-addlist " + username);
		return service.qmUnAddListToAddList(selectedSections, username);
	}
	
	@PostMapping(value = "/get-qm-subchapters", produces = "application/json")
	public List<QmsQmChaptersDto> getQmSubChaptersById(@RequestBody Long chapterId, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-qm-subchapters-by-id " + username);
		return service.getQmSubChaptersById(chapterId);
	}
	
	@PostMapping(value = "/add-qm-new-subchapter/{chapterId}", produces = "application/json")
	public Long addQmNewSubChapter(@PathVariable("chapterId") Long chapterId, @RequestBody String chapterName, @RequestHeader  String username) throws Exception {
		logger.info(" Inside add-qm-new-subchapter " + username);
		return service.addQmNewSubChapter(chapterId, chapterName, username);
	}
	
	@PostMapping(value = "/update-qm-chaptercontent/{chapterId}", produces = "application/json")
	public Long updateQmChapterContent(@PathVariable("chapterId") Long chapterId, @RequestBody String chapterContent, @RequestHeader String username) throws Exception {
		logger.info(" Inside update-qm-chaptercontent " + username);
		return service.updateQmChapterContent(chapterId, chapterContent, username);
	}
	
	@PostMapping(value = "/update-qm-chaptername/{chapterId}", produces = "application/json")
	public Long updateQmChapterNameById(@PathVariable("chapterId") Long chapterId, @RequestBody String chapterName, @RequestHeader String username) throws Exception {
		logger.info(" Inside update-qm-chaptername " + username);
		return service.updateQmChapterName(chapterId, chapterName, username);
	}
	
	@PostMapping(value = "/add-new-qm-revision", produces = "application/json")
	public Long addNewQmRevision(@RequestBody QmsQmRevisionRecordDto qmsQmRevisionRecordDto, @RequestHeader String username) throws Exception {
		logger.info(" Inside add-new-qm-revision " + username);
		return service.addNewQmRevision(qmsQmRevisionRecordDto, username);
	}
	
	@GetMapping("/download-qm-document")
	public ResponseEntity<Resource> downloadQmDocument(@RequestParam String fileName, HttpServletResponse res, @RequestHeader String username) throws Exception {
		logger.info(" Inside download-qm-document " + username);
	    File file = new File(storageDrive + fileName);

	    if (!file.exists() || !file.isFile()) {
	        throw new FileNotFoundException("File not found: " + fileName);
	    }

	    HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
	    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
	    headers.add("Pragma", "no-cache");
	    headers.add("Expires", "0");

	    Path path = file.toPath();
	    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(file.length())
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	    
	}
	
	@GetMapping("/get-qm-moc-excel")
    public ResponseEntity<FileSystemResource> downloadExcel() throws Exception {
		logger.info(" Inside get-qm-moc-excel " );
		String fileName = "QMS"+File.separator+"QM_Defaults"+File.separator+"Mapping_of_Clauses.xlsx";
        String filePath = storageDrive+fileName;
        
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("File not found: " + filePath);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("filename", file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }
	
	
	@PostMapping(value = "/add-docsummary", produces = "application/json")
	public long addQmDocSummary(@RequestBody QmsQmDocumentSummaryDto qmsQmDocumentSummaryDto, @RequestHeader String username) throws Exception {
		logger.info(" Inside add-docSummary " + username);
		
		return service.addQmDocSummary(qmsQmDocumentSummaryDto, username);
	}

	@PostMapping(value = "/get-docsummary", produces = "application/json")
	public QmsQmDocumentSummaryDto getQmDocSummarybyId(@RequestBody long DocumentSummaryId, @RequestHeader  String username) throws Exception {
		logger.info(" Inside get-docsummary " + username);
		return service.getQmDocSummarybyId(DocumentSummaryId);
	}
	
	@PostMapping(value = "/get-docsummary-by-revisionRecordId", produces = "application/json")
	public QmsQmDocumentSummary getQmDocSummarybyRevisionRecordId(@RequestBody long revisionRecordId, @RequestHeader  String username) throws Exception {
		logger.info(" Inside get-docsummary " + username);
		return service.getQmDocSummarybyRevisionRecordId(revisionRecordId);
	}
	
	@PostMapping(value = "/delete-qm-chapteId", produces = "application/json")
	public long deleteQmChapterById(@RequestBody long chapterId, @RequestHeader String username) throws Exception {
		logger.info(" Inside delete-qm-chapteId " + username);
		
		return service.deleteQmChapterById(chapterId, username);
	}
	
	
	@PostMapping(value = "/get-qm-chapter", produces = "application/json")
	public QmsQmChaptersDto getQmChapterById(@RequestBody long chapterId, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-qm-chapteId " + username);
		
		return service.getQmChapterById(chapterId);
	}
	
	@PostMapping(value = "/updatechapter-pagebreak-landscape", produces = "application/json")
	public Long updatechapterPagebreakAndLandscape(@RequestBody String[] chaperContent, @RequestHeader String username) throws Exception {
		logger.info(" Inside updatechapterPagebreakAndLandscape " + username);
		return service.updatechapterPagebreakAndLandscape(chaperContent, username);
	}
	
	
	@PostMapping(value = "/get-abbreviationlist", produces = "application/json")
	public List<QmsAbbreviations> getAbbreviationList(@RequestBody String abbreviationIdNotReq, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-abbreviationlist " + username);
		return service.getAbbreviationList(abbreviationIdNotReq);
	}
	
	
	@PostMapping(value = "/get-qm-revision-record", produces = "application/json")
	public QmsQmRevisionRecord getQmVersionRecordById(@RequestBody Long revisionRecordId, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-qm-revision-record " + username);
		return service.getQmsQmRevisionRecord(revisionRecordId);
	}
	
	@PostMapping(value = "update-qm-notreq-abbreviation/{revisionRecordId}")
	public long updateNotReqQmAbbreviationIds(@PathVariable("revisionRecordId") Long revisionRecordId, @RequestBody String abbreviationIds, @RequestHeader String username ) throws Exception {
		logger.info(" Inside update-qm-notreq-abbreviation " + username);
		return service.updateNotReqQmAbbreviationIds(revisionRecordId, abbreviationIds, username);
	}
	
	@PostMapping(value = "/add-moc/{revisionRecordId}", produces = "application/json")
	public  Long addMappingOfClasses(@PathVariable("revisionRecordId") Long revisionRecordId,@RequestBody List<String[]> mocList, @RequestHeader  String username) throws Exception {
		logger.info(" Inside add-moc " + username);
		return service.addMappingOfClasses(revisionRecordId, mocList, username);
	}
	
	
	@PostMapping(value = "/get-moclist", produces = "application/json")
	public  List<Object[]> getMocList(@RequestBody Long revisionRecordId, @RequestHeader  String username) throws Exception {
		logger.info(" Inside add-moc " + username);
		return service.getMocList(revisionRecordId);
	}
	
	@PostMapping(value = "/get-moc-total-list", produces = "application/json")
	public  List<QmsQmMappingDto> getMocTotalList( @RequestHeader  String username) throws Exception {
		logger.info(" get-moc-total-list " + username);
		return service.getMoctotalList();
	}
	
	@PostMapping(value = "/get-dwp-version-record-list", produces = "application/json")
	public List<DwpRevisionRecordDto> getDwpVersionRecordDtoList(@RequestBody Long divisionId, @RequestHeader  String username) throws Exception {
		logger.info(" Inside get-dwp-version-record-list " + username);
		return service.getDwpVersionRecordDtoList(divisionId);
	}
	
	@PostMapping(value = "/get-all-dwp-chapters", produces = "application/json")
	public List<DwpChapters> getAllDwpChapters(@RequestBody Long divisionId, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-all-dwp-chapters " + username);
		return service.getAllDwpChapters(divisionId);
	}
	
	@PostMapping(value = "/update-dwp-chaptername/{chapterId}", produces = "application/json")
	public Long updateDwpChapterNameById(@PathVariable("chapterId") Long chapterId, @RequestBody String chapterName, @RequestHeader String username) throws Exception {
		logger.info(" Inside update-dwp-chaptername " + username);
		return service.updateDwpChapterName(chapterId, chapterName, username);
	}
	
	@PostMapping(value = "/get-dwp-subchapters", produces = "application/json")
	public List<DwpChapters> getDwpSubChaptersById(@RequestBody Long chapterId, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-dwp-subchapters-by-id " + username);
		return service.getDwpSubChaptersById(chapterId);
	}
	
	@PostMapping(value = "/delete-dwp-chapteId", produces = "application/json")
	public Long deleteDwpChapterById(@RequestBody long chapterId, @RequestHeader String username) throws Exception {
		logger.info(" Inside delete-qm-chapteId " + username);
		
		return service.deleteDwpChapterById(chapterId, username);
	}
	
	@PostMapping(value = "/add-dwp-new-subchapter/{chapterId}", produces = "application/json")
	public Long addDwpNewSubChapter(@PathVariable("chapterId") Long chapterId, @RequestBody String chapterName, @RequestHeader  String username) throws Exception {
		logger.info(" Inside add-dwp-new-subchapter " + username);
		return service.addDwpNewSubChapter(chapterId, chapterName, username);
	}
	
	@PostMapping(value = "/update-dwp-pagebreak-landscape", produces = "application/json")
	public Long updateDwpchapterPagebreakAndLandscape(@RequestBody String[] chaperContent, @RequestHeader String username) throws Exception {
		logger.info(" Inside updatechapterPagebreakAndLandscape " + username);
		return service.updateDwpPagebreakAndLandscape(chaperContent, username);
	}
	
	@PostMapping(value = "/get-dwp-chapter", produces = "application/json")
	public DwpChapters getDwpChapterById(@RequestBody long chapterId, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-dwp-chapteId " + username);
		return service.getDwpChapterById(chapterId);
	}
	
	@PostMapping(value = "/update-dwp-chaptercontent/{chapterId}", produces = "application/json")
	public Long updateDwpChapterContent(@PathVariable("chapterId") Long chapterId, @RequestBody String chapterContent, @RequestHeader String username) throws Exception {
		logger.info(" Inside update-dwp-chaptercontent " + username);
		return service.updateDwpChapterContent(chapterId, chapterContent, username);
	}
	
	@PostMapping(value = "/un-added-dwp-section-list", produces = "application/json")
	public List<DwpSections> unDwpAddedQmSectionList(@RequestBody Long divisionId, @RequestHeader String username) throws Exception {
		logger.info(" Inside un-added-dwp-section-list " + username);
		return service.getDwpUnAddedQmSectionList(divisionId);
	}
	
	@PostMapping(value = "/add-new-dwp-section/{divisionId}", produces = "application/json")
	public Long addNewQmSection(@PathVariable("divisionId") long divisionId, @RequestBody String sectionName, @RequestHeader String username) throws Exception {
		logger.info(" Inside add-new-qm-section " + username);
		return service.addNewDwpSection(divisionId, sectionName, username);
	}
	
	@PostMapping(value = "/dwp-unaddlist-to-addlist", produces = "application/json")
	public Long dwpUnAddListToAddList(@RequestBody long[] selectedSections, @RequestHeader  String username) throws Exception {
		logger.info(" Inside qm-unaddlist-to-addlist " + username);
		return service.dwpUnAddListToAddList(selectedSections, username);
	}
	
	@PostMapping(value = "/update-chapter-desc", produces = "application/json")
	public ResponseEntity<String> updateChapterDescById(@RequestHeader String username, @RequestBody CheckListMasterDto checkListMasterDto) throws Exception {
		try {
			logger.info( " update-chapter-desc" );
			Integer result=service.updateChapterDescById(checkListMasterDto,username);
			 if(result > 0) {
				 return new ResponseEntity<String>("Successfully" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("Unsuccessful" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error("update-chapter-desc"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/delete-chapter-desc", produces = "application/json")
	public ResponseEntity<String> deleteChapterDescById(@RequestHeader String username, @RequestBody String mocId) throws Exception {
		try {
			logger.info( " delete-chapter-desc" );
			Integer result=service.deleteChapterDescById(mocId,username);
			 if(result > 0) {
				 return new ResponseEntity<String>("S" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("U" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error("delete-chapter-desc"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/delete-sub-chapter-desc", produces = "application/json")
	public ResponseEntity<String> deleteSubChapterDescById(@RequestHeader String username, @RequestBody String mocId) throws Exception {
		try {
			logger.info( " delete-sub-chapter-desc" );
			Integer result=service.deleteSubChapterDescById(mocId,username);
			 if(result > 0) {
				 return new ResponseEntity<String>("S" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("U" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error("delete-sub-chapter-desc"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/add-new-chapter", produces = "application/json")
	public ResponseEntity<String> addNewChapter(@RequestHeader String username, @RequestBody CheckListMasterDto checkListMasterDto) throws Exception {
		try {
			logger.info( " add-new-chapter" );
			Long result=service.addNewChapter(checkListMasterDto,username);
			 if(result > 0) {
				 return new ResponseEntity<String>("Successfully" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("Unsuccessful" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error("add-new-chapter"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/add-chapter-to-master", produces = "application/json")
	public ResponseEntity<String> addChapterToMasters(@RequestHeader String username, @RequestBody List<String> mocIds) throws Exception {
		try {
			logger.info( " add-chapter-to-master" );
			Integer result=service.addChapterToMasters(mocIds,username);
			 if(result > 0) {
				 return new ResponseEntity<String>("Successfully" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("Unsuccessful" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error("add-chapter-to-master"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/update-check-list-chapters", produces = "application/json")
	public ResponseEntity<String> updateCheckListChapters(@RequestHeader String username, @RequestBody List<Long> mocIds) throws Exception {
		try {
			logger.info( " update-check-list-chapters" );
			Integer result=service.updateCheckListChapters(mocIds,username);
			 if(result > 0) {
				 return new ResponseEntity<String>("Successfully" , HttpStatus.OK);
			 }else {
				 return new ResponseEntity<String>("Unsuccessful" , HttpStatus.BAD_REQUEST);
			 }
		} catch (Exception e) {
			 logger.error("update-check-list-chapters"+ e.getMessage());
			 e.printStackTrace();
			 return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
		}
	}
	@PostMapping(value = "/get-dwp-revision-record", produces = "application/json")
	public DwpRevisionRecord getDwpVersionRecordById(@RequestBody Long revisionRecordId, @RequestHeader String username) throws Exception {
		logger.info(" Inside get-dwp-revision-record " + username);
		return service.getDwpRevisionRecord(revisionRecordId);
	}
	
	@PostMapping(value = "update-dwp-notreq-abbreviation/{revisionRecordId}")
	public long updateDwpNotReqQmAbbreviationIds(@PathVariable("revisionRecordId") Long revisionRecordId, @RequestBody String abbreviationIds, @RequestHeader String username ) throws Exception {
		logger.info(" Inside update-dwp-notreq-abbreviation " + username);
		return service.updateNotReqDwpAbbreviationIds(revisionRecordId, abbreviationIds, username);
	}
	
	@PostMapping(value = "/add-dwp-docsummary", produces = "application/json")
	public long addDwpDocSummary(@RequestBody DwpGwpDocumentSummary dwpGwpDocumentSummary, @RequestHeader String username) throws Exception {
		logger.info(" Inside add-dwp-docsummary " + username);
		return service.addDwpDocSummary(dwpGwpDocumentSummary, username);
	}
	
	@PostMapping(value = "/get-dwp-docsummary-byid", produces = "application/json")
	public DwpGwpDocumentSummary getDwpDocSummarybyRevisionRecordId(@RequestBody long revisionRecordId, @RequestHeader  String username) throws Exception {
		logger.info(" Inside get-docsummary " + username);
		return service.getDwpDocSummarybyRevisionRecordId(revisionRecordId);
	}
	
}
