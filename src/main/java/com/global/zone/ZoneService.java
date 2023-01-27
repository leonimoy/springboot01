package com.global.zone;

import com.global.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {
  private final ZoneRepository zoneRepository;

  // @PostConstruct : 생성자가 호출된 직후에 자동으로 호출되는 메소드
  @PostConstruct
  public void initZoneData() throws IOException {
    // data 가 하나도 없다면....
    // zone_ke.csv 파일에서 data 를 가져옴
    if (zoneRepository.count() == 0){
      Resource resource = new ClassPathResource("zone_kr.csv");
      List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                                 .stream()
                                 .map(line -> {
                                      String[] split = line.split(",");
                                      return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                                     }).collect(Collectors.toList());
      zoneRepository.saveAll(zoneList);
    }
  }

}
