package com.global.settings;

import com.global.WithAccount;
import com.global.account.AccountRepository;
import com.global.account.AccountService;
import com.global.account.SignUpForm;
import com.global.domain.Account;
import jdk.jshell.spi.ExecutionControlProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired AccountRepository accountRepository;

  @Autowired PasswordEncoder passwordEncoder;


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


  // @WithAccount("global") - 인증정보를 제공해 주는 Annotation
  @WithAccount("global")
  @DisplayName("프로필 수정 폼 테스트")
  @Test
  void updateProfileForm() throws Exception{
    String bio = "자기소개를 수정하는 경우";
    mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
          .andExpect(status().isOk())
          .andExpect(model().attributeExists("account"))
          .andExpect(model().attributeExists("profile"));
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
  /*
    void updateProfile() 로 test 하면
    global 이라는 account 를 생성하고나서
    global 이라는 account 를 Security Context 에 넣은 후에
    test 를 진행하게 됨  <-- test 성공 !!!

    Account global = accountRepository.findByNickName("global");
      <-- 에서 global 이라는 account 를 생성한 후에
          data 가 변경되었으므로, 다시 loading 해도
          data 가 변경된 것을 확인할 수 있게 됨

  */
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

  @WithAccount("global")
  @DisplayName("프로필 수정 테스트 - 입력값에 오류가 있는 경우")
  @Test
  void updateProfile_error() throws Exception{
    String bio = "자기소개를 35자가 넘게 길게 입력한 경우에는 오류가 발생하도록 Profile 클래스에 @Length(max=35) 라고 설정해 놓았음";
    mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
          .param("bio", bio)
          .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW))
          .andExpect(model().attributeExists("account"))
          .andExpect(model().attributeExists("profile"))
          .andExpect(model().hasErrors());

    Account global = accountRepository.findByNickName("global");
    assertNull(global.getBio());
  }

  @WithAccount("global")
  @DisplayName("비밀번호 수정 테스트하기 - 입력값 정상인 경우")
  @Test
  void updatePassword_success() throws Exception{
    mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
          .param("newPassword", "12345678")
          .param("newPasswordConfirm", "12345678")
          .with(csrf()))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
          .andExpect(flash().attributeExists("message"));

    Account global = accountRepository.findByNickName("global");

    // passwordEncoder 를 주입 받아서 비밀번호가 일치하는지 확인함
    assertTrue(passwordEncoder.matches("12345678", global.getPassword()));
  }

  @WithAccount("global")
  @DisplayName("비밀번호 수정 테스트하기 - 비밀번호 일치하지 않는 경우")
  @Test
  void updatePassword_fail() throws Exception{
    mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
          .param("newPassword", "12345678")
          .param("newPasswordConfirm", "12378456")
          .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeExists("passwordForm"))
          .andExpect(model().attributeExists("account"));

  }

}






