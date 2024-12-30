package com.vts.ims.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
	private Long notificationId;          
	private String empName; 
	private String empDesig;
	private String notificationMessage; 
	private String notificationUrl; 
}
