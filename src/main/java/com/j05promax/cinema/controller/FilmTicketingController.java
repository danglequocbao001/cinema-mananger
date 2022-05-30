package com.j05promax.cinema.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j05promax.cinema.entity.Calendar;
import com.j05promax.cinema.entity.Film;
import com.j05promax.cinema.repo.PostgreSQLRepo;
import com.j05promax.cinema.util.common.Common;
import com.j05promax.cinema.util.log.Log;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FilmTicketingController {
    @GetMapping("/film/{id}")
    public String filmTicketing(
            HttpServletRequest request,
            HttpServletResponse response,

            @PathVariable String id,
            Model model) {
        Context ctx = new Context();
        ctx.request = request;
        ctx.response = response;

        ctx = Midleware.Authenticate(ctx);
        if (!Midleware.IsSignedIn(ctx)) {
            return "redirect:/auth/login";
        }

        Common cm = Common.getInstance();
        PostgreSQLRepo repo = PostgreSQLRepo.getInstance();
        ArrayList<Calendar> calendars = null;
        Film film = new Film();

        try {
            calendars = repo.Calendar.GetAll(id);
            film = repo.Film.GetByID(id);
        } catch (SQLException e) {
            new Log(e).Show();
        }

        ArrayList<String> dates = new ArrayList<>();
        for (Calendar cal : calendars) {
            dates.add(String.valueOf(cal.StartTime.getTime()));
        }

        ctx.SetUnicodeCookie("film-name", film.Name, "/");
        ctx.SetUnicodeCookie("film-id", film.FilmID, "/");
        ctx.SetUnicodeCookie("film-image", film.Thumnail, "/");
        ctx.SetUnicodeCookie("film-price", String.valueOf(film.Price), "/");
        ctx.SetUnicodeCookie("film-duration", String.valueOf(film.Duration), "/");
        ctx.SetUnicodeCookie("transaction-id", cm.GetUID(), "/");

        ctx.SetUnicodeCookie("calendar-dates", String.join("-", dates), "/");
        model.addAttribute("staffName", ctx.UserEmail);
        model.addAttribute("film", film);
        return "film-ticketing";
    }
}
