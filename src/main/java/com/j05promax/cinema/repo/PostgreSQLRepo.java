package com.j05promax.cinema.repo;

import com.j05promax.cinema.util.db.PostgreSQL;
import com.j05promax.cinema.util.log.Log;

public class PostgreSQLRepo {
    private static PostgreSQLRepo single_instance = null;

    public PostgreSQL postgreSQL;
    public AdminRepo Admin;
    public CalendarRepo Calendar;
    public EventRepo Event;
    public FilmRepo Film;
    public TicketRepo Ticket;
    public UserRepo User;

    private PostgreSQLRepo() {
		postgreSQL = new PostgreSQL();

		try {
			postgreSQL.Open("jdbc:postgresql://ec2-3-228-75-39.compute-1.amazonaws.com:5432/d547mp4ioththq?user=dyferylqhijzep&password=b1f9d7c095fd085cd8d1127ca1b974547fc0f2eef510ad66a57abb18479cc672");
            Admin = new AdminRepo(postgreSQL);
            Calendar = new CalendarRepo(postgreSQL);
            Event = new EventRepo(postgreSQL);
            Film = new FilmRepo(postgreSQL);
            Ticket = new TicketRepo(postgreSQL);
            User = new UserRepo(postgreSQL);
		} catch (Exception e) {
            new Log(e).Show();
		}
    }

    public static PostgreSQLRepo getInstance() {
        if (single_instance == null) {
            single_instance = new PostgreSQLRepo();
        }

        return single_instance;
    }
}