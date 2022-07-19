package com.codecool.assemblyshop.controller;

import com.codecool.assemblyshop.model.Assembly;
import com.codecool.assemblyshop.service.AssemblyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/assembly")
public class AssemblyController {

    private final AssemblyService assemblyService;

    @Autowired
    public AssemblyController(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    @GetMapping
    public List<Assembly> getAllAssemblies() {
        return assemblyService.getAllAssemblies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assembly> getAssemblyById(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok().body(assemblyService.getAssemblyById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Assembly> saveWizard(@Valid @RequestBody Assembly assembly, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(assemblyService.saveAssembly(assembly));
    }

    @DeleteMapping("/{id}")
    public void deletePartById(@PathVariable("id") long id) {
        assemblyService.deletePartById(id);
    }

    @GetMapping("/simple")
    public List<Assembly> simpleParts() {
        return assemblyService.getSimpleParts();
    }

}
