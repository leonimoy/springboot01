package com.global.settings.form;

import com.global.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

// Form 에 입력하는 용도
@Data
// @NoArgsConstructor
// <-- 매개변수 있는 생성자를 하나라도 작성하지 않으면
//     기본생성자는 자동으로 만들어지므로 이 어노테이션이 필요 없음
public class Notifications {

  private boolean studyCreatedByEmail;
  private boolean studyCreatedByWeb;
  private boolean studyEnrollmentResultByEmail;
  private boolean studyEnrollmentResultByWeb;
  private boolean studyUpdatedByEmail;
  private boolean studyUpdatedByWeb;

  /*
  SettingsController 에서 ModelMapper 를 사용해서 설정하려고 이 부분을 주석 처리함
  public Notifications(Account account){
    //  Notifications 클래스는 Bean 이 아니어서
    //  ModelMapper 를 주입할 수 없음
    // <-- 명시적으로 객체를 생성해 주어야 함
    ModelMapper modelMapper = new ModelMapper();
    //  ㄴ 이런 식으로 객체를 생성한 후 아래와 같이 해도 됨
    modelMapper.map(account, this);
    // SettingsController 에서 ModelMapper 를 사용해서 설정해도 됨

    this.studyCreatedByEmail = account.isStudyCreatedByEmail();
    this.studyCreatedByWeb = account.isStudyCreatedByWeb();
    this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
    this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
    this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
    this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
  }
  */
}
