package com.quickstore.inventoryservice.service;

import com.quickstore.inventoryservice.dto.InventoryResponse;
import com.quickstore.inventoryservice.model.Inventory;
import com.quickstore.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(skuCodes);
        List<InventoryResponse> inventoryResponseList = inventoryList.stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build())
                .toList();
        return inventoryResponseList;
    }
}
