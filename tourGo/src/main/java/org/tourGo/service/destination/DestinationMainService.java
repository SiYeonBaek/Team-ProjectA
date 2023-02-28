package org.tourGo.service.destination;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tourGo.models.destination.entity.DestinationDetail;
import org.tourGo.models.destination.entity.QDestinationDetail;
import org.tourGo.models.entity.user.User;
import org.tourGo.models.plan.entity.Planner;
import org.tourGo.models.plan.entity.QPlanner;

import com.querydsl.core.BooleanBuilder;

@Service
public class DestinationMainService {

	@Autowired
	private DestinationMainRepository destinationMainRepository;
	
	
	// DB에서 값 가져 오기
	public Page<DestinationDetail> dest_mainList(String destination, Pageable pageable) {
		
		BooleanBuilder builder = new BooleanBuilder();
		QDestinationDetail destinationdetail = QDestinationDetail.destinationDetail;
		
		if(!destination.equals("전체")) {
			builder.and(destinationdetail.tourDestination.eq(destination));
		}
		
		//List<DestinationDetail> list = (List<DestinationDetail>) destinationMainRepository.findAll(builder);
		
		Page<DestinationDetail> page = (Page<DestinationDetail>) destinationMainRepository.findAll(builder, pageable);
		System.out.println("Pageable 페이징"+ page);
		System.out.println(page.getSize());
		
		return page;
	}
	
	// 여행지 검색
	public Page<DestinationDetail> dest_search(String searchKeyword, Pageable pageable) {
		
		BooleanBuilder builder = new BooleanBuilder();
		QDestinationDetail destinationDetail = QDestinationDetail.destinationDetail;
		
		builder.and(destinationDetail.tourTitle.contains(searchKeyword));
		
		Page<DestinationDetail> search = destinationMainRepository.findAll(builder, pageable);
		
		return search;
	}
	
	// 조회수 탑3 추출
	public List<DestinationDetail> topHitsDestination() {
		Sort sort = Sort.by(Sort.Order.desc("tourHits"),Sort.Order.asc("destinationNo"));
		List<DestinationDetail> top = destinationMainRepository.findTop3By(sort);
		return top;
	}



	
}
