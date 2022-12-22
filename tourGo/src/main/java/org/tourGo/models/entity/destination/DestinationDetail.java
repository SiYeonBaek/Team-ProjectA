package org.tourGo.models.entity.destination;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.tourGo.models.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DestinationDetail {
	
	@Id 
	@GeneratedValue
	private Long DestinationNo;// db테이블 증감번호

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "userNo")
	private User user;// 관계매핑

	@Column(nullable = true)
	private Long id;		// id
	private String title;	// 관광지 이름
	private String image1;	// 관광지 이미지1
	private String image2;	// 관광지 이미지2
	private Double mapX;	// 관광지 좌표x
	private Double mapY;	// 관광지 좌표y
	private String zipcode;	// 관광지 우편번호
	private String address;	// 관광지 주소
	private int hits;		// 관광지 조회수
	private int bookmark;	// 관광지 좋아요

}
