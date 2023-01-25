package com.global.settings;

import com.global.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/*
  com.global.domain.Account 클래스에 있는
  bio, url, occupation, location .
     이 네 개의 정보를 받아오는 클래스
     
     @NoArgsConstructor : 기본생성자를 자동으로 만들어 줌
*/

@Data
@NoArgsConstructor
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

  public Profile(Account account){
    this.bio = account.getBio();
    this.url = account.getUrl();
    this.occupation = account.getOccupation();
    this.location = account.getLocation();
    this.profileImage = account.getProfileImage();
  }

}
