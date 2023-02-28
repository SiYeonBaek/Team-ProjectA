package org.tourGo.controller.plan;

import java.io.UnsupportedEncodingException;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tourGo.common.AlertException;
import org.tourGo.common.JsonResult;
import org.tourGo.models.entity.user.User;
import org.tourGo.models.plan.entity.Planner;

import org.tourGo.models.plan.tourList.TourListDto;
import org.tourGo.service.plan.PlannerService;
import org.tourGo.service.plan.TourService;


@RestController
public class TourListController {

	@Autowired
	TourService tourService;
	@Autowired PlannerService plannerService;

	
	//api호출하는 컨트롤러
	@GetMapping("/tourList")
	public JsonResult<?> fetch(String keyword){
			List<TourListDto> list = null;
			
				try {
					list = tourService.loadApi(keyword);
				} catch (Exception e) {
					e.printStackTrace();
				throw new AlertException("오류 발생! 담당자에게 문의부탁드립니다.");
				}
			
		    
			return new JsonResult<>(true, "성공", list);
	}
	//사용자가 좋아요누른 관광지 추출하는 컨트롤러
	@GetMapping("/likedList")
	public JsonResult<?> getLikedList(Long plannerNo){
		
		User user = plannerService.getUserByPlannerNo(plannerNo);
		List<TourListDto> list = null;
		try {
			list = tourService.getLikedList(user);
		}catch (Exception e) {
			throw new AlertException("오류 발생! 담당자에게 문의부탁드립니다.");
		}
		
		System.out.println("테스트 찜");
		System.out.println(list);
		
		return new JsonResult<>(true,"성공",list);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
