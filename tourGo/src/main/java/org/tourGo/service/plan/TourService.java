package org.tourGo.service.plan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tourGo.common.HttpRequest;
import org.tourGo.common.JsonResult;
import org.tourGo.models.destination.entity.DestinationDetail;
import org.tourGo.models.entity.user.User;

import org.tourGo.models.plan.tourList.TourListDto;
import org.tourGo.service.destination.DestinationDetailService;

@Service
public class TourService {
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private DestinationDetailService destinationDetailService;
	
	/**
	 * 
	  https://apis.data.go.kr/B551011/KorService/searchKeyword
	  ?serviceKey=ORm3mQovRB97uz6GfTJJNBR%2F2egRn2vglLUfbXCP%2B2pblHvBggwbP1wMwnl%2FRvFZfHqob4GRBHbQDNn6IZP%2FFg%3D%3D
	  &numOfRows=10
	  &pageNo=1
	  &MobileOS=ETC
	  &MobileApp=tourGo
	  &_type=json
	  &listYN=Y
	  &arrange=C
	  &keyword=
	 * */

	 private final String BASE_URL = "https://apis.data.go.kr/B551011/KorService/searchKeyword";
	 private final String serviceKey = "ORm3mQovRB97uz6GfTJJNBR%2F2egRn2vglLUfbXCP%2B2pblHvBggwbP1wMwnl%2FRvFZfHqob4GRBHbQDNn6IZP%2FFg%3D%3D";
	 private final String numOfRows = "&numOfRows=10";
	 private final String pageNo= "&pageNo=1";
	 private final String defaultQueryParam = "&MobileOS=ETC&MobileApp=tourGo&_type=json";
	 private final String listYN = "&listYN=Y";
	 private final String arrange = "&arrange=C";
	 private String _keyword = "&keyword=";	 

	 public List<TourListDto> loadApi(String keyword) throws Exception{
		 
		 StringBuilder urlBuilder = new StringBuilder(BASE_URL);
		 urlBuilder.append("?serviceKey=").append(serviceKey);
		 urlBuilder.append("&").append("numOfRows=").append("30");
		 urlBuilder.append("&").append("pageNo=").append("1");
		 urlBuilder.append("&").append("MobileOS=").append("ETC");
		 urlBuilder.append("&").append("MobileApp=").append("tourGo");
		 urlBuilder.append("&").append("_type=").append("json");
		 urlBuilder.append("&").append("listYN=").append("Y");
		 urlBuilder.append("&").append("arrange=").append("C");
		 urlBuilder.append("&").append("keyword=").append(URLEncoder.encode(keyword,"UTF-8"));
		 
		 String makeUrl = urlBuilder.toString();
	
		
		 URI uri = new URI(makeUrl);
		 RestTemplate restTemplate = this.makeRestTemplate();
		  String jsonString = restTemplate.getForObject(uri,String.class);
		  
			
		 JSONParser jsonParser = new JSONParser();
		 JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
		  // 가장 큰 JSON 객체 response 가져오기
		    JSONObject jsonResponse = (JSONObject) jsonObject.get("response");
		    // 그 다음 body 부분 파싱
		    JSONObject jsonBody = (JSONObject) jsonResponse.get("body");

		    // 그 다음 위치 정보를 배열로 담은 items 파싱
		    JSONObject jsonItems = (JSONObject) jsonBody.get("items");

		    // items는 JSON임, 이제 그걸 또 배열로 가져온다
		    JSONArray jsonItemList = (JSONArray) jsonItems.get("item");
		    System.out.println(jsonItemList);
		    List<TourListDto> result = new ArrayList<>();
		    
		    for (Object o : jsonItemList) {
		        JSONObject item = (JSONObject) o;
		        result.add(makeLocationDto(item));
		    }
		   
		 return result;
	 }
	 //restTemplate ssl인증 무시하는 메서드
	 private RestTemplate makeRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
			
	        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

	        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	        		.loadTrustMaterial(null, acceptingTrustStrategy)
	        		.build();
	        
	        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
	        
	        CloseableHttpClient httpClient = HttpClients.custom()
	        		.setSSLSocketFactory(csf)
	        		.build();
	        
	        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	        requestFactory.setHttpClient(httpClient);
	        
	        requestFactory.setConnectTimeout(3 * 1000);    
	        
	        requestFactory.setReadTimeout(3 * 1000);
	        
	        return new RestTemplate(requestFactory);
	    }

	
	
	private TourListDto makeLocationDto(JSONObject item) {
		
		TourListDto dto = TourListDto.builder()
				.title((String) item.get("title"))
				.address((String)item.get("addr1"))
				.firstimage((String)item.get("firstimage"))
				.mapx((String)item.get("mapx"))
				.mapy((String)item.get("mapy"))
				.tel((String)item.get("tel"))
				.build();
		
		return dto;
	}
	
	
	
	/**1.현재 플래너 넘버를 추출하고 거기서 유저넘버를 추출한다
	   2.유저 넘버로 유저 객체를 생성
	   3.관광지에 유저 넘버랑 유저 객체에 넘버를 비교한다
	   4.일치할경우 관광지에 넘버를 추출해서 관광지 객체를 
	   			tourTitle
	   		   tourImg
	   		   mapx
	   		   mapy
	   		   tourAddr
	   		   tourNumber
	 * */
	public List<TourListDto> getLikedList(User user){
		List<DestinationDetail> destinationList = destinationDetailService.getDetailListByUserNo(user.getUserNo());
		
		List<TourListDto> list = new ArrayList<>();
		for(DestinationDetail detail: destinationList) {
			TourListDto dto = TourListDto.builder()
					.title(detail.getTourTitle())
					.address(detail.getTourAddr())
					.firstimage(detail.getTourImg())
					.mapx(Double.toString(detail.getMapX()))
					.mapy(Double.toString(detail.getMapY()))
					.tel(detail.getTourNumber())
					.build();
			list.add(dto);
		}
		
		return list;
	}
	
	
	
	

	
	
	
	
}
