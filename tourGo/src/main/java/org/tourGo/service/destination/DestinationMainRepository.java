package org.tourGo.service.destination;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.tourGo.models.destination.entity.DestinationDetail;
import org.tourGo.models.plan.entity.Planner;

public interface DestinationMainRepository extends JpaRepository<DestinationDetail, Long>, QuerydslPredicateExecutor {

	// 조회수 top3
	List<DestinationDetail> findTop3By(Sort sort);
	
}
