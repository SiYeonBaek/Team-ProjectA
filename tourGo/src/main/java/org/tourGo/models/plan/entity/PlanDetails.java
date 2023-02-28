package org.tourGo.models.plan.entity;

import java.time.LocalTime;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.tourGo.common.BaseEntity;
import org.tourGo.models.plan.details.PlanDetailsRq;

import lombok.*;


@Entity
@Table(name="planDetails")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlanDetails extends BaseEntity {


	@Id @GeneratedValue
	private Long detailsNo; // pk
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="plannerNo")
    private Planner plannerNo; // 관계매핑용
	@DateTimeFormat(pattern="HH:mm")
    private LocalTime stime; // 관광지 시작시간
    @DateTimeFormat(pattern="HH:mm")
    private LocalTime etime;//관광지 종료시간
    private String title;//관광지이름
    private String address; //관광지 주소
	@Column(nullable=true)
    private Integer day; // day1,day2...등 일자 표시용
    private String firstimage;//관광지 이미지
	private Double mapx;
	private Double mapy;
	private String tel;//전화번호
}

