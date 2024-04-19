package ru.tsu.hits24.secondsbproject.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import ru.tsu.hits24.secondsbproject.dto.ResponseDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryCreateDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryEditDto;
import ru.tsu.hits24.secondsbproject.dto.category.CategoryHierarchyDto;
import ru.tsu.hits24.secondsbproject.exception.InvalidArgumentsException;
import ru.tsu.hits24.secondsbproject.exception.PermissionDeniedException;
import ru.tsu.hits24.secondsbproject.jpa.entity.CategoryEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.TopicEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.CategoryRepository;

import javax.management.BadAttributeValueExpException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final UserService userService;
    private final CategoryRepository categoryRepository;

    public Long createCategory(CategoryCreateDto data) {
        UserEntity user = userService.getCurrentUser();

        CategoryEntity parent = null;
        if (data.getParentCategory() != null) {
            parent = categoryRepository.findById(data.getParentCategory())
                    .orElse(null);
            if (parent == null) {
                throw new DatabaseException("Invalid parent category id.");
            }
        }


        if (!userService.isAdmin(user) && !userService.isModerator(user)){
            throw new PermissionDeniedException("User is not permitted to create categories.");
        } else if (userService.isModerator(user)) {
            if (parent!= null && (!parent.getModerators().contains(user))) {
                throw new PermissionDeniedException("User is not permitted to create subcategories in this category.");
            }
        }
        if (parent != null && parent.isContainsTopics() == true) {
            throw new PermissionDeniedException("Category can not contain subcategories");
        }
        Long id;
        CategoryEntity category = CategoryEntity.builder()
                .name(data.getName())
                .parentCategory(parent)
                .containsTopics(data.isContainsTopics())
                .moderators(new ArrayList<UserEntity>() )
                .subcategories(new ArrayList<CategoryEntity>())
                .topics(new ArrayList<TopicEntity>())
                .build();
        category.setCreationTime(LocalDateTime.now());
        category.setModificationTime(LocalDateTime.now());
        category.setCreator(user);
        try {
            id = categoryRepository.save(category).getId();
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return id;
    }

    public CategoryDto editCategory(CategoryEditDto data, Long id) {
        UserEntity user = userService.getCurrentUser();
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid category id"));

        if (!userService.isAdmin(user) && !userService.isModerator(user)){
            throw new PermissionDeniedException("User is not permitted to edit categories.");
        } else if (userService.isModerator(user)) {
            if (!category.getModerators().contains(user)) {
                throw new PermissionDeniedException("User is not permitted to edit this category.");
            }
        }

        if (!category.getTopics().isEmpty()){
            if (data.isContainsTopics() == false) {
                throw new InvalidArgumentsException("Cannot change change category type to contain subcategories" +
                        ", it already contains topics.");
            }
        }
        if (!category.getSubcategories().isEmpty()){
            if (data.isContainsTopics() == true) {
                throw new InvalidArgumentsException("Cannot change change category type to contain topics" +
                        ", it already contains subcategories.");
            }
        }

        category.setName(data.getName());
        category.setContainsTopics(data.isContainsTopics());
        category.setModificationTime(LocalDateTime.now());

        try {
            categoryRepository.save(category);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new CategoryDto(category);
    }

    public ResponseDto deleteCategory(Long id) {
        UserEntity user = userService.getCurrentUser();
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid category id"));
        if (!userService.isAdmin(user) && !userService.isModerator(user)){
            throw new PermissionDeniedException("User is not permitted to delete categories.");
        } else if (userService.isModerator(user)) {
            if (!category.getModerators().contains(user)) {
                throw new PermissionDeniedException("User is not permitted to edit this category.");
            }
        }
        if (!category.getSubcategories().isEmpty() || !category.getTopics().isEmpty()) {
            throw new InvalidArgumentsException("Cannot delete a non empty category.");
        }

        try {
            categoryRepository.delete(category);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new ResponseDto("Success", "Category deleted successfully.");
    }

    public CategoryDto setModerator(Long categoryId, Long moderId) {
        UserEntity user = userService.getCurrentUser();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->new DatabaseException("Invalid category id"));
        UserEntity moder = userService.findById(moderId);
        if (moder == null) {
            throw new DatabaseException("Invalid user id");
        }
        if (!userService.isAdmin(user) ){
            throw new PermissionDeniedException("User is not permitted to set moderators.");
        }

        userService.setModerator(moder);


        category.getModerators().add(moder);

        try {
            categoryRepository.save(category);
            userService.save(moder);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new CategoryDto(category);
    }

    public CategoryDto removeModerator(Long categoryId, Long moderId) {
        UserEntity user = userService.getCurrentUser();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->new DatabaseException("Invalid category id"));
        UserEntity moder = userService.findById(moderId);
        if (moder == null) {
            throw new DatabaseException("Invalid user id");
        }
        if (!userService.isAdmin(user) ){
            throw new PermissionDeniedException("User is not permitted set moderators.");
        }

        if (!category.getModerators().contains(moder)) {
            throw new InvalidArgumentsException("User with id:" + moderId
                    + "is not a moderator of a category:" + categoryId +".");
        }

        category.getModerators().remove(moder);

        try {
            categoryRepository.save(category);
            userService.save(moder);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return new CategoryDto(category);
    }

    public CategoryDto getCategory(Long id) {
        UserEntity user = userService.getCurrentUser();

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() ->new DatabaseException("Invalid category id"));

        return new CategoryDto(category);
    }

    public List<CategoryHierarchyDto> getCategoryHierarchy() {
        UserEntity user = userService.getCurrentUser();

        List<CategoryEntity> categories = categoryRepository.findAllByParentCategoryIsNull();

        List<CategoryHierarchyDto> result = categories.stream().map(c -> new CategoryHierarchyDto(c)).toList();

        return result;
    }

}
