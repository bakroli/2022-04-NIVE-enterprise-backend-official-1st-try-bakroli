package com.codecool.assemblyshop.controller;

import com.codecool.assemblyshop.model.Part;
import com.codecool.assemblyshop.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/part")
public class PartController {

    private final PartService partService;

    @Autowired
    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public List<Part> getAllParts() {
        return partService.getAllParts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getWizardById(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok().body(partService.getPartById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Part> saveWizard(@Valid @RequestBody Part part, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(partService.savePart(part));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Part> updateApprentice(@PathVariable("id") long id, @Valid @RequestBody Part part, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || (id != part.getId())) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            return ResponseEntity.ok().body(partService.updateApprentice(part));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deletePartById(@PathVariable("id") long id) {
        partService.deletePartById(id);
    }


}
