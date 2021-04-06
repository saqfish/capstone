package models;

import java.util.Locale;

/**
 * Session Model
 * @author Abdirisaq Sheikh
 */

public class Session {

  private String username;
  private Locale locale;
  final private Cache cache;

  public Session() {
   this.cache = new Cache();

    // Fill the cache on init
    Cache.cache(this.getCache());

    this.locale = Locale.getDefault();
  }

  /**
   * Update the session locale
   * @param locale local to use
   */
  public void updateLocale(Locale locale) {
    if (locale != null)
      this.locale = locale;
  }
  /**
   * Add a user to the session
   * @param str user name
   */
  public void newUser(String str) {
    if (str != null)
      username = str;
  }

  /**
   * Get the session user's locale
   * @return locale of the session's user
   */
  public Locale getLocale() {
    return locale;
  }
  /**
   * Get the session's user's name
   * @return the session's user's name
   */
  public String getUser() {
    return username;
  }

  public Cache getCache() {
    return cache;
  }


}