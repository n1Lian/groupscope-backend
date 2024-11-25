package org.groupscope.schedule_nure;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.schedule_nure.service.ScheduleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AutoUpdater {

  ScheduleService scheduleService;

  // Sending updating requests every 6 hours
  //@Scheduled(fixedRate = 1000 * 60 * 60 * 6, initialDelay = 1000)
  public void update() {
    logUpdate("Groups", scheduleService.getGroupsMap());
    logUpdate("Teachers", scheduleService.getTeachersMap());
    logUpdate("Auditories", scheduleService.getAuditoriesMap());
    //logUpdate("Subjects", scheduleService.getSubjectsMap());
  }

  private void logUpdate(String dataType, Map<?, ?> dataMap) {
      if (!dataMap.isEmpty()) {
          log.info("{} update complete successful: {} elements.", dataType, dataMap.size());
      } else {
          log.error("{} update failed", dataType);
      }
  }
}
