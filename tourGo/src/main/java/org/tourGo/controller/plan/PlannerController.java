package org.tourGo.controller.plan;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tourGo.common.AlertException;
import org.tourGo.config.auth.PrincipalDetail;
import org.tourGo.models.entity.user.User;
import org.tourGo.models.plan.PlannerRq;
import org.tourGo.models.plan.TourType;
import org.tourGo.models.plan.details.DetailsItems;
import org.tourGo.models.plan.details.PlanDetailsRq;
import org.tourGo.models.plan.entity.PlanDetails;
import org.tourGo.models.plan.entity.Planner;
import org.tourGo.models.plan.entity.like.PlanUidEntity;
import org.tourGo.models.plan.entity.like.PlanUidRequest;
import org.tourGo.models.plan.entity.PlanDetails.PlanDetailsBuilder;
import org.tourGo.models.user.UserRepository;
import org.tourGo.service.plan.PlanDetailsRepository;
import org.tourGo.service.plan.PlanDetailsService;
import org.tourGo.service.plan.PlanLikeService;
import org.tourGo.service.plan.PlanReadHitService;
import org.tourGo.service.plan.PlanUidEntityRepository;
import org.tourGo.service.plan.PlannerRepository;
import org.tourGo.service.plan.PlannerService;

import lombok.Value;

