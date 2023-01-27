package com.global.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.account.AccountService;
import com.global.account.CurrentUser;
import com.global.domain.Account;
import com.global.domain.Tag;
import com.global.domain.Zone;
import com.global.settings.form.*;
import com.global.settings.validator.NickNameValidator;
import com.global.settings.validator.PasswordFormValidator;
import com.global.tag.TagRepository;
import com.global.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


// error 가 없는 경우, update(수정작업) 를 진행함
// data 를 수정하는 경우에는 Service 에 위임해서
//                            ㄴ (이렇게 할려면 Service 클래스 타입의 멤버변수를 선언해야 함)
//                            멤버변수를 parameter 하는 생성자를 작성해서 Service 객체를 주입 받음
//                                 ㄴ @RequiredArgsConstructor 어노테이션을 사용함
//                            프로그래머가 code 를 작성해서 객체를 생성하지 않고
//                            필요한 객체를 Spring 이 자동으로 생성해서 할당까지 해줌
//                              ㄴ Inversion of Control (제어의 역행) : IoC
// Transaction 안에서 수정해야  DB 에 반영됨
// <-- @Transactional 어노테이션 사용


@Controller
@RequiredArgsConstructor
public class SettingsController {

  static final String ROOT = "/";
  static final String SETTINGS  = "settings";
  static final String ZONES  = "zones";

  // "settings.profile" 문자열을 static 변수에 저장함
  static final String SETTINGS_PROFILE_VIEW = "settings/profile";
  static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW;

  static final String SETTINGS_PASSWORD_VIEW = "settings/password";
  static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW;

  static final String SETTING_NOTIFICATIONS_VIEW = "settings/notifications";
  static final String SETTING_NOTIFICATIONS_URL = "/" + SETTING_NOTIFICATIONS_VIEW;

  static final String SETTINGS_ACCOUNT_VIEW = "settings/account";
  static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW;

  static final String SETTINGS_TAGS_VIEW = "settings/tags";
  static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW;

  static final String SETTINGS_ZONES_VIEW = "settings/zones";
  static final String SETTINGS_ZONES_URL = "/" + SETTINGS_ZONES_VIEW;



  // Service type 의 멤버변수 선언
  private final AccountService accountService;

  // SettingsController 에서 ModelMapper 설정하기
  // @RequiredArgsConstructor 에 의해서 Spring 으로부터 주입 받기
  private final ModelMapper modelMapper;

  // SettingsController 에서 ModelMapper 설정하기
  // @RequiredArgsConstructor 에 의해서 Spring 으로부터 주입 받기
  private final NickNameValidator nickNameValidator;

  private final TagRepository tagRepository;
  private final ZoneRepository zoneRepository;

  // ObjectMapper : Java 객체를 JSON 으로 변환함
  private final ObjectMapper objectMapper;


