package org.tourGo.service.plan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.tourGo.common.AlertException;
import org.tourGo.models.destination.entity.DestinationDetail;
import org.tourGo.models.entity.user.User;
import org.tourGo.models.plan.PlannerRq;
import org.tourGo.models.plan.entity.PlanDetails;
import org.tourGo.models.plan.entity.Planner;
import org.tourGo.models.plan.entity.QPlanDetails;
import org.tourGo.models.plan.entity.QPlanner;
import org.tourGo.models.plan.entity.like.PlanUidEntity;
import org.tourGo.models.plan.tourList.TourListDto;
import org.tourGo.models.user.UserRepository;
import org.tourGo.service.destination.DestinationDetailService;

import com.querydsl.core.BooleanBuilder;



@Service
public class PlannerService {

	@Autowired
	private PlannerRepository plannerRepo; // 여기서 선언해서 그런건가요?
	@Autowired
	private PlanDetailsRepository detailsRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private PlanUidEntityRepository uidRepo;
	

	
	//plannerNo로 planner를 찾고 planner.getUser를 통해 user반환
	public User getUserByPlannerNo(Long no) {
		Optional<Planner> _planner = plannerRepo.findById(no);
		Planner planner = _planner.orElse(null);
		
		User user = planner.getUser();
		return user;
	}

	//상세일정을 저장하는 버튼을 누를때 첫번째 관광지의 이미지를 planner 대표 이미지로 설정하는 메서드
	@Transactional
	public void updateImage(Planner planner) {
		BooleanBuilder builder = new BooleanBuilder();
		QPlanDetails details = QPlanDetails.planDetails;
		builder.and(details.plannerNo.eq(planner));
		String image = "";
		
		List<PlanDetails> list = (List<PlanDetails>) detailsRepo.findAll(builder,Sort.by(Sort.Direction.ASC, "day","stime","detailsNo"));
		if(!list.isEmpty()) {
		 image = list.get(0).getFirstimage();
		planner.setImage(image);
		}else {
			planner.setImage(image);
		}
		
		
	}
	//사용자가 좋아요한 플래너 추출하는 메서드
	public List<Planner> getPlannerLiked(Long userNo){
		
		List<PlanUidEntity> uidEntity = uidRepo.findByUserNo(userNo);
		System.out.println("유아이디");
		System.out.println(userNo);
		System.out.println(uidEntity);
		List<Planner> list = new ArrayList<>();
		for(PlanUidEntity _uid : uidEntity) {
			String uid = _uid.getUid();
			Long plannerNo = Long.parseLong(uid.split("_")[0]);
			System.out.println(plannerNo);
			Optional<Planner> _plannerEntity = plannerRepo.findById(plannerNo);
			Planner plannerEntity = _plannerEntity.orElse(null);
			list.add(plannerEntity);
			
		}
		
		return list;
	}
	//db로부터 플래너 번호를 내림차순하여 plannerRq List형태로 변환하는 메서드
	public List<PlannerRq> userPlanner(User user)	{ 


		List<Planner> list = plannerRepo.findAllByUser(user,Sort.by(Sort.Direction.DESC,"plannerNo"));
		List<PlannerRq> list2 = new ArrayList<>();
		
		for(Planner planner : list) {
		PlannerRq rq =	PlannerService.toDto(planner);
		
		list2.add(rq);
		}
		
		return list2;
	}
	//사용자가 planner를 수정하였을때 업데이트하는 메서드
	@Transactional
	public Planner updatePlanner(PlannerRq request) {
		System.out.println(request);
		Optional<Planner> _planner = plannerRepo.findById(request.getPlannerNo());
		LocalDate sdate = request.getSdate();		
		LocalDate edate = sdate.plusDays(request.getDay());
		Planner planner = _planner.orElse(null);
		planner.setTitle(request.getTitle());
		planner.setPlanSize(request.getPlanSize());
		planner.setPlanType(request.getPlanType());
		planner.setSdate(sdate);
		planner.setEdate(edate);
		planner.setMemo(request.getMemo());
		planner.setDay(request.getDay());
		planner.setOpen(request.getOpen());
		return planner;

	}
	public void deletePlanner(Planner planner) {
		
		plannerRepo.delete(planner);
		
	}
	

	
	public Page<Planner> plannerList(Pageable pageable,User user){
		BooleanBuilder builder = new BooleanBuilder();
		QPlanner planner = QPlanner.planner;
		builder.and(planner.user.eq(user));
		
		Page<Planner> list = plannerRepo.findAll(builder, pageable);
		
		
		
		return list;
	}
	
	
	public Page<Planner> plannerList(Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		QPlanner planner = QPlanner.planner;
		builder.and(planner.open.eq(true));
		
		Page<Planner> list = plannerRepo.findAll(builder,pageable);
		
		return list;
	}