@Controller
@RequestMapping("/plan")
public class PlannerController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PlannerService plannerService;

	@Autowired
	private PlanUidEntityRepository planUidEntityRepository;

	@Autowired
	private PlanDetailsService detailsService;

	@Autowired
	private PlanReadHitService planReadHitService;

	@ModelAttribute("planTypes")
	public TourType[] tourType() {
		return TourType.values();
	}

	@GetMapping() // 여행 상세 일정 보는 화면
	public String plan(Model model, @AuthenticationPrincipal PrincipalDetail principal,
			@PageableDefault(page = 0, size = 3, sort = "plannerNo", direction = Sort.Direction.DESC) Pageable pageable,
			String searchKeyword) {

		Optional<User> _user = null;
		try {
			_user = userRepository.findByUserId(principal.getUser().getUserId());
		} catch (Exception e) {
			throw new AlertException("로그인후에 이용가능한 페이지입니다.", "/user/login");
		}
		User user = _user.orElse(null);

		Page<Planner> list = null;

		if (searchKeyword == null) {
			list = plannerService.plannerList(pageable, user);
		} else {
			list = plannerService.plannerSearchList(searchKeyword, pageable, user);
		}

		int nowPage = list.getPageable().getPageNumber() + 1; // pageable 0부터 시작하기때문에 +1 해준다
		int startPage = Math.max(nowPage - 4, 1); // 둘중 큰거 반환함.
		int endPage = Math.min(nowPage + 5, list.getTotalPages());

		model.addAttribute("list", list);
		model.addAttribute("user", user);
		model.addAttribute("nowPage", nowPage);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		
		model.addAttribute("addScript", "layer");
		return "plan/plannerView";
	}

	@GetMapping("/plannerallview")
	public String planallview(Model model,
			@PageableDefault(page = 0, size = 3, sort = "plannerNo", direction = Direction.DESC) Pageable pageable,
			String searchKeyword) {
		
		Page<Planner> list = null;

		if (searchKeyword == null) {
			list = plannerService.plannerList(pageable);
		} else {
			list = plannerService.plannerSearchList(searchKeyword, pageable);
		}
		
		
		

		int nowPage = list.getPageable().getPageNumber() + 1;
		int startPage = Math.max(nowPage - 4, 1);
		int endPage = Math.min(nowPage + 5, list.getTotalPages());

		model.addAttribute("list", list);
		model.addAttribute("nowPage", nowPage);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);

		return "plan/plannerallView";
	}

	// 아이디값확인해서 아이디일치하지않으면 예외처리하기!!
	@GetMapping("/makeDetails/{no}")
	public String makeDetails(Model model, @PathVariable Long no, @AuthenticationPrincipal PrincipalDetail principal) {
		try {
			Optional<User> _user = userRepository.findByUserId(principal.getUser().getUserId());
			User user = _user.orElse(null);

			Planner planner = plannerService.getPlanner(no);

			boolean check = plannerService.checkPlanner(user, planner);
			if (!check) {
				model.addAttribute("scripts", " alert('유효하지않은 접근입니다'); location.replace('/plan');");
				return "common/excution";
			}

			List<PlanDetailsRq> list = detailsService.getPlanDetailsByDay(1, planner);

			
			PlannerRq plannerRq = plannerService.toDto(planner);
			ArrayList<String> dayList = new ArrayList<>();
			for (int i = 1; i <= plannerRq.getDay(); i++) {
				String d = "DAY" + i;
				dayList.add(d);
			}
			model.addAttribute("dayList", dayList);
			model.addAttribute("list", list);
			model.addAttribute("plannerRq", plannerRq);
			model.addAttribute("plannerNo", planner.getPlannerNo());
	
			 model.addAttribute("addCss", new String[] {"plan/makePlan"});
			 model.addAttribute("addScript", new String[] {"plan/makePlan/details"}); 
			  
			
			
		} catch (Exception e) {
			throw new AlertException("유효하지 않은 접근입니다.", "/plan");
		}

		return "plan/makeDetails";
	}

	@PostMapping("/{plannerNo}")
	public String makeDetailsPs(@PathVariable Long plannerNo,DetailsItems items, Model model) {
	
		Planner planner = plannerService.getPlanner(plannerNo);
	
		plannerService.updateImage(planner);
		if(items.getDetailsNo()!=null) {
		
		detailsService.updatePlanDetails(items);
		}
		model.addAttribute("scripts", " alert('저장되었습니다!'); location.replace('/plan');");
		return "common/excution";
	}

	@GetMapping("/makeplan2")
	public String makePlan(Model model) {
		PlannerRq plannerRq = new PlannerRq();
		model.addAttribute("plannerRq", plannerRq);
		return "plan/makePlan";
	}

	@PostMapping("/makeDetails")
	public String makePlanPs(@Valid PlannerRq plannerRq, Errors errors, Model model,
			@AuthenticationPrincipal PrincipalDetail principal) {

		

		if (errors.hasErrors()) {
			model.addAttribute("plannerRq", plannerRq);
			return "plan/makePlan";
		}

		Optional<User> _user = userRepository.findByUserId(principal.getUser().getUserId());
		User user = _user.orElse(null);

		Planner planner = plannerService.insertPlanner(plannerRq, user);

		model.addAttribute("scripts", "parent.location.replace('../plan/makeDetails/" + planner.getPlannerNo() + "');");
		model.addAttribute("plannerRq", plannerRq);

		return "common/excution";

	}

	@GetMapping("/readplan/{no}")
	public String read(Model model, @PathVariable Long no) {
		Planner planner = plannerService.getPlanner(no);
		PlannerRq plannerRq = PlannerService.toDto(planner); // 플래너번호를 받아 dto객체로 변환
		
		List<PlanDetailsRq> list = detailsService.getPlanDetailsRqList(planner);
		model.addAttribute("planner", plannerRq);
		model.addAttribute("addScript", "layer");
		model.addAttribute("list",list);
		
		
		
		
		return "plan/read";
	}

	@GetMapping("/writeplan/{no}")

	public String write(Model model, @PathVariable Long no) {

		PlannerRq plannerRq = PlannerService.toDto(plannerService.getPlanner(no));

		model.addAttribute("planner", plannerRq);

		model.addAttribute("addScript", "layer");
		return "plan/write";

	}

	@PostMapping("/readplan/{no}")
	public String writeps(@Valid PlannerRq plannerRq, @PathVariable Long no, Model model,
			@AuthenticationPrincipal PrincipalDetail principal) {
		Optional<User> _user = null;
		_user = userRepository.findByUserId(principal.getUser().getUserId());

		User user = _user.orElse(null);
		plannerRq.setPlannerNo(no);
		Planner planner = plannerService.updatePlanner(plannerRq);
		model.addAttribute("scripts", "location.replace('/plan/readplan/" + planner.getPlannerNo() + "');");

		return "common/excution";

	}

	@GetMapping("/deleteplan/{no}")
	public String deletePs(Model model, @PathVariable Long no) {

		Planner planner = plannerService.getPlanner(no);
		detailsService.deleteAllDetailsByPlanner(planner);
		
		plannerService.deletePlanner(planner);

		model.addAttribute("scripts", " alert('처리가 완료되었습니다'); parent.location.replace('/plan');");
		return "common/excution";
	}

	@GetMapping("/plannerallview_page/{no}")
	@Transactional
	public String planallview_page(Model model, @AuthenticationPrincipal PrincipalDetail principal,
			@PathVariable Long no) {
		Planner planner = plannerService.getPlanner(no);
		List<PlanDetailsRq> list = detailsService.getPlanDetailsRqList(planner);
		try {

			planner = plannerService.getPlanner(no);

			boolean check = plannerService.checkPlannerNo(planner);
			if (!check) {
				model.addAttribute("scripts", " alert('유효하지않은 접근입니다'); location.replace('/plan/plannerallview');");
				return "common/excution";
			}

		Long userNo = null;

		if (principal != null) {
			model.addAttribute("user", principal.getUsername());
			userNo = principal.getUser().getUserNo();

			PlanUidEntity planUidEntity = planUidEntityRepository.findByNo("liked", no, userNo).orElse(null);
			if (planUidEntity != null) {
				PlanUidRequest like = new PlanUidRequest(planUidEntity);
			
				model.addAttribute("like", like);
			}
		}

		String readUid = PlanUidRequest.getUid(no, userNo);
		planReadHitService.process(readUid, "readHit", "plan");

		model.addAttribute("planner", planner);
		model.addAttribute("list", list);
		
		} catch (Exception e) {
			throw new AlertException("유효하지 않은 접근입니다.", "/plan");
		}
		
		
		return "plan/plannerallView_page";
	}

	@GetMapping("/weather")
	public String weather() {

		return "plan/weather";
	}

	@GetMapping("/makeDetails_move/{no}")
	public String makeDetails2(Model model, @PathVariable Long no) {

		model.addAttribute("scripts", "parent.location.replace('../makeDetails/" + no + "');");
		return "common/excution";
	}
	
	
	
	@GetMapping("/go_plannerview_private/{no}")
	public String go_plannerview_private(Model model, @PathVariable Long no) {

		model.addAttribute("scripts", "parent.location.replace('/plan/plannerview_private/" + no + "');");
		return "common/excution";
	}
	
	@GetMapping("/plannerview_private/{no}")
	public String planneview_private(Model model, @AuthenticationPrincipal PrincipalDetail principal,
			@PathVariable Long no) {
		Planner planner = plannerService.getPlanner(no);
		List<PlanDetailsRq> list = detailsService.getPlanDetailsRqList(planner);
		try {

			Optional<User> _user = userRepository.findByUserId(principal.getUser().getUserId());
			User user = _user.orElse(null);

			boolean check = plannerService.checkPlanner(user,planner);
			if (!check) {
				model.addAttribute("scripts", " alert('유효하지않은 접근입니다'); location.replace('/plan');");
				return "common/excution";
			}

		Long userNo = null;

		if (principal != null) {
			model.addAttribute("user", principal.getUsername());
			userNo = principal.getUser().getUserNo();

			PlanUidEntity planUidEntity = planUidEntityRepository.findByNo("liked", no, userNo).orElse(null);
			if (planUidEntity != null) {
				PlanUidRequest like = new PlanUidRequest(planUidEntity);
			
				model.addAttribute("like", like);
			}
		}
				String readUid = PlanUidRequest.getUid(no, userNo);
				planReadHitService.process(readUid, "readHit", "plan");

				model.addAttribute("planner", planner);
				model.addAttribute("list", list);
				
		} catch (Exception e) {
			throw new AlertException("유효하지 않은 접근입니다.", "/plan");
		}
		return "plan/plannerview_private";
}
}
	