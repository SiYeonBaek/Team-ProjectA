package org.tourGo.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.tourGo.models.entity.user.User;

// 스프링 시큐리티가 로그인 요청을 가로채서 로그인을 진행하고 완료가 되면 UserDetails타입의 오브젝트를
// 스프링 시큐리티의 고유한 세션 저장소에 저장을 해준다.
public class PrincipalDetail implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	private User user; // 컴포지션
	
	public PrincipalDetail(User user) {
		this.user = user;
	}

	@Override
	public String getPassword() {
		return user.getUserPw();
	}

	@Override
	public String getUsername() {
		return user.getUserId();
	}
	
	// 계정이 만료되지 않았는지 리턴(true: 만료 안됨)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	// 계정이 잠겨있지 않았는지 리턴(true: 잠기지 않음)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 비밀번호가 만료되지 않았는지 리턴(true: 만료안됨)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	// 계정이 활성화 (사용가능) 인지 리턴(true: 활성화)
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	// 계정이 갖고있는 권한 목록을 리턴한다.
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collectors = new ArrayList<>();
		collectors.add(() -> { return "ROLE_" + user.getAdminType(); });
		return collectors;
	}
}