	//좋아요 탑3 추출
	public List<Planner> topLikedPlanner(){
		Sort sort = Sort.by(Sort.Order.desc("totalLikes"),Sort.Order.asc("plannerNo"));
		List<Planner> list = plannerRepo.findTop3ByOpen(true,sort);
		
		return list;
	}
	
	public Page<Planner> plannerSearchList(String searchKeyword, Pageable pageable,User user) {
		
		BooleanBuilder builder = new BooleanBuilder();
		QPlanner planner = QPlanner.planner;
		builder.and(planner.user.eq(user));
		builder.and(planner.title.contains(searchKeyword));
		Page<Planner> list = plannerRepo.findAll(builder,pageable);
		return list; 
	}
	
	public Page<Planner> plannerSearchList(String searchKeyword, Pageable pageable) {
		
		BooleanBuilder builder = new BooleanBuilder();
		QPlanner planner = QPlanner.planner;
		builder.and(planner.open.eq(true));
		builder.and(planner.title.contains(searchKeyword));
		Page<Planner> list = plannerRepo.findAll(builder,pageable);
		return list; 
	}
	
	
	
	//planner DB에 저장하는 메서드
	public Planner insertPlanner(PlannerRq plannerRq,User user) {
		
		

		Planner planner = PlannerService.toEntity(plannerRq, user);
	
		planner = plannerRepo.save(planner);
		return planner;
		
	}
	//시큐리티 세션에 저장되어있는 유저정보랑 planner엔티티가 갖고있는 유저객체랑 일치하는지 체크
	public boolean checkPlanner(User user,Planner planner) {

		if(planner.getUser() != user) {
			return false;
		}
			
		return true;
	}
	//공개설정 체크여부 (비공개 플래너 접근 불가)
	public boolean checkPlannerNo(Planner planner) {

		if(planner.getOpen() != true) {
			return false;
		}
			
		return true;
	}
	
	//plannerNo를 받아 planner 반환
	public Planner getPlanner(Long no) {
		
		Optional<Planner> _planner = plannerRepo.findById(no);
		Planner planner = _planner.orElse(null);
		return planner;
		
	}
	//toDto toEntity 메서드s
	public static PlannerRq toDto(Planner planner) {
		
		int day = planner.getDay();
		LocalDate sdate = planner.getSdate();
		LocalDate edate = sdate.plusDays(day);
		
		
		PlannerRq rq = PlannerRq.builder().day(day).sdate(sdate).edate(edate).image(planner.getImage()).memo(planner.getMemo()).planSize(planner.getPlanSize())
				.planType(planner.getPlanType()).title(planner.getTitle()).plannerNo(planner.getPlannerNo()).open(planner.getOpen()).build();
		
		return rq;
	}
	
	public static Planner toEntity(PlannerRq rq,User user) {
		int day = rq.getDay();
		LocalDate sdate = rq.getSdate();
		
		LocalDate edate = sdate.plusDays(day);
		Planner planner = Planner.builder().plannerNo(rq.getPlannerNo()).day(day).sdate(sdate).edate(edate).image(rq.getImage()).memo(rq.getMemo()).planSize(rq.getPlanSize())
				.planType(rq.getPlanType()).title(rq.getTitle()).user(user).open(rq.getOpen()).build();
		
		return planner;
		
	}
	
	public static Planner toEntity(PlannerRq rq) {
		int day = rq.getDay();
		LocalDate sdate = rq.getSdate();
		LocalDate edate = sdate.plusDays(day);
		Planner planner = Planner.builder().plannerNo(rq.getPlannerNo()).day(day).sdate(sdate).edate(edate).image(rq.getImage()).memo(rq.getMemo()).planSize(rq.getPlanSize())
				.planType(rq.getPlanType()).title(rq.getTitle()).open(rq.getOpen()).build();
		
		return planner;
		
	}
	//toDto toEntity 메서드e



	
	
	
}