  // PasswordFormValidator 를 Bean 으로 등록하지 않고
  // InitBinder 를 사용해서 객체를 생성함
  @InitBinder("passwordForm")
  public void initBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(new PasswordFormValidator());
  }

  // nickNameForm 을 처리할 때 nickNameValidator 를 추가하라는 의미
  @InitBinder("nickNameForm")
  public void nickNameInitBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(nickNameValidator);
  }

  // 주소표시줄에
  // /settings/profile 요청이 들어오면
  // 자동으로 호출되는 메소드
  // @CurrentUser  <-- 현재 user(현재 login 상태에 있는 회원)
  //                   정보를 가져오기 위한 Annotation
  @GetMapping(SETTINGS_PROFILE_URL)
  public String updateProfileForm(@CurrentUser Account account, Model model){
    // model.addAttribute("account", account); 아래의 code 와 같은 기능을 함
    // attributeName 이 자동으로 "account" 라고 지어짐
    model.addAttribute(account);
    // model.addAttribute("profile", new Profile(account)); 아래의 code 와 같은 기능을 함
    // attributeName 이 자동으로 "profile" 이라고 지어짐
    // model.addAttribute(new Profile(account));
    //  ㄴ Profile 클래스에 매개변수 있는 생성자가 없으면 오류가 발생함
    //   ㄴ 아래와 같이 ModelMapper 로 처리함
    //  ┌ Profile 객체를 자동으로 생성해서 account 클래스의 멤버변수에 있는 정보를 Profile 객체에 mapping 함
    model.addAttribute(modelMapper.map(account, Profile.class));
    return SETTINGS_PROFILE_VIEW;

  }

  // post 방식으로 요청이 들어올 때
  // 자동으로 호출되는 메소드
  // @CurrentUser  <-- 현재 user(현재 login 상태에 있는 회원)
  //                   정보를 가져오기 위한 Annotation
  // @Valid @ModelAttribute Profile profile
  //   ㄴ form 에서 입력한 값들은 @ModelAttribute 를 사용해서 Profile 객체로 받아옴
  //   ㄴ @ModelAttribute 은 생략할 수 있음    ㄴ  @ModelAttribute 로 data 를 biding 함
  // Errors errors : @ModelAttribute 객체의 biding error 를 받아주는 객체
  //  ㄴ binding 하는데 error 가 있든지, validation 하는데 error 가 있는 경우 Errors 객체가 생성됨
  // @Valid @ModelAttribute Profile profile
  //  <-- Spring 이 Profile 객체를 자동으로 생성해서 (setter 를 사용해서) parameter 에 주입하는데
  //      이때, 기본 생성자를 호출함. Profile 클래스에 기본생성자를 작성해야 함
  @PostMapping(SETTINGS_PROFILE_URL)
  public String updateProfile(@CurrentUser Account account,
                              @Valid @ModelAttribute Profile profile,
                              Errors errors, Model model, RedirectAttributes redirectAttributes){

    // error 가 있는 경우 (validation 위반)
    // ㄴ model 에 form 에 채워진 data 가 자동으로 들어가고,
    //   error 에 대한 정보도 model 에 자동으로 들어감
    // ㄴ account 객체만 명시적으로 넣어주면 됨
    if(errors.hasErrors()){
      model.addAttribute(account);
      // 화면에는 현재 view 를 그대로 보여줌
      return SETTINGS_PROFILE_VIEW;
    }

    // error 가 없는 경우, update(수정작업) 를 진행함
    // data 를 수정하는 경우에는 Service 에 위임해서
    // Transaction 안에서 수정해야  DB 에 반영됨
    // <-- @Transactional 어노테이션 사용
    accountService.updateProfile(account, profile);
    redirectAttributes.addFlashAttribute("message", "프로필이 수정되었습니다.");

    // 수정한 후에는 redirect 로 root page 로 이동함
    //                            ㄴ  localhost:8080/
    //  SETTINGS_PROFILE_URL 변수에 / 가 이미 있으므로
    //  redirect: 뒤에는 / 가 없어야 함
    //  SETTINGS_PROFILE_URL = "/settings/profile"
    return "redirect:" + SETTINGS_PROFILE_URL;
  }

  @GetMapping(SETTINGS_PASSWORD_URL)
  public String updatePasswordForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);

    // Form 으로 사용할 객체가 없음 -> Form 으로 사용할 클래스 작성하기
    // com.global.settings.form.PasswordForm 클래스 작성함
    model.addAttribute(new PasswordForm());

    return SETTINGS_PASSWORD_VIEW;
  }

  // @CurrentUser Account account : 현재 접속해 있는 사용자
  @PostMapping(SETTINGS_PASSWORD_URL)
  public String updatePassword(@CurrentUser Account account,
                               @Valid PasswordForm passwordForm,
                               Errors errors, Model model,
                               RedirectAttributes redirectAttributes){
    if(errors.hasErrors()){
      model.addAttribute(account);
      return SETTINGS_PASSWORD_VIEW;
    }

    accountService.updatePassword(account, passwordForm.getNewPassword());
    redirectAttributes.addFlashAttribute("message", "비밀번호를 수정했습니다.");

    return "redirect:" + SETTINGS_PASSWORD_URL;
  }

  @GetMapping(SETTING_NOTIFICATIONS_URL)
  public String updateNotificationsForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);
    // model.addAttribute(new Notifications(account));
    //  ㄴ Notifications 클래스에 매개변수 있는 생성자가 없으면 오류가 발생함
    //   ㄴ 아래와 같이 ModelMapper 로 처리함
    //  ┌ Notifications 객체를 자동으로 생성해서 account 클래스의 멤버변수에 있는 정보를 Notifications 객체에 mapping 함
    model.addAttribute(modelMapper.map(account, Notifications.class));
    return SETTING_NOTIFICATIONS_VIEW;
  }

  // @Valid Notifications notifications : 여기서는 다른 검증이 불필요해서
  // InitBinder() 를 별도로 지정하지 않음
  @PostMapping(SETTING_NOTIFICATIONS_URL)
  public String updateNotifications(@CurrentUser Account account,
                                    @Valid Notifications notifications,
                                    Errors errors, Model model,
                                    RedirectAttributes redirectAttributes){
    if(errors.hasErrors()){
      model.addAttribute(account);
      return SETTING_NOTIFICATIONS_VIEW;
    }

    accountService.updateNotifications(account, notifications);
    redirectAttributes.addFlashAttribute("message", "알림 설정이 변경되었습니다.");
    return "redirect:" + SETTING_NOTIFICATIONS_URL;
  }

  // tag 를 수정하는 view 보여주기
  @GetMapping(SETTINGS_TAGS_URL)
  public String updateTags(@CurrentUser Account account, Model model){
    model.addAttribute(account);

    // form 을 입력하는 view 에서 등록한 tag 정보들을 조회하기
    // Account 가 가지고 있는 tag 정보를 가져옴
    Set<Tag> tags = accountService.getTags(account);

    // view 에서 보여줄 때는 Tag Entity type 으로
    // 보여주는 것이 아니고 문자열로 전송함
    // Tag type 의 List 가 아니고, 문자열 type 의 List 로 함
    // CurrentUser 가 가지고 있는 tag 목록을 view 에 전달하게 됨
    // view 에서 이 정보를 화면에 (thymeleaf 사용)보여주게 됨  <-- settings/tags.html
    model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

    // settings/tags
    return SETTINGS_TAGS_VIEW;
  }

  // 관심 주제 등록을 위한 기능 구현
  // @RequestBody TagForm tagForm  <-- ajax 요청으로부터 받아온 data
  // @ResponseBody  <-- http 요청의 body 부분을 전달받음
  //                    Java 객체를 JSON 형태로 변환해서 http body 에 담음
  @PostMapping(SETTINGS_TAGS_URL + "/add")
  @ResponseBody
  public ResponseEntity addTag(@CurrentUser Account account,
                               @RequestBody TagForm tagForm){
    String title = tagForm.getTagTitle();

    // title 에 할당된 문자열과 같은 tag 가
    // 있는지 없는지 DB 에서 찾아봄
    // tag 가 없으면 Account 에 추가해 줌
    /*
    Optional 을 사용하는 경우
    Tag tag = tagRepository.findbyTitle(title).orElseGet(() -> tagRepository.save(Tag.builder()
                                                                .title(tagForm.getTagTitle())
                                                                .build()));
    */

    Tag tag = tagRepository.findByTitle(title);
    /* Optional 을 사용하지 않고 조건문으로 null 값 처리를 하는 경우 */
    /* tagRepository.findbyTitle(title) 으로 tag 를 가져오지 못하면 찾아서 할당함 */
    if (tag == null){
      tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
    }

    /* tag 를 가져온 경우(tag 가 DB 에 있음) account 에 tag 를 추가함 */
    accountService.addTag(account, tag);

    return ResponseEntity.ok().build();
  }

  @PostMapping(SETTINGS_TAGS_URL + "/remove")
  @ResponseBody
  public ResponseEntity removeTag(@CurrentUser Account account,
                                  @RequestBody TagForm tagForm){
    String title = tagForm.getTagTitle();
    Tag tag = tagRepository.findByTitle(title);
    if(tag == null){
      return ResponseEntity.badRequest().build();
    }
    accountService.removeTag(account, tag);
    return ResponseEntity.ok().build();
  }


  // nickName 수정하기 위해서 @GetMapping, @PostMapping  메소드 작성하기
  @GetMapping(SETTINGS_ACCOUNT_URL)
  public String updateAccountForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, NickNameForm.class));
    return SETTINGS_ACCOUNT_VIEW;
  }

  @PostMapping(SETTINGS_ACCOUNT_URL)
  public String updateAccount(@CurrentUser Account account,
                              @Valid NickNameForm nickNameForm,
                              Errors errors, Model model,
                              RedirectAttributes redirectAttributes){
    if(errors.hasErrors()){
      model.addAttribute(account);
      return SETTINGS_ACCOUNT_VIEW;
    }

    // error 가 발생하지 않은 경우
    accountService.updateNickName(account, nickNameForm.getNickName());
    redirectAttributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
    return "redirect:" + SETTINGS_ACCOUNT_URL;

  }

  @GetMapping(SETTINGS_ZONES_URL)
  public String updateZoneForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
    model.addAttribute(account);

    Set<Zone> zones = accountService.getZones(account);
    model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

    List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
    model.addAttribute("allZones", objectMapper.writeValueAsString(allZones));

    return SETTINGS_ZONES_VIEW;
  }

  // zones.html 의 ajax 에서
  //  method: "POST",
  //  url: "/settings/zones" + url,  <-- 이렇게 지정하면
  // Controller 에서는
  // @PostMapping("/settings/zones" + url) 로 받게 됨
  // SETTINGS_ZONES_URL  <--  /settings/zones
  // @PostMapping(SETTINGS_ZONES_URL + "/add")
  // public ResponseEntity addZone()
  //                        ㄴ ajax 에서 onAdd(e) 를 호출할 때 자동으로 호출되는 메소드
  @PostMapping(SETTINGS_ZONES_URL + "/add")
  @ResponseBody
  public ResponseEntity addZone(@CurrentUser Account account,
                                @RequestBody ZoneForm zoneForm){

    Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
    if(zone == null){
      return ResponseEntity.badRequest().build();
    }
    accountService.addZone(account, zone);
    return ResponseEntity.ok().build();
  }

  // zones.html 의 ajax 에서
  //  method: "POST",
  //  url: "/settings/zones" + url,  <-- 이렇게 지정하면
  // Controller 에서는
  // @PostMapping("/settings/zones" + url) 로 받게 됨
  // SETTINGS_ZONES_URL  <--  /settings/zones
  // @PostMapping(SETTINGS_ZONES_URL + "/remove")
  // public ResponseEntity removeZone()
  //                        ㄴ ajax 에서 onRemove(e) 를 호출할 때 자동으로 호출되는 메소드
  @PostMapping(SETTINGS_ZONES_URL + "/remove")
  @ResponseBody
  public ResponseEntity removeZone(@CurrentUser Account account,
                                   @RequestBody ZoneForm zoneForm){

    Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
    if(zone == null){
      return ResponseEntity.badRequest().build();
    }
    accountService.removeZone(account, zone);
    return ResponseEntity.ok().build();
  }

}
