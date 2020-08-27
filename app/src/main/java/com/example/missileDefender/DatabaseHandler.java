package com.example.missileDefender;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DatabaseHandler extends AsyncTask<String, Void, String> {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    @SuppressLint("StaticFieldLeak")
    private GameOver context;
    private static String dbURL;
    private Connection conn;
    private static final String TAG = "StudentDatabaseHandler";
    private static final String App_Scores = "AppScores";
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());


    DatabaseHandler(GameOver ctx) {
        context = ctx;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    protected String doInBackground(String... values) {


        String initials = values[0];
        int score = Integer.parseInt(values[1]);
        int level = Integer.parseInt(values[2]);

        try {
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

            StringBuilder sb = new StringBuilder();
            if (initials.contains("080")) {
                sb.append("*"+lowestScore());
            }
            else if (initials.contains("070")){
                sb.append(getAll());
            }
            else {
                addStudent(initials, score, level);
                sb.append(getAll());
            }


            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d(TAG, "onPostExecute: " + s);
        context.setResults(s);
    }

    private String addStudent(String initials, int score, int level) throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "insert into " + App_Scores + " values (" +
                System.currentTimeMillis() + ", '" + initials + "', " + score + ", " +
                level +
                ")";
        int result = stmt.executeUpdate(sql);
        stmt.close();
        return "Student " + initials + " added (" + result + " record)\n\n";
    }


    private String getAll() throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "select * from " + App_Scores + " order by Score desc limit 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);
        int counter = 1;
        while (rs.next()) {
            long dateTime = rs.getLong(1);
            String initials = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
            Date resultdate = new Date(dateTime);
            if (initials.length() == 1){
                initials = "  " + initials;
            }
            else if(initials.length() == 2){
                initials = " " + initials;
            }
            String pad1 = "    ";
            String pad2 = "    ";
            if (level > 99){
                pad1 = "";
            }
            else if (level > 9){
                pad1 = "  ";
            }
            if (score > 99){
                pad2 = "";
            }
            else if (score > 9){
                pad2 = "  ";
            }

            sb.append(String.format(Locale.getDefault(),
                    "%s               %s%d               %s%d              %12s%n", initials.toUpperCase(), pad1, level, pad2, score, sdf.format(resultdate)));
            //"%-10d %-12s %8.2d %12s%n
            counter++;
        }
        rs.close();
        stmt.close();

        return sb.toString();
    }

    private int lowestScore() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from " + App_Scores + " order by Score desc limit 10";

        int highscore = 0;

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int score = rs.getInt(3);
            highscore = score;
        }
        rs.close();
        stmt.close();

        return highscore;


    }
}