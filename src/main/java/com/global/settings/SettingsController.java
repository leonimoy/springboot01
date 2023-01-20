package com.global.settings;

import com.global.account.CurrentUser;
import com.global.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

  // 주소표시줄에
  // /settings/profile 요청이 들어오면
  // 자동으로 호출되는 메소드
  // @CurrentUser  <-- 현재 user(현재 login 상태에 있는 회원)
  //                   정보를 가져오기 위한 Annotation
  @GetMapping("/settings/profile")
  public String profileUpdateForm(@CurrentUser Account account, Model model){
    // model.addAttribute("account", account); 아래의 code 와 같은 기능을 함
    // attributeName 이 자동으로 "account" 라고 지어짐
    model.addAttribute(account);
    // model.addAttribute("profile", new Profile(account)); 아래의 code 와 같은 기능을 함
    // attributeName 이 자동으로 "profile" 이라고 지어짐
    model.addAttribute(new Profile(account));

    return "settings/profile";

  }

}
