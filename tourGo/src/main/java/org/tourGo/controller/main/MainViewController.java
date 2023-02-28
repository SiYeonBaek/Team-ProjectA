package org.tourGo.controller.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.tourGo.config.auth.PrincipalDetail;
import org.tourGo.models.destination.entity.DestinationDetail;
import org.tourGo.models.entity.user.User;
import org.tourGo.models.plan.entity.Planner;
import org.tourGo.models.user.UserRepository;
import org.tourGo.service.destination.DestinationMainService;
import org.tourGo.service.plan.PlannerService;

@Controller
public class MainViewController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PlannerService plannerService;
	
	@Autowired
	private DestinationMainService destinationMainService;


	@GetMapping("/main_view")
	public String main_view3(Model model, @AuthenticationPrincipal PrincipalDetail principal) {
		Optional<User> _user = null;

		List<Planner> top3 = new ArrayList<>();
		try {
			top3 = plannerService.topLikedPlanner();
		} catch (Exception e) {
			top3 = null;
		}
		model.addAttribute("plannerTop3List", top3);
		
		List<DestinationDetail> top3_2 = new ArrayList<>();
		try {
			top3_2 = destinationMainService.topHitsDestination();
		} catch (Exception e) {
			top3_2 = null;
		}
		model.addAttribute("destTop3List",top3_2);

		try {
			_user = userRepository.findByUserId(principal.getUser().getUserId());
			User user = _user.orElse(null);
			model.addAttribute("user", user);
			System.out.println("안녕하세요" + user);

			model.addAttribute("addScript", "layer");
			return "main/main_view";

		} catch (Exception e) {
			return "main/main_view";
		}

	}

}
