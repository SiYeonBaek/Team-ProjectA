package org.tourGo.service.community.query;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tourGo.common.AlertException;
import org.tourGo.controller.community.query.QueryRequest;
import org.tourGo.models.community.query.QueryEntityRepository;
import org.tourGo.models.entity.community.query.QueryEntity;
import org.tourGo.models.entity.user.User;
import org.tourGo.models.user.UserRepository;

@Service
public class QuerySaveService {

	@Autowired
	private QueryEntityRepository queryRepository;
	@Autowired
	private UserRepository userRepository;
	
	//등록과 수정을 같이 처리하는 서비스
	public QueryRequest process(QueryRequest queryRequest) {
		//게시글 번호가 있다면 수정, 없다면 등록
		Long queryNo = queryRequest.getQueryNo();
		QueryEntity queryEntity = null;
		User user = userRepository.findByUserId(queryRequest.getUser().getUserId()).orElse(null);
		
		if(queryNo != null) {//수정
			queryEntity = queryRepository.findById(queryNo)
				.orElseThrow(() -> new AlertException("게시글이 존재하지 않습니다", "/community/query_main"));
		}
		
		if(queryEntity==null) {//등록
			queryEntity = new QueryEntity();
			if(user != null){
				queryEntity.setUser(user);
			}
		}

		queryEntity.setQueryContent(queryRequest.getQueryContent());
		queryEntity.setQueryTitle(queryRequest.getQueryTitle());
		queryEntity.setSecretPost(queryRequest.isSecretPost());
		
		queryEntity = queryRepository.save(queryEntity);
		ModelMapper mapper = new ModelMapper();
		queryRequest = mapper.map(queryEntity, QueryRequest.class);
		
		return queryRequest;
	}
}