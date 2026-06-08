package com.example.nevera.service;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.SortType;
import com.example.nevera.common.enums.StorageLocation;
import com.example.nevera.dto.inventory.FridgeInventoryResponse;
import com.example.nevera.dto.inventory.InventoryResponse;
import com.example.nevera.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyFridgeService {

    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public Slice<FridgeInventoryResponse> getIngredients(StorageLocation storageLocation, Category category, Pageable pageable) {
        // QueryDSL이나 커스텀 Repository 메서드를 통해 동적 쿼리 호출
        return inventoryRepository.findIngredientsByFilters(storageLocation, category, pageable)
                .map(FridgeInventoryResponse::from);
    }

//    public InventoryResponse updateIngredient(Long id, IngredientUpdateRequestDto requestDto) {
//        Ingredient ingredient = ingredientRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("식재료를 찾을 수 없습니다."));
//
//        // 엔티티 수정 로직 (예: ingredient.update(requestDto))
//        ingredient.updateDetails(requestDto);
//
//        return IngredientResponseDto.fromEntity(ingredient);
//    }
//
//    // 구조 처리 (비율에 따른 잔여량 계산)
//    public void processRescue(Long id, int percentage) {
//        Ingredient ingredient = ingredientRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("식재료를 찾을 수 없습니다."));
//
//        if (percentage == 100) {
//            // 100% 처리 시 냉장고 리스트에서 삭제 (혹은 상태를 '구조완료'로 변경) [cite: 1245]
//            ingredientRepository.delete(ingredient);
//        } else {
//            // 100% 미만 시 잔여 식재료 금액(수량)으로 갱신 [cite: 1247, 1209]
//            ingredient.reduceAmount(percentage);
//        }
//    }
//
//    // 폐기 처리 (구조와 로직은 유사하나, 히스토리나 통계 테이블이 있다면 별도 기록 필요)
//    public void processDiscard(Long id, int percentage) {
//        Ingredient ingredient = ingredientRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("식재료를 찾을 수 없습니다."));
//
//        if (percentage == 100) {
//            // 100% 처리 시 냉장고 리스트에서 삭제 [cite: 1245]
//            ingredientRepository.delete(ingredient);
//        } else {
//            // 100% 미만 시 잔여 식재료 금액(수량)으로 갱신 [cite: 1247, 1209]
//            ingredient.reduceAmount(percentage);
//        }
//    }
}
