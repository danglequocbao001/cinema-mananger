package com.j05promax.cinema.repo;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.j05promax.cinema.entity.Ticket;
import com.j05promax.cinema.util.common.Common;
import com.j05promax.cinema.util.db.DBConnection;
import com.j05promax.cinema.util.log.Log;

public class TicketRepo extends Repository {

    public TicketRepo(DBConnection conn) {
        super(conn);
    }

    public ArrayList<Ticket> GetAll() throws SQLException {
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();

        String query = "SELECT * FROM %s";
        ResultSet result = this.Query(String.format(query, Ticket.TableName()), (ParamSetter) (statement) -> {
        });

        while (result.next()) {
            tickets.add(new Ticket().FromResultSet(result));
        }

        this.Close();
        return tickets;
    }

    public Ticket GetByID(String id) {
        return new Ticket();
    }

    public ArrayList<String> getSeats() {
        String query = "select seat from tickets where seat is not null;";
        ArrayList<String> seats = new ArrayList<String>();
        try {
            ResultSet result = this.Query(query, (ParamSetter) (statement) -> {});
            while (result.next()) {
                seats.add(result.getString("seat"));
            }

        } catch (SQLException e) {
            new Log(e).Show();
        }
        return seats;
    }

    public boolean Create(Ticket ticket) {
        Common cm = Common.getInstance();
        ticket.TicketID = cm.GetUID();
        String query = "INSERT INTO %s (ticket_id, price, seat, user_id, created_at, updated_at, transaction, film_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?) RETURNING ticket_id;";
        try {
            ResultSet result = this.Query(
                    String.format(query, Ticket.TableName()), (ParamSetter) (statement) -> {
                        statement.setString(1, ticket.TicketID);
                        statement.setFloat(2, ticket.Price);
                        statement.setString(3, ticket.Seat);
                        statement.setString(4, ticket.UserID);
                        statement.setTimestamp(5, ticket.CreatedAt);
                        statement.setTimestamp(6, ticket.Updated);
                        statement.setString(7, ticket.Transaction);
                        statement.setString(8, ticket.FilmID);
                    });

            if (result.next()) {
                if (result.getString("ticket_id").contentEquals("")) {
                    return false;
                }
            }

        } catch (SQLException e) {
            new Log(e).Show();
            return false;
        }

        return true;
    }

    public boolean Update(String id, Ticket ticket) {
        return true;
    }

    public boolean Delete(String id) {
        return true;
    }
}