package com.vts.ims.qms.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vts.ims.qms.model.QmsQmMappingOfClasses;

import jakarta.transaction.Transactional;
@Transactional
public interface QmsQmMappingOfClassesRepo extends JpaRepository<QmsQmMappingOfClasses, Long> {
	

	void deleteByRevisionRecordId(Long revisionRecordId);
	
	@Query(value = "SELECT t1.SectionNo, t1.ClauseNo, t1.Description " +
            "FROM ims_qms_qm_mapping_classes t1 " +
            "WHERE t1.revisionRecordId = :revisionRecordId", nativeQuery = true)
	List<Object[]> findAllByRevisionRecordId(@Param("revisionRecordId") Long revisionRecordId);
	
	@Modifying
	@Query(value = "UPDATE ims_qms_qm_mapping_classes SET MocDescription = :MocDescription,ModifiedBy = :ModifiedBy,ModifiedDate = :ModifiedDate WHERE MocId = :MocId",nativeQuery = true)
	public Integer updateMoc(@Param("MocId") Long mocId,@Param("MocDescription") String mocDescription,@Param("ModifiedBy") String username,@Param("ModifiedDate") LocalDateTime ModifiedDate);
	
	@Modifying
	@Query(value = "UPDATE ims_qms_qm_mapping_classes SET MocParentId = 0,ModifiedBy = :ModifiedBy,ModifiedDate = :ModifiedDate WHERE MocId = :MocId",nativeQuery = true)
	public Integer deleteMoc(@Param("MocId") String mocId,@Param("ModifiedBy") String username,@Param("ModifiedDate") LocalDateTime ModifiedDate);
	
	@Modifying
	@Query(value = "UPDATE ims_qms_qm_mapping_classes SET IsActive = 0,ModifiedBy = :ModifiedBy,ModifiedDate = :ModifiedDate WHERE MocId = :MocId",nativeQuery = true)
	public Integer deleteSubChapter(@Param("MocId") String mocId,@Param("ModifiedBy") String username,@Param("ModifiedDate") LocalDateTime ModifiedDate);

	@Modifying
	@Query(value = "UPDATE ims_qms_qm_mapping_classes SET MocParentId = :SectionNo,ModifiedBy = :ModifiedBy,ModifiedDate = :ModifiedDate WHERE MocId = :MocId",nativeQuery = true)
	public Integer addToCheckListMaster(@Param("MocId")String id,@Param("SectionNo")String sectionNo, @Param("ModifiedBy")String username, @Param("ModifiedDate")LocalDateTime now);
	
	@Modifying
	@Query(value = "UPDATE ims_qms_qm_mapping_classes SET IsForCheckList = 'Y',ModifiedBy = :ModifiedBy,ModifiedDate = :ModifiedDate WHERE MocId = :MocId",nativeQuery = true)
	public Integer updateCheckListChapters(@Param("MocId")Long id,@Param("ModifiedBy")String username, @Param("ModifiedDate")LocalDateTime now);
	
	@Modifying
	@Query(value = "UPDATE ims_qms_qm_mapping_classes SET IsForCheckList = 'N'",nativeQuery = true)
	public Integer deleteCheckListChapters();
	
}
