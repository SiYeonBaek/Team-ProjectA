package org.tourGo.controller.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tourGo.common.JsonResult;
import org.tourGo.models.plan.details.DetailsItems;
import org.tourGo.models.plan.details.PlanDetailsRq;
import org.tourGo.models.plan.entity.PlanDetails;
import org.tourGo.models.plan.entity.Planner;
import org.tourGo.service.plan.PlanDetailsService;
import org.tourGo.service.plan.PlannerService;

@Controller
public class DetailsController {
	
	@Autowired
	PlanDetailsService detailsService;
	@Autowired
	PlannerService plannerService;
	
	@PostMapping("select")
	public String selectDay(Model model,DetailsItems rqList){
	Integer day = rqList.getDay();
	
	
	Planner planner = plannerService.getPlanner(rqList.getPlannerNo());
	if(rqList.getDetailsNo()!=null) {
	detailsService.updatePlanDetails(rqList);
	}
	
	List<PlanDetailsRq> list = detailsService.getPlanDetailsByDay(day, planner);

	
	
	
	model.addAttribute("list", list);
	return "plan/makeDetails::#selected_items";
	
	}
	
	
	@PostMapping("saveDetails")
	public String testxml(PlanDetailsRq rq,Model model){
	
		Planner planner = plannerService.getPlanner(rq.getPlannerNo());
		Integer day = rq.getDay();
		PlanDetails entity = detailsService.insertPlanDetails(rq,planner);

		List<PlanDetailsRq> list = detailsService.getPlanDetailsByDay(day, planner);
	
		
		model.addAttribute("list", list);
		
		return "plan/makeDetails::#selected_items";
	}
	
	@GetMapping("delteDetails")
	public String delete(Long detailsNo,Model model) {
		System.out.println(detailsNo);
		
		PlanDetails entity =  detailsService.deleteDetails(detailsNo);
		
		List<PlanDetailsRq> list = detailsService.getPlanDetailsByDay(entity.getDay(), entity.getPlannerNo());
	
	
		model.addAttribute("list", list);
		
		return "plan/makeDetails::#selected_items";
	}
	
	
}