package com.projects.oleksii.leheza.cashtruck.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/client")
public class ClientController {

	@GetMapping(path = "/{id}")
	public void showClient(@PathVariable(value = "id") Long id) {

	}
}
