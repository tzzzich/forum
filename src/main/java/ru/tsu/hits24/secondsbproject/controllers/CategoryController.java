package ru.tsu.hits24.secondsbproject.controllers;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryCreateDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryEditDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryHierarchyDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.service.CategoryService;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping("create")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<Long> createCategory(@Valid @RequestBody CategoryCreateDto data){
        Long id = categoryService.createCategory(data);
        return ResponseEntity.ok(id);
    }

    @GetMapping("categoryId")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<CategoryDto> getCategory(@RequestParam Long id) {
        CategoryDto response = categoryService.getCategory( id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("edit")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<CategoryDto> editCategory(@Valid @RequestBody CategoryEditDto data, @RequestParam Long id) {
        CategoryDto response = categoryService.editCategory(data, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<ResponseDto> deleteCategory(@RequestParam Long id) {
        ResponseDto response = categoryService.deleteCategory(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("hierarchy")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<List<CategoryHierarchyDto>> getCategoryHierarchy() {
        List<CategoryHierarchyDto> response = categoryService.getCategoryHierarchy();
        return ResponseEntity.ok(response);
    }

    @PostMapping("moderator/set")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<CategoryDto> setModerator(@RequestParam Long categoryId, @RequestParam Long moderatorId) {
        CategoryDto response = categoryService.setModerator(categoryId, moderatorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("moderator/delete")
    @SecurityRequirement(name = "JWT")
    @ResponseBody
    public ResponseEntity<CategoryDto> removeModerator(@RequestParam Long categoryId, @RequestParam Long moderatorId) {
        CategoryDto response = categoryService.removeModerator(categoryId, moderatorId);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ResponseDto> handleDatabaseException (DatabaseException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidArgumentsException.class)
    public ResponseEntity<ResponseDto> handleInvalidArgumentsException (InvalidArgumentsException ex) {
        ResponseDto errorResponse = new ResponseDto("Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ResponseDto> handlePermissionDeniedException (PermissionDeniedException ex) {
        ResponseDto errorResponse = new ResponseDto("Forbidden", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception ex) {
        ResponseDto errorResponse = new ResponseDto("Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
