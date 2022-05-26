package com.j05promax.cinema.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FilmController {

	@GetMapping("/film")
	public String GetAllFilms(
			HttpServletRequest request,
			HttpServletResponse response,

			@RequestParam(name = "name", required = false, defaultValue = "") String search,
			Model model) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;

		ctx = Midleware.Authenticate(ctx);
		if (!ctx.SignedIn)
			return "redirect:/auth/login";

		PostgreSQLRepo repo = PostgreSQLRepo.getInstance();

		int countedFilm = 0;
		try {
			countedFilm = repo.Film.CountFilmActive();
		} catch (SQLException e) {
			new Log(e).Show();
		}
		System.out.println(countedFilm);

		List<Film> films = new ArrayList<>();
		try {
			films = repo.Film.GetAll(search.strip());
		} catch (SQLException e) {
			new Log(e).Show();
		}

		model.addAttribute("films", films);
		model.addAttribute("staffRole", ctx.UserGroup);
		model.addAttribute("staffName", ctx.UserEmail.replace("@gmail.com", "") + " (" + ctx.UserGroup + ")");
		model.addAttribute("countedFilm", countedFilm);
		return "film";
	}

	// @GetMapping("/film/{id}")
	// public String GetFilmByID(
	// HttpServletRequest request,
	// HttpServletResponse response,

	// @PathVariable String id) {

	// Context ctx = new Context();
	// ctx.request = request;
	// ctx.response = response;

	// ctx = Midleware.Authenticate(ctx);
	// if (!ctx.SignedIn) {
	// return "redirect:/auth/login";
	// }

	// return "film";
	// }

	@GetMapping("add-film-info")
	public String addFilmInfo(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;

		ctx = Midleware.Authenticate(ctx);
		if (!Midleware.IsSignedIn(ctx)) {
			return "redirect:/auth/login";
		}

		if (!Midleware.IsManager(ctx)) {
			return "permission-denied";
		}

		return "add-film-info";
	}

	@PostMapping("store-film-info")
	public String storeFilmInfo(
			HttpServletRequest request,
			HttpServletResponse response,

			@RequestParam(name = "name", required = false, defaultValue = "") String name,
			@RequestParam(name = "category", required = false, defaultValue = "") String category,
			@RequestParam(name = "duration", required = false, defaultValue = "") String duration,
			@RequestParam(name = "thumbnail", required = false, defaultValue = "") String thumbnail,
			@RequestParam(name = "details", required = false, defaultValue = "") String details,
			@RequestParam(name = "film-start", required = false, defaultValue = "") String start,
			@RequestParam(name = "film-end", required = false, defaultValue = "") String end,
			@RequestParam(name = "price", required = false, defaultValue = "") String price,
			Model model) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;

		ctx = Midleware.Authenticate(ctx);
		if (!Midleware.IsSignedIn(ctx)) {
			return "redirect:/auth/login";
		}

		if (!Midleware.IsManager(ctx)) {
			return "permission-denied";
		}

		ctx.SetUnicodeCookie("name", name, "/");
		ctx.SetUnicodeCookie("category", category, "/");
		ctx.SetUnicodeCookie("duration", duration, "/");
		ctx.SetUnicodeCookie("thumbnail", thumbnail, "/");
		ctx.SetUnicodeCookie("details", details, "/");
		ctx.SetUnicodeCookie("start", start, "/");
		ctx.SetUnicodeCookie("end", end, "/");
		ctx.SetUnicodeCookie("price", price, "/");

		return "redirect:/add-film-schedule";
	}

	@PostMapping("/film")
	public String CreateFilm(
			HttpServletRequest request,
			HttpServletResponse response) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;
		Common cm = Common.getInstance();
		PostgreSQLRepo repo = PostgreSQLRepo.getInstance();

		Cookie tmpC;

		Film film = new Film();

		tmpC = ctx.getCookie("name");
		try {
			film.Name = URLDecoder.decode(tmpC.getValue(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		tmpC = ctx.getCookie("category");
		try {
			film.Category = URLDecoder.decode(tmpC.getValue(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		tmpC = ctx.getCookie("duration");
		try {
			film.Duration = Integer.valueOf(URLDecoder.decode(tmpC.getValue(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		tmpC = ctx.getCookie("thumbnail");
		try {
			film.Thumnail = URLDecoder.decode(tmpC.getValue(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		tmpC = ctx.getCookie("details");
		try {
			film.Details = URLDecoder.decode(tmpC.getValue(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		tmpC = ctx.getCookie("price");
		try {
			film.Price = Float.valueOf(URLDecoder.decode(tmpC.getValue(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!repo.Film.Create(film)) {
			return "error";
		}

		ArrayList<Calendar> calendars = new ArrayList<Calendar>();

		tmpC = ctx.getCookie("datetimes");
		String[] dates = new String[] {},
				dateAndTimes = new String[] {},
				times = new String[] {};

		try {
			dates = URLDecoder.decode(tmpC.getValue(), "UTF-8").split("@");
		} catch (UnsupportedEncodingException e) {
			new Log(e).Show();
		}

		for (int index = 0; index < dates.length; index++) {
			Date start = new Date(), end = new Date();
			Calendar calendar = new Calendar();

			dateAndTimes = dates[index].split("#");
			times = dateAndTimes[1].split("-");

			start = cm.parseWithFormat(
					String.format("%s %s", dateAndTimes[0], times[0]),
					"dd-MM-yyyy HH:mm");

			end = cm.parseWithFormat(
					String.format("%s %s", dateAndTimes[0], times[1]),
					"dd-MM-yyyy HH:mm");

			calendar.StartTime = new Timestamp(start.getTime());
			calendar.EndTime = new Timestamp(end.getTime());
			calendar.FilmID = film.FilmID;
			calendars.add(calendar);
		}

		try {
			for (Calendar c : calendars) {
				repo.Calendar.Create(c);
			}
			// repo.Calendar.InsertBulk(calendars);
		} catch (SQLException e) {
			new Log(e).Show();
		}

		return "redirect:/film";
	}

	@PostMapping("booking-ticket")
	public String storeFilmInfo(
			HttpServletRequest request,
			HttpServletResponse response) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;

		ctx = Midleware.Authenticate(ctx);
		if (!ctx.SignedIn)
			return "redirect:/auth/login";

		return "redirect:/";
	}
}
