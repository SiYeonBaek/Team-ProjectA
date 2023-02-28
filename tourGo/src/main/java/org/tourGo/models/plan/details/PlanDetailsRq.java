package org.tourGo.models.plan.details;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;
import org.tourGo.models.plan.PlannerRq;
import org.tourGo.models.plan.entity.PlanDetails;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlanDetailsRq {//여행 세부일정을 담당하는 dto
		
		private Long detailsNo; // pk
	    private Long plannerNo; // 관계매핑용
	    @DateTimeFormat(pattern="HH : mm")
	    private LocalTime stime; // 관광지 시작시간
	    @DateTimeFormat(pattern="HH : mm")
	    private LocalTime etime;//관광지 종료시간
	    private String title;//관광지이름
	    private String address; //관광지 주소
	    private Integer day; // day1,day2...등 일자 표시용
	    private String firstimage;//관광지 이미지
		private Double mapx;
		private Double mapy;
		private String tel;
	    
	    
	 
	    
}
