package com.abhiyan.bookrentalsystem.service.impl;

import com.abhiyan.bookrentalsystem.converter.CategoryDtoConverter;
import com.abhiyan.bookrentalsystem.dto.CategoryDto;
import com.abhiyan.bookrentalsystem.dto.ResponseDto;
import com.abhiyan.bookrentalsystem.model.Category;
import com.abhiyan.bookrentalsystem.repository.CategoryRepo;
import com.abhiyan.bookrentalsystem.service.CategoryService;
import org.apache.coyote.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    private final CategoryDtoConverter categoryDtoConverter;

    public CategoryServiceImpl(CategoryRepo categoryRepo, CategoryDtoConverter categoryDtoConverter) {
        this.categoryRepo = categoryRepo;
        this.categoryDtoConverter = categoryDtoConverter;
    }

    @Override
    public ResponseDto saveCategory(CategoryDto categoryDto) {
        Category category = categoryDtoConverter.dtoToEntity(categoryDto);

        try{
            categoryRepo.save(category);
            return ResponseDto.builder()
                    .message("Category added successfully")
                    .status(true)
                    .build();
        }
        catch (Exception e){
            if(e.getMessage().contains("category")){
                return ResponseDto.builder()
                        .message("Category already exists")
                        .status(false)
                        .build();
            }
            else{
                e.printStackTrace();
                return ResponseDto.builder()
                        .message(e.getMessage())
                        .status(false)
                        .build();
            }
        }

    }

    @Override
    public List<Category> viewCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public void deleteCategory(Integer id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public CategoryDto editCategory(Integer id) {
        Category category = categoryRepo.findById(id).orElse(null);
        CategoryDto categoryDto = categoryDtoConverter.entityToDto(category);
        return categoryDto;
    }

    @Override
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepo.findById(id).orElse(null);
        existingCategory.setName(categoryDto.getName());
        existingCategory.setDescription(categoryDto.getDescription());
        categoryRepo.save(existingCategory);

        return categoryDtoConverter.entityToDto(existingCategory);
    }


}
