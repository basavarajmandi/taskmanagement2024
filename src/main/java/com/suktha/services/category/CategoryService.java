package com.suktha.services.category;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    List<String> getAllCategories();
}
