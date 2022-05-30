package com.j05promax.cinema.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j05promax.cinema.entity.Ticket;
import com.j05promax.cinema.repo.PostgreSQLRepo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FilmTicketingLastStep {
    @GetMapping("film-ticketing-last-step")
    public String filmTicketingLastStep(Model model, HttpServletRequest request, HttpServletResponse response) {
        Context ctx = new Context();
        ctx.request = request;
        ctx.response = response;

        ctx = Midleware.Authenticate(ctx);
		if (!Midleware.IsSignedIn(ctx)) {
			return "redirect:/auth/login";
        }

        return "film-ticketing-last-step";
    }

    @PostMapping("/create-ticket")
    public String bookTicket(Model model, HttpServletRequest request, HttpServletResponse response) {
        Context ctx = new Context();
        ctx.request = request;
        ctx.response = response;

        ctx = Midleware.Authenticate(ctx);
		if (!Midleware.IsSignedIn(ctx)) {
			return "redirect:/auth/login";
        }

        PostgreSQLRepo repo = PostgreSQLRepo.getInstance();
        String seatS = ctx.getCookie("seats").getValue();
        String[] seats = seatS.split("\\.");
        System.out.print(seats.length);

        for(int index = 0; index < seats.length; index++) {
            Ticket ticket = new Ticket();
            ticket.Price = Float.valueOf(ctx.getCookie("film-price").getValue()) * Float.valueOf("0.9");
            ticket.UserID = ctx.getCookie("current-user").getValue();
            ticket.Seat = seats[index];
            ticket.Transaction = ctx.getCookie("transaction-id").getValue();
            ticket.FilmID = ctx.getCookie("film-id").getValue();
            repo.Ticket.Create(ticket);
        }
        
        return "redirect:/";
    }
}
