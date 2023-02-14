package com.quickstore.orderservice.service;

import com.quickstore.inventoryservice.dto.InventoryResponse;
import com.quickstore.orderservice.dto.OrderLineItemsDto;
import com.quickstore.orderservice.dto.OrderRequest;
import com.quickstore.orderservice.exceptions.ProductNotInStockException;
import com.quickstore.orderservice.model.Order;
import com.quickstore.orderservice.model.OrderLineItems;
import com.quickstore.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    /**
     *
     * @param orderRequest - contains a list of orderLineItems
     */
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);

        // Call inventory service and place order only if products are in stock

        // 1. To fetch inventory status we need to send skuCodes for each product
        List<String> skuCodes = orderLineItemsList.stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // 2. Fetch inventory status for each product in orderLineItemsList by calling api/inventory via webClient
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes)
                                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block(); // by default webclient will make async request, in order to make sync call use block().

        // 3. Check if all products in inventoryResponse are in stock or not
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new ProductNotInStockException("Product not in stock");
        }
    }

    public OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }
}
