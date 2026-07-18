package com.api.domu.domu.person;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.domu.domu.shared.ApiPaths;
import com.api.domu.domu.shared.ChangeStatusRequest;
import com.api.domu.domu.shared.EntityStatus;
import com.api.domu.domu.shared.PageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/persons")
@CrossOrigin
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@Valid @RequestBody CreatePersonRequest request) {
        System.out.println("LEGGAOOOOOOPOST");
        return personService.create(request);
    }

    @CrossOrigin
    @GetMapping
    public PageResponse<PersonResponse> list(@RequestParam(required = false) String term,   
                                             @RequestParam(required = false) EntityStatus status,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String sort) {
        System.out.println("LEGGAOOOOOOGET3_AT");

        return personService.list(term, status, page, size, sort);
    }

    @GetMapping("/{id}")
    public PersonResponse getById(@PathVariable Long id) {
        return personService.getById(id);
    }

    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody UpdatePersonRequest request) {
        return personService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public PersonResponse changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRequest request) {
        return personService.changeStatus(id, request.status());
    }
}
