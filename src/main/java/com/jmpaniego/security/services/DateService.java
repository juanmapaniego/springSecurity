package com.jmpaniego.security.services;

import org.joda.time.DateTime;

public interface DateService {
  /**
   * @return current date at the moment of the call
   */
  DateTime now();
}
