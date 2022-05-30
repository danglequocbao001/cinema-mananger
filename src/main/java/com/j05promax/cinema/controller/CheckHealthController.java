package com.j05promax.cinema.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.j05promax.cinema.repo.PostgreSQLRepo;
import com.j05promax.cinema.util.common.Common;
import com.j05promax.cinema.util.log.Log;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckHealthController {

	@GetMapping("/check_health")
	public String health(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		Context ctx = new Context();
		ctx.request = request;
		ctx.response = response;

		PostgreSQLRepo repo = PostgreSQLRepo.getInstance();
		ctx = Midleware.Authenticate(ctx);
		model.addAttribute("signedin", ctx.SignedIn);

		try {
			System.out.println(repo.Calendar.GetAll("1E453380F788A2E5627B161F7AA977B6").size());
		} catch (Exception e) {
			new Log(e).Show();
		}

		Common cm = Common.getInstance();

		System.out.println(cm.GetUID() + "|" + cm.GetUID() + "|");

		return "tmp";
	}

}
