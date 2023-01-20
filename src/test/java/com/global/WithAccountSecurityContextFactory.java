package com.global;

import com.global.account.AccountService;
import com.global.account.SignUpForm;
import com.global.settings.SettingsController;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

// WithSecurityContextFactory<WithAccount> 의 generics 에
// test 폴더 밑에 있는 com.global 폴더에 작성한 Annotation type 을 지정함
@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

  private final AccountService accountService;

  @Override
  public SecurityContext createSecurityContext(WithAccount withAccount) {

    // SettingsControllerTest 클래스의 void updateProfile() 메소드에
    // @WithAccount("global") 어노테이션을 설정하면
    //   withAccount.value() 를 호출했을 때 "global" nickName 을 반환함
    // nickName 을 받아와서
    String nickName = withAccount.value();

    // 받아온 nickName 에 해당하는 user 객체에 정보를 넣고
    //  ㄴ 이 작업을 SettingsControllerTest 클래스의  @BeforeEach 에서 수행했으나
    //     이곳으로 가져옴
    // (이렇게 하면 Account 를 사용할 때마다 Account 에 해당하는 계정을 만들기 때문에)
    // test 할 때 @AfterEach 를 사용해서  -->  ㄴ 생성된 계정을 삭제해야 함
    // @AfterEach
    //  void afterEach(){
    //    accountRepository.deleteAll();  <-- 생성된 계정을 삭제함
    //  }
    SignUpForm signUpForm = new SignUpForm();
    signUpForm.setNickName(nickName);
    signUpForm.setEmail(nickName + "@gmail.com");
    signUpForm.setPassword("12345678");
    accountService.processNewAccount(signUpForm);

    // 받아온 nickName 에 해당하는 data 를 읽음
    // 받아온 nickName 에 해당하는 user 객체에 정보를 넣고
    // UserDetailsService 에서 loading 함  <-- 제대로 loading 이 됨
    UserDetails principal = accountService.loadUserByUsername(nickName);

    // principal, principal.getPassword(), principal.getAuthorities()
    //  ㄴ 이 data 들을 SpringSecurityContext 에 넣어줌
    Authentication authentication =
      new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());

    // data 들이 SecurityContext 에 setting 됨
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);

    return securityContext;
  }
}
