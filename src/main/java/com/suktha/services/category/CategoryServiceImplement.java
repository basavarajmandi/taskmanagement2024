package com.suktha.services.category;

import com.suktha.entity.Category;
import com.suktha.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplement implements CategoryService {

   @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<String> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
}
