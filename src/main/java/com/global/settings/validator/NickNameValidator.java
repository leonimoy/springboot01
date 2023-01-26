package com.global.settings.validator;

import com.global.account.AccountRepository;
import com.global.domain.Account;
import com.global.settings.form.NickNameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NickNameValidator implements Validator {

  private final AccountRepository accountRepository;

  @Override
  public boolean supports(Class<?> clazz) {
    return NickNameForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    // NickNameForm 객체가 먼저 메모리에 loading 되므로 null 값 확인 안 해도 됨
    NickNameForm nickNameForm = (NickNameForm)target;
    Account byNickName = accountRepository.findByNickName(nickNameForm.getNickName());
    // nickNameForm.getNickName() 으로 가져온 nickName 을 사용하는 user 가 있는지 없는지 중복 확인만 하면 됨
    // 중복된 경우에는 아래와 같이 처리함
    if (byNickName != null){
      errors.rejectValue("nickName", "wrong.value", "입력하신 닉네임은 사용할 수 없습니다.");
    }
  }
}
