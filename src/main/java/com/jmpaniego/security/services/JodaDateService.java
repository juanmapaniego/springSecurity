package com.jmpaniego.security.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

public class JodaDateService implements DateService {
  private final DateTimeZone timeZone;

  /**
   * Force system-wide timezone to ensure consistent
   * dates over all servers, independently from the region
   * the server is running.
   */
  JodaDateService(final DateTimeZone timeZone) {
    super();
    this.timeZone = timeZone;

    System.setProperty("user.timezone", timeZone.getID());
    TimeZone.setDefault(timeZone.toTimeZone());
    DateTimeZone.setDefault(timeZone);
  }

  @Override
  public DateTime now() {
    return DateTime.now(timeZone);
  }
}
