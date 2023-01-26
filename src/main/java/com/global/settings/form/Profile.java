package com.global.settings.form;

import com.global.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

/*
  com.global.domain.Account 클래스에 있는
  bio, url, occupation, location .
     이 네 개의 정보를 받아오는 클래스
     
     @NoArgsConstructor : 기본생성자를 자동으로 만들어 줌
*/

@Data
// @NoArgsConstructor
// <-- 매개변수 있는 생성자를 하나라도 작성하지 않으면
//     기본생성자는 자동으로 만들어지므로 이 어노테이션이 필요 없음
public class Profile {

  @Length(max=35)
  private String bio;
  @Length(max=50)
  private String url;
  @Length(max=50)
  private String occupation;
  @Length(max=50)
  private String location;

  private String profileImage;

  /*
  SettingsController 에서 ModelMapper 를 사용해서 설정하려고 이 부분을 주석 처리함
  public Profile(Account account){
    // ModelMapper modelMapper = new ModelMapper();
    // modelMapper.map(this, account);
    this.bio = account.getBio();
    this.url = account.getUrl();
    this.occupation = account.getOccupation();
    this.location = account.getLocation();
    this.profileImage = account.getProfileImage();
  }
  */
}
