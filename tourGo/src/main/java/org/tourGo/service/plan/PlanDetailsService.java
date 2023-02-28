package org.tourGo.service.plan;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tourGo.common.AlertException;
import org.tourGo.models.entity.user.User;
import org.tourGo.models.plan.details.DetailsItems;
import org.tourGo.models.plan.details.PlanDetailsRq;
import org.tourGo.models.plan.entity.PlanDetails;
import org.tourGo.models.plan.entity.Planner;
import org.tourGo.models.plan.entity.QPlanDetails;

import com.querydsl.core.BooleanBuilder;

@Service
public class PlanDetailsService {

	@Autowired
	private PlanDetailsRepository detailsRepo;
		

		
	public String getDetailsImage(Planner planner){
		
		List<Order> orders = new ArrayList<>();
		orders.add(Order.asc("day"));
		orders.add(Order.desc("sdate"));
		BooleanBuilder builder = new BooleanBuilder();
		QPlanDetails details = QPlanDetails.planDetails;
		builder.and(details.plannerNo.eq(planner));
		Sort sort = Sort.by(orders);
		
		List<PlanDetails> list = (List<PlanDetails>) detailsRepo.findAll(builder,sort);
		
		PlanDetails _details = list.get(0);
		String image = _details.getFirstimage();
		return image;
	}
	
	public PlanDetails test(Long no) {
		Optional<PlanDetails> _entity = detailsRepo.findById(no);
		PlanDetails entity = _entity.orElse(null);
		return entity;
	}
	

	
	public PlanDetails deleteDetails(Long no) {
		
		Optional<PlanDetails> _details = detailsRepo.findById(no);
		PlanDetails details = _details.orElse(null);
		
		detailsRepo.delete(details);
		
		return details;
		
	}
	public List<PlanDetailsRq> getPlanDetailsRqList(Planner planner){//planner랑 매핑된 entity들 list형태로 반환
		BooleanBuilder builder = new BooleanBuilder();
		QPlanDetails details = QPlanDetails.planDetails;
		builder.and(details.plannerNo.eq(planner));
		
		
		List<PlanDetails> _list = (List<PlanDetails>) detailsRepo.findAll(builder,Sort.by(Sort.Direction.ASC, "day","stime","detailsNo"));
		
		List<PlanDetailsRq> list = PlanDetailsService.toDtoList(_list);
		
		return list;
	}
	public void deleteAllDetailsByPlanner(Planner planner) {
		BooleanBuilder builder = new BooleanBuilder();
		QPlanDetails details = QPlanDetails.planDetails;
		builder.and(details.plannerNo.eq(planner));
		List<PlanDetails> list = (List<PlanDetails>) detailsRepo.findAll(builder);
		
		for(PlanDetails entity : list) {
			detailsRepo.delete(entity);
		}
		
		
	}
	public List<PlanDetailsRq> getPlanDetailsByDay(Integer day,Planner planner){//지정한 날짜에있는 entity들을 찾아서 list로 반환
		BooleanBuilder builder = new BooleanBuilder();
		QPlanDetails details = QPlanDetails.planDetails;
		builder.and(details.plannerNo.eq(planner));
		builder.and(details.day.eq(day));
		
		List<PlanDetails> _list = (List<PlanDetails>) detailsRepo.findAll(builder,Sort.by(Sort.Direction.ASC, "stime","detailsNo"));
		List<PlanDetailsRq> list = PlanDetailsService.toDtoList(_list);
		
		
		return list;
	}
	@Transactional
	public void updatePlanDetails(DetailsItems items) {//관광지 시작시간과 종료시간 업데이트
		try {
			if((!items.getDetailsNo().isEmpty())&&items.getDetailsNo()!=null) {
		
			for(int i=0; i<items.getDetailsNo().size();i++) {//ajax로 받은 detailsItems의 detailsNo만큼 반복
			Optional<PlanDetails> details = detailsRepo.findById(items.getDetailsNo().get(i));
			
			PlanDetails entity = details.orElse(null);
		
			if(entity==null) {
				throw new AlertException("일정을 찾을수없습니다.");
			}
			if(!items.getStime().isEmpty()&&items.getStime()!=null) {
				String _stime = items.getStime().get(i);
				
				if(!_stime.isBlank()) {
					LocalTime stime = LocalTime.parse(_stime,DateTimeFormatter.ofPattern("HH:mm"));
					entity.setStime(stime);
					
				}
			}
		
			if(!items.getEtime().isEmpty()&&items.getEtime()!=null) {
			String _etime = items.getEtime().get(i);
		
			if(!_etime.isBlank()) {
				LocalTime etime = LocalTime.parse(_etime,DateTimeFormatter.ofPattern("HH:mm"));
				entity.setEtime(etime);
			}
			
			}
		}
			}
		}catch(Exception e) {
			e.printStackTrace();
			
			throw new AlertException("세부일정 에러! 다시 시도해주세요.");
			}
	//,"/plan"
	}
	
