package com.codecool.assemblyshop.service;

import com.codecool.assemblyshop.dao.AssemblyRepository;
import com.codecool.assemblyshop.model.Assembly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssemblyService {

    private final AssemblyRepository assemblyRepository;

    @Autowired
    public AssemblyService(AssemblyRepository assemblyRepository) {
        this.assemblyRepository = assemblyRepository;
    }

    public List<Assembly> getAllAssemblies() {
        return assemblyRepository.findAll();
    }

    public Assembly getAssemblyById(long id) {
        return assemblyRepository.findById(id).orElseThrow();
    }

    public Assembly saveAssembly(Assembly assembly) {
        assemblyRepository.save(assembly);
        return assembly;
    }

    public void deletePartById(long id) {
        assemblyRepository.deleteById(id);
    }

    public List<Assembly> getSimpleParts() {
        List<Assembly> allAssemblies = assemblyRepository.findAll();
        List<Assembly> simpleAssemblies = new ArrayList<>();
        for (Assembly assembly : allAssemblies) {
            if (assembly.getParts().size() < 4) {
                simpleAssemblies.add(assembly);
            }
        }
        return simpleAssemblies;
    }

}
