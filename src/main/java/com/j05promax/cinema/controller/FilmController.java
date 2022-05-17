package com.j05promax.cinema.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.ui.Model;

@Controller
public class FilmController {

	@GetMapping("/film")
	public String GetAllFilms(
			HttpServletRequest request,
			HttpServletResponse response,

			Model model) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;

		ctx = Midleware.Authenticate(ctx);
		if (!ctx.SignedIn) {
			return "redirect:/auth/login";
		}

		model.addAttribute("staffName", "Staff's name");
		return "film";
	}

	@GetMapping("/film/{id}")
	public String GetFilmByID(
			HttpServletRequest request,
			HttpServletResponse response,

			@PathVariable String id) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;

		ctx = Midleware.Authenticate(ctx);
		if (!ctx.SignedIn) {
			return "redirect:/auth/login";
		}

		System.out.println("====[films id] " + id);
		return "film";
	}

	@PostMapping("/film/")
	public String CreateFilm() {
		return "film";
	}

	@PutMapping("/film/{id}")
	public String UpdateFilmByID(@PathVariable String id) {
		System.out.println("====[films id] " + id);
		return "film";
	}

	@DeleteMapping("/film/{id}")
	public String DeleteFilmByID(@PathVariable String id) {
		System.out.println("====[films id] " + id);
		return "film";
	}
}
