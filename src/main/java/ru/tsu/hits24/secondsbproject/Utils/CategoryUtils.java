package ru.tsu.hits24.secondsbproject.Utils;


import lombok.RequiredArgsConstructor;
import ru.tsu.hits24.secondsbproject.jpa.entity.CategoryEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.UserEntity;
import ru.tsu.hits24.secondsbproject.jpa.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CategoryUtils {

    private final CategoryRepository categoryRepository;

    public static boolean canEditCategory(UserEntity user, CategoryEntity category) {

        if (category.getModerators().contains(user)) {
            return true;
        }

        CategoryEntity parent = category.getParentCategory();
        if (parent != null) {
            return canEditCategory(user, parent);
        }
        return false;
    }

    public static List<Long> getAllCategoryIdsBelowCategory(CategoryEntity category) {
        List<Long> allCategoryIds = new ArrayList<>();

        allCategoryIds.add(category.getId());

        for (CategoryEntity subcategory : category.getSubcategories()) {
            allCategoryIds.addAll(getAllCategoryIdsBelowCategory(subcategory));
        }

        return allCategoryIds;
    }
}
