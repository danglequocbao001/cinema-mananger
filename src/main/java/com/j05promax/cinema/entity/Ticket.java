package com.j05promax.cinema.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class Ticket extends Entity {

    public String TicketID = null;
    public float Price = 0;
    public String Seat = null;
    public String UserID = null;
    public String FilmID = null;
    public String Transaction = null;
    public Timestamp CreatedAt = new Timestamp(new Date().getTime());
    public Timestamp Updated = new Timestamp(new Date().getTime());

    public static String TableName() {
        return "tickets";
    };

    public Ticket FromResultSet(ResultSet result) throws SQLException {
        TicketID = result.getString("ticket_id");
        Price = result.getFloat("price");
        Seat = result.getString("seat");
        UserID = result.getString("user_id");
        FilmID = result.getString("film_id");
        CreatedAt = result.getTimestamp("created_at");
        Updated = result.getTimestamp("updated_at");
        Transaction = result.getString("transaction");
        return this;
    }
}
