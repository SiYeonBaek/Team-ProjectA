package org.tourGo.controller.community.review;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tourGo.common.JsonResult;
import org.tourGo.controller.RestExceptionController;
import org.tourGo.models.community.review.ReviewEntityRepository;
import org.tourGo.models.file.FileInfo;
import org.tourGo.service.community.ReviewService;
import org.tourGo.services.file.FileRUDService;

@Controller
@RequestMapping("/community")
public class ReviewReadController {

	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ReviewEntityRepository repository;
	@Autowired
	private FileRUDService fileService;
	@Autowired
	private HttpSession session;
	@Autowired
	private HttpServletResponse response;


//	String[] regionLists = {"광주", "대구", "대전", "부산", "서울", "울산", "인천", 
//							"강원도", "경기도", "경상도", "전라도", "제주도", "충청도"};
//	String[] periodLists = {"당일치기", "1박2일", "2박3일", "3박4일", "4박5일", "5박6일 이상"};
	
	private String baseUrl = "community/review/";
	
	//static & board명 추가
	private void addCssJs(String boardName, String[] cssList, String[] jsList, Model model) {
		model.addAttribute("board", boardName);
		model.addAttribute("addCss", cssList);
		model.addAttribute("addScript", jsList);
	}
	
	//제목 클릭시 후기읽기 페이지
	@GetMapping("/review_read/reviewNo_{reviewNo}")
	public String readReview(@PathVariable Long reviewNo, String keyword, String order,
							@CookieValue(value="visitReview", required=false) Cookie cookie , Model model) throws Exception{
		
		//css, js, board 추가
		addCssJs("review", new String[] {"community/community_common"}, 
				new String[] {"community/community_common", "ckeditor/ckeditor",
								"community/review/read", "community/reply"}, model);
		

		/** 쿠키 처리 S */
		if(cookie!=null) {
			if(!cookie.getValue().contains("["+reviewNo+"]")) {
				cookie.setValue(cookie.getValue()+"_["+reviewNo+"]");
				response.addCookie(cookie);
				repository.updateReviewRead(reviewNo);
			}
		}else {
			Cookie newCookie = new Cookie("visitReview", "["+reviewNo+"]");
			response.addCookie(newCookie);
			repository.updateReviewRead(reviewNo);
		}
		/** 쿠키 처리 E */
		
		//게시글 가져오기
		ReviewRequest reviewRequest = reviewService.getOneReviewList(reviewNo);
//		List<FileInfo> fileLists = fileService.getFileLists(reviewRequest.getGid());
		model.addAttribute("reviewRequest", reviewRequest);		
//		model.addAttribute("fileLists", fileLists);
		
		//댓글
		ReplyRequest replyRequest = new ReplyRequest();
		model.addAttribute("replyRequest", replyRequest);
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("order", order);
		
		//나중에 지우기!!!
		session.setAttribute("user", "user01");
		return baseUrl + "review_read";
	}		
}
