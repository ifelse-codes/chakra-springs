package dev.chakra.springs.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class StoreController {

    private static final List<String> BRANDS = List.of("Arco", "Nimbus", "Velo", "Kova", "Zeno");
    private static final List<String> CATEGORIES = List.of("Electronics", "Clothing", "Home & Kitchen", "Books", "Sports");
    private static final List<String> ADJECTIVES = List.of("Ultra", "Pro", "Lite", "Max", "Swift");
    private static final List<String> NOUNS = List.of("Boost", "Gear", "Wave", "Edge", "Spark");

    private final Random rng = new Random();

    @GetMapping("/")
    public Map<String, Object> index() {
        return Map.of(
            "service", "chakra-springs",
            "runtime", "Spring Boot 3.3 / Java 21",
            "deployed_by", "Chakra — zero YAML, zero Dockerfile",
            "endpoints", List.of("/products", "/products/{id}", "/categories", "/actuator/health")
        );
    }

    @GetMapping("/products")
    public Map<String, Object> products(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String category) {

        count = Math.min(Math.max(count, 1), 100);
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(makeProduct());
        }
        if (category != null) {
            items.removeIf(p -> !category.equalsIgnoreCase((String) p.get("category")));
        }
        return Map.of("total", items.size(), "category_filter", category != null ? category : "none", "products", items);
    }

    @GetMapping("/products/{id}")
    public Map<String, Object> product(@PathVariable String id) {
        Map<String, Object> p = makeProduct();
        p.put("id", id);
        return p;
    }

    @GetMapping("/categories")
    public Map<String, Object> categories() {
        return Map.of("categories", CATEGORIES);
    }

    private Map<String, Object> makeProduct() {
        String brand = pick(BRANDS);
        String name = brand + " " + pick(ADJECTIVES) + " " + pick(NOUNS) + " " + (100 + rng.nextInt(900));
        double price = Math.round((5 + rng.nextDouble() * 495) * 100.0) / 100.0;
        int discount = pick(List.of(0, 0, 5, 10, 15, 20, 25));
        double finalPrice = Math.round(price * (1 - discount / 100.0) * 100.0) / 100.0;
        int stock = rng.nextInt(500);

        Map<String, Object> rating = new LinkedHashMap<>();
        rating.put("score", Math.round((2.5 + rng.nextDouble() * 2.5) * 10.0) / 10.0);
        rating.put("count", 4 + rng.nextInt(4800));

        Map<String, Object> product = new LinkedHashMap<>();
        product.put("id", UUID.randomUUID().toString());
        product.put("sku", "SKU-" + (10000 + rng.nextInt(90000)) + "-" + (char)('A' + rng.nextInt(16)));
        product.put("name", name);
        product.put("brand", brand);
        product.put("category", pick(CATEGORIES));
        product.put("price", price);
        product.put("discount_pct", discount);
        product.put("final_price", finalPrice);
        product.put("currency", "USD");
        product.put("in_stock", stock > 0);
        product.put("stock_qty", stock);
        product.put("rating", rating);
        return product;
    }

    private <T> T pick(List<T> list) {
        return list.get(rng.nextInt(list.size()));
    }
}
