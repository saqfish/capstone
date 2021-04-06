package models;

import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Commonly used methods util class
 * @author Abdirisaq Sheikh
 */
public class Util {
    // SQL Database settings
    public static final String username="U08BzS";
    public static final String password="53689241454";
    public static final String db ="WJ08BzS";
    public static final String URL ="jdbc:mysql://wgudb.ucertify.com/" + db + "?useSSL=false";

    /**
    Generic alert method
     @param type alert type
     @param title alert title
     @param header alert header
     */
    public static void alert(Alert.AlertType type, String title, String header){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    /**
     Generic alert method
     @param type alert type
     @param title alert title
     @param header alert header
     @param content alert content
     */
    public static void alert(Alert.AlertType type, String title, String header, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


   public static DateTimeFormatter date24(boolean ss){
       String datePattern = "yyyy-MM-dd HH:mm" + (ss ? ":ss": "");
       DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(datePattern);
       return dateFormat;
   }

    public static DateTimeFormatter dateKK(){
        String datePattern = "yyyy-MM-dd KK:mm";
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(datePattern);
        return dateFormat;
    }
    public static DateTimeFormatter date12(){
        String datePattern = "yyyy-MM-dd hh:mm";
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(datePattern);
        return dateFormat;
    }
    /**
     * Check if times over-lap
     * @param start Start time
     * @param end End time
     * @param cuid Customer ID
     * @param aid Appointment ID
     * @param language Language resource bundle
     * @return returns false on over-lap
     * @throws SQLException if unsuccessful
     */
    public static boolean checkAppointment(Timestamp start, Timestamp end, int cuid, int aid, ResourceBundle language) throws SQLException {
        ZonedDateTime zustart = ZonedDateTime.of(start.toLocalDateTime(), ZoneId.of("UTC"));
        ZonedDateTime zestart = zustart.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime zuend = ZonedDateTime.of(end.toLocalDateTime(), ZoneId.of("UTC"));
        ZonedDateTime zeend = zuend.withZoneSameInstant(ZoneId.of("America/New_York"));

        int zs = zestart.getHour() - 1;
        int ze = zeend.getHour() - 1;

        if (zs > 22 || zs < 8 || ze > 22 || ze < 8) {
            alert(Alert.AlertType.ERROR,
                    language.getString("BUSINESS HOURS"),
                    language.getString("BUSINESS_HOURS_TITLE"),
                    language.getString("BUSINESS_HOURS_TEXT")
            );
            return false;
        }
        if (!checkTimes(start, end, cuid, aid)) {
            alert(Alert.AlertType.ERROR,
                    language.getString("SCHEDULE_CONFLICT"),
                    language.getString("CUSTOMER_TIME_CONFLICT")
            );
            return false;
        }
        return true;
    }

    /**
     * Generate new element ID
     * @param table SQL table
     * @param property SQL property
     * @return id
     * @throws SQLException on failure
     */
    public static int newId(String table, String property) throws SQLException {
        String sql = "SELECT * FROM "+table;
        Statement stmt = SQL.connect().createStatement();
        ResultSet result = stmt.executeQuery(sql);
        int c = 0;
        while (result.next()) {
            if (result.getInt(property) > c) c = result.getInt(property);
        }
        return c + 1;
    }

    /**
     * Query a SQL string
     * @param sqlString SQL string to query
     * @return ResultSet Result set of query
     * @throws SQLException Exception on failure
     */
    public static ResultSet sql(String sqlString) throws SQLException {
        Statement statement = SQL.connect().createStatement();
        return statement.executeQuery(sqlString);
    }

    /**
     * check available time slot
     * @param utcend utc end time
     * @param utcstart utc start time
     * @param cuid linked customer
     * @param aid appointment id
     * @return false on no times
     * @throws SQLException if unsuccessful
     */
    public static boolean checkTimes(Timestamp utcstart, Timestamp utcend, int cuid, int aid) throws SQLException {
        ZonedDateTime utcs = ZonedDateTime.of(utcstart.toLocalDateTime(), ZoneId.of("UTC"));
        ZonedDateTime ute = ZonedDateTime.of(utcend.toLocalDateTime(), ZoneId.of("UTC"));

        ResultSet utcResult =  sql("SELECT * FROM appointments WHERE Customer_ID = '"+cuid+"' AND Appointment_ID != '"+aid+"';");
        String s = "", e = "";
        while (utcResult.next()) {
            s = utcResult.getString("Start");
            e = utcResult.getString("End");

            ZonedDateTime utccs = ZonedDateTime.of(Timestamp.valueOf(s).toLocalDateTime(), ZoneId.of("UTC"));
            ZonedDateTime utcce = ZonedDateTime.of(Timestamp.valueOf(e).toLocalDateTime(), ZoneId.of("UTC"));

            System.out.println(utcs);
            System.out.println(ute);
            System.out.println(utccs);
            System.out.println(utcce);
            if (utcs.isBefore(utcce) && ute.isAfter(utccs) || utcs.isEqual(utccs) || ute.isEqual(utcce))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert to local time
     * @param time UTC
     * @param h hour
     * @param m mins
     * @return Timestamp time
     */
    public static Timestamp ttlt(LocalDate time, String h, String m) {
        LocalDateTime l = LocalDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), Integer.parseInt(h), Integer.parseInt(m));
        return Timestamp.valueOf(l);
    }
    /**
     * Convert to local to UTC
     * @param local local time
     * @param h hour
     * @param m mins
     * @return Timestamp time
     */
    public static Timestamp tutct(LocalDate local, String h, String m) {
        LocalDateTime t = LocalDateTime.of(local.getYear(), local.getMonthValue(), local.getDayOfMonth(), Integer.parseInt(h), Integer.parseInt(m));
        ZonedDateTime l = ZonedDateTime.of(t, ZoneId.systemDefault());
        ZonedDateTime utc = l.withZoneSameInstant(ZoneOffset.UTC);
        return Timestamp.valueOf(utc.format(date24(true)));
    }
}
