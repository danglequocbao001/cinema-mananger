package com.j05promax.cinema.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.j05promax.cinema.entity.Film;
import com.j05promax.cinema.entity.Image;
import com.j05promax.cinema.util.common.Common;
import com.j05promax.cinema.util.db.DBConnection;
import com.j05promax.cinema.util.log.Log;

public class FilmRepo extends Repository {

    public FilmRepo(DBConnection conn) {
        super(conn);
    }

    public ArrayList<Film> GetAll(String search) throws SQLException {
        ArrayList<Film> films = new ArrayList<Film>();

        String query = (" SELECT " +
                " 	films.film_id, " +
                " 	films.name, " +
                " 	films.details, " +
                " 	films.price, " +
                " 	films.status, " +
                " 	films.category, " +
                " 	films.duration, " +
                " 	films.created_at, " +
                " 	films.updated_at, " +
                " 	images.url " +
                " from " +
                " 	films " +
                " LEFT JOIN images ON " +
                " 	images.image_id = films.film_id AND " +
                " 	images.image_type = 'films' " +
                " WHERE (LOWER(name) LIKE ? OR LOWER(category) LIKE ?) ");

        ResultSet result = this.Query(
                String.format(query, Film.TableName()),
                (ParamSetter) (statement) -> {
                    statement.setString(1, ("%" + search + "%").toLowerCase());
                    statement.setString(2, ("%" + search + "%").toLowerCase());
                });

        while (result.next()) {
            films.add(new Film().FromResultSet(result));
        }

        this.Close();
        return films;
    }

    public int CountFilmActive() throws SQLException {
        int count_film_active = 0;
        String query = "SELECT COUNT(*) AS counted FROM films WHERE status LIKE '%Đang chiếu%'";
        ResultSet result = this.Query(query, (ParamSetter) (statement) -> {
        });
        if (result.next()) {
            count_film_active = result.getInt("counted");
        }
        this.Close();
        return count_film_active;
    }

    public Film GetByID(String id) throws SQLException {
        String query = (" SELECT " +
                " 	films.film_id, " +
                " 	films.name, " +
                " 	films.details, " +
                " 	films.price, " +
                " 	films.status, " +
                " 	films.category, " +
                " 	films.duration, " +
                " 	films.created_at, " +
                " 	films.updated_at, " +
                " 	images.url " +
                " from " +
                " 	films " +
                " LEFT JOIN images ON " +
                " 	images.image_id = films.film_id AND " +
                " 	images.image_type = 'films' " +
                " WHERE film_id = ? ");
        Film user = null;
        ResultSet result = this.Query(String.format(query,Film.TableName()),
        (ParamSetter)(statement) -> {
            statement.setString(1, id);
        });
        if(result.next()){
            user = new Film().FromResultSet(result);
        }

        this.Close();
        return user;
    }

    public boolean Create(Film film) {
        Common cm = Common.getInstance();
        film.FilmID = cm.GetUID();
        film.CreatedAt = new Timestamp(new Date().getTime());
        film.Updated = new Timestamp(new Date().getTime());
        String query = "INSERT INTO %s (film_id, name, details, price, duration, status, created_at, updated_at, category) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING film_id";
        try {
            ResultSet result = this.Query(
                    String.format(query, Film.TableName()),
                    (ParamSetter) (statement) -> {
                        statement.setString(1, film.FilmID);
                        statement.setString(2, film.Name);
                        statement.setString(3, film.Details);
                        statement.setFloat(4, film.Price);
                        statement.setInt(5, film.Duration);
                        statement.setString(6, film.Status);
                        statement.setTimestamp(7, film.CreatedAt);
                        statement.setTimestamp(8, film.Updated);
                        statement.setString(9, film.Category);
                    });

            if (result.next()) {
                if (result.getString("film_id").contentEquals("")) {
                    return false;
                }
            }

            Image image = new Image();
            image.ImageID = film.FilmID;
            image.Url = film.Thumnail;
            image.ImageType = "films";
            image.CreatedAt = new Timestamp(new Date().getTime());
            image.Updated = new Timestamp(new Date().getTime());

            String query1 = "INSERT INTO %s (url,image_type,image_id,created_at) VALUES(?,?,?,?) RETURNING image_id;";

            ResultSet result1 = this.Query(
                    String.format(query1, Image.TableName()), (ParamSetter) (statement) -> {
                        statement.setString(1, image.Url);
                        statement.setString(2, image.ImageType);
                        statement.setString(3, image.ImageID);
                        statement.setTimestamp(4, image.CreatedAt);
                    });
            if (result1.next()) {
                if (result1.getString("image_id").contentEquals("")) {
                    return false;
                }
            }

        } catch (SQLException e) {
            new Log(e).Show();
            return false;
        }

        return true;
    }

    public boolean Update(String id, Film film) {
        return true;
    }

    public boolean Delete(String id) {
        return true;
    }
}