	public PlanDetails insertPlanDetails(PlanDetailsRq dto,Planner planner){//db에 entity저장
	
		
		PlanDetails entity = PlanDetailsService.toEntity(dto, planner);
		entity= detailsRepo.save(entity);
		

		return entity;
		
	}
	
	
	//toDto toEntity 메서드s
	   public static PlanDetailsRq toDto(PlanDetails entity) {

	    	return PlanDetailsRq.builder()
	    			.detailsNo(entity.getDetailsNo())
	    			.plannerNo(entity.getPlannerNo().getPlannerNo())
	    			.stime(entity.getStime())
	    			.etime(entity.getEtime())
	    			.title(entity.getTitle())
	    			.address(entity.getAddress())
	    			.day(entity.getDay())
	    			.firstimage(entity.getFirstimage())
	    		
	    			.mapx(entity.getMapx())
	    			.mapy(entity.getMapy())
	    			.tel(entity.getTel())
	    			.build();
	    
	    }
	    
	    public static PlanDetails toEntity(PlanDetailsRq planDetails) {

	    	return PlanDetails.builder()
	    			.detailsNo(planDetails.getDetailsNo())
	    			.stime(planDetails.getStime())
	    			.etime(planDetails.getEtime())
	    			.title(planDetails.getTitle())
	    			.address(planDetails.getAddress())
	    			.day(planDetails.getDay())
	    			.firstimage(planDetails.getFirstimage())
	    		
	    			.mapx(planDetails.getMapx())
	    			.mapy(planDetails.getMapy())
	    			.tel(planDetails.getTel())
	    			.build();
	    }
	    public static PlanDetails toEntity(PlanDetailsRq planDetails,Planner planner) {

	    	return PlanDetails.builder()
	    			.detailsNo(planDetails.getDetailsNo())
	    			.plannerNo(planner)
	    			.stime(planDetails.getStime())
	    			.etime(planDetails.getEtime())
	    			.title(planDetails.getTitle())
	    			.address(planDetails.getAddress())
	    			.day(planDetails.getDay())
	    			.firstimage(planDetails.getFirstimage())
	    			
	    			.mapx(planDetails.getMapx())
	    			.mapy(planDetails.getMapy())
	    			.tel(planDetails.getTel())
	    			.build();
	    }
	    
	    public static List<PlanDetails> toEntityList(List<PlanDetailsRq> _list){
	    	List<PlanDetails> list = null;
	    	
	    	for(PlanDetailsRq rq : _list) {
	    		PlanDetails entity = PlanDetailsService.toEntity(rq);
	    		list.add(entity);
	    	}
	    	
	    	
	    	
			return list;
	    	
	    }
	    
	    public static List<PlanDetails> toEntityList(List<PlanDetailsRq> _list,Planner planner){
	    	List<PlanDetails> list = null;
	    	
	    	for(PlanDetailsRq dto : _list) {
	    		
	    		PlanDetails entity = PlanDetailsService.toEntity(dto);
	    		entity.setPlannerNo(planner);
	    		list.add(entity);
	    	}
	    	
	    	
	    	
			return list;
	    	
	    }
	    
	    public static List<PlanDetailsRq> toDtoList(List<PlanDetails> _list){
	    	
	    	List<PlanDetailsRq> list = new ArrayList<>();
	    	
	    	for(PlanDetails entity : _list) {
	    		PlanDetailsRq dto = PlanDetailsService.toDto(entity);
	    		list.add(dto);
	    	}
	    	
	    	return list;
	    }
	    
	  //toDto toEntity 메서드e
	
	
}
