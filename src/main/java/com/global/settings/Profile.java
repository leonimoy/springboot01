package com.global.settings;

import com.global.domain.Account;
import lombok.Data;

/*
  com.global.domain.Account 클래스에 있는
  bio, url, occupation, location .
     이 네 개의 정보를 받아오는 클래스
*/

@Data
public class Profile {

  private String bio;
  private String url;
  private String occupation;
  private String location;

  public Profile(Account account){
    this.bio = account.getBio();
    this.url = account.getUrl();
    this.occupation = account.getOccupation();
    this.location = account.getLocation();

  }

}
