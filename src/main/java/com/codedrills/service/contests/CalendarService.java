package com.codedrills.service.contests;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.codedrills.model.Contest;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CalendarService {
  private static String CALENDAR_URL = "https://calendar.google.com/calendar/ical/codedrills%40gmail.com/public/basic.ics";
  List<Contest> contests;

  public CalendarService() {
    fetchContests();
  }

  @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 60 * 1000)
  public void fetchContests() {
    log.info("Fetching calendar");
    ICalendar ical = Biweekly.parse(executeGET(CALENDAR_URL)).first();
    List<VEvent> events = ical.getEvents();
    contests = events.stream()
      .map(e -> new Contest(e))
      .collect(Collectors.toList());
  }

  public static String executeGET(String targetURL) {
    HttpURLConnection connection = null;

    try {
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();

      return IOUtils.toString(connection.getInputStream(), Charsets.UTF_8);
    } catch(Exception ex) {
      log.error("Error when fetching calendar", ex);
      return null;
    } finally {
      if(connection != null) {
        connection.disconnect();
      }
    }
  }

  public List<Contest> getContests() {
    return contests;
  }
}
