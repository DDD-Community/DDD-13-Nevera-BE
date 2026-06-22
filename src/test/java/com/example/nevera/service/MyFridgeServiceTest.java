package com.example.nevera.service;

import com.example.nevera.common.enums.Category;
import com.example.nevera.common.enums.IngredientStatus;
import com.example.nevera.dto.inventory.InventoryProcessRequest;
import com.example.nevera.dto.inventory.InventoryProcessResponse;
import com.example.nevera.entity.Inventory;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.SavingsRecord;
import com.example.nevera.repository.InventoryRepository;
import com.example.nevera.repository.SavingsRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MyFridgeServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private SavingsRecordRepository savingsRecordRepository;

    @InjectMocks
    private MyFridgeService myFridgeService;

    @Test
    void processIngredient_calculatesFromOriginalCost() {
        Inventory inventory = inventory(10_000, 10_000);
        given(inventoryRepository.findByIdForUpdate(1L)).willReturn(Optional.of(inventory));
        given(savingsRecordRepository.sumRatioByInventoryId(1L)).willReturn(0);
        given(savingsRecordRepository.sumRatioByInventoryIdAndStatus(1L, IngredientStatus.CONSUMED))
                .willReturn(0);
        given(savingsRecordRepository.sumRatioByInventoryIdAndStatus(1L, IngredientStatus.WASTED))
                .willReturn(0);

        InventoryProcessResponse response = myFridgeService.processIngredient(
                1L, 1L, new InventoryProcessRequest(IngredientStatus.CONSUMED, 25));

        assertThat(response.processedAmount()).isEqualTo(2_500);
        assertThat(response.remainingAmount()).isEqualTo(7_500);
        assertThat(response.completed()).isFalse();
        assertThat(inventory.getStatus()).isEqualTo(IngredientStatus.ACTIVE);

        ArgumentCaptor<SavingsRecord> captor = ArgumentCaptor.forClass(SavingsRecord.class);
        verify(savingsRecordRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualTo(2_500);
        assertThat(captor.getValue().getRatio()).isEqualTo(25);
    }

    @Test
    void processIngredient_completesAsConsumedWhenRatiosAreEqual() {
        Inventory inventory = inventory(10_000, 5_000);
        given(inventoryRepository.findByIdForUpdate(1L)).willReturn(Optional.of(inventory));
        given(savingsRecordRepository.sumRatioByInventoryId(1L)).willReturn(50);
        given(savingsRecordRepository.sumRatioByInventoryIdAndStatus(1L, IngredientStatus.CONSUMED))
                .willReturn(50);
        given(savingsRecordRepository.sumRatioByInventoryIdAndStatus(1L, IngredientStatus.WASTED))
                .willReturn(0);

        InventoryProcessResponse response = myFridgeService.processIngredient(
                1L, 1L, new InventoryProcessRequest(IngredientStatus.WASTED, 50));

        assertThat(response.processedAmount()).isEqualTo(5_000);
        assertThat(response.remainingAmount()).isZero();
        assertThat(response.inventoryStatus()).isEqualTo(IngredientStatus.CONSUMED);
        assertThat(response.completed()).isTrue();
        assertThat(inventory.getCompletedAt()).isNotNull();
    }

    private Inventory inventory(int originalCost, int currentCost) {
        Member member = Member.builder()
                .id(1L)
                .email("test@example.com")
                .build();
        return Inventory.builder()
                .id(1L)
                .member(member)
                .name("우유")
                .category(Category.DAIRY)
                .originalCost(originalCost)
                .cost(currentCost)
                .build();
    }
}
