package com.codecool.assemblyshop.service;

import com.codecool.assemblyshop.dao.PartRepository;
import com.codecool.assemblyshop.model.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    @Autowired
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Part getPartById(long id) {
        return partRepository.findById(id).orElseThrow();
    }

    public Part savePart(Part part) {
        partRepository.save(part);
        return part;
    }

    public Part updateApprentice(Part part) {
        Part partOld = partRepository.findById(part.getId()).orElseThrow();
        if (part.getMaterial() == null) {
            part.setMaterial(partOld.getMaterial());
        }
        part.setMaterial(partOld.getMaterial());
        partRepository.save(part);
        return part;
    }

    public void deletePartById(long id) {
        partRepository.deleteById(id);
    }

}
