package com.global.settings;

import com.global.WithAccount;
import com.global.account.AccountRepository;
import com.global.account.AccountService;
import com.global.account.SignUpForm;
import com.global.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired AccountRepository accountRepository;
/*
  @BeforeEach
  void beforeEach(){


     이 부분을 WithAccountSecurityContextFactory 클래스의
     createSecurityContext() 메소드에서 진행함

    SignUpForm signUpForm = new SignUpForm();
    signUpForm.setNickName("global");
    signUpForm.setEmail("global@gmail.com");
    signUpForm.setPassword("12345678");
    accountService.processNewAccount(signUpForm);


  }
*/
  @AfterEach
  void afterEach(){
    accountRepository.deleteAll();
  }

  // 요청을 보낼 때, 어떤 user 가 보내는지 설정하기
  // Spring Security 에서 test 코드를 작성할 때
  //  Security Context 를 설정하는 방법을 사용함
  // @WithUserDetails(value = "global") 이렇게만 설정하면
  // @BeforeEach 메소드 보다 먼저 실행됨
  // setupBefore = TestExecutionEvent.TEST_EXECUTION 라고 설정해야
  // @BeforeEach 메소드 실행 후에 실행됨
  // @WithUserDetails(value = "global", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  // 이렇게 설정해도 @BeforeEach 메소드 보다 먼저 실행되는 경우가 있어서
  // @BeforeEach 메소드 에서 data 를 넣기 전에 "global" user 정보를 가져오려다가 에러가 발생함
  //    <--  Custom Annotation(@WithAccount) 을 작성해서 진행함
  // @WithAccount("global")
  //   ㄴ 객체를 먼저 가져온 후, WithAccountSecurityContextFactory 에서 data 를 넣어서 setting 함
  // @WithAccount("global") <-- 설정하지 않으면
  // java.lang.IllegalStateException: Unable to create SecurityContext using
  // @org.springframework.security.test.context.support.WithUserDetails
  // (setupBefore=TEST_METHOD, userDetailsServiceBeanName="", value="global") 에러가 발생함
  @WithAccount("global")
  @DisplayName("프로필 수정 테스트 - 입력값이 정상인 경우")
  @Test
  void updateProfile() throws Exception{
    String bio = "소개글을 수정함";
    mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
      .param("bio", "소개글을 수정함")
      .with(csrf()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
      .andExpect(flash().attributeExists("message"));

    // global 이라는 user 에 대한 정보를 수정한다는 설정
    Account global = accountRepository.findByNickName("global");
    // global.getBio() 에서 얻어온 정보가 33행의 bio 의 값으로 변경되었는지 확인함
    assertEquals(bio, global.getBio());

  }


}