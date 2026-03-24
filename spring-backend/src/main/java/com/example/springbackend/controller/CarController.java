package com.example.springbackend.controller;

import com.example.springbackend.model.BrandCar;
import com.example.springbackend.model.Car;
import com.example.springbackend.repository.BrandCarRepository;
import com.example.springbackend.repository.CarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/car")
@CrossOrigin
public class CarController {
    private final CarRepository repo;
    private final BrandCarRepository brandRepo;

    public CarController(CarRepository repo, BrandCarRepository brandRepo) {
        this.repo = repo;
        this.brandRepo = brandRepo;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        int p = (page == null || page < 1) ? 1 : page;
        int l = (limit == null || limit < 1) ? 6 : limit;
        Page<Car> carsPage = repo.findAll(PageRequest.of(p - 1, l));
        // Attach brand info for each car
        List<Map<String, Object>> carsWithBrand = new java.util.ArrayList<>();
        for (Car c : carsPage.getContent()) {
            Map<String, Object> carMap = new HashMap<>();
            carMap.put("id", c.getId());
            carMap.put("img", c.getImg());
            carMap.put("category", c.getCategory());
            carMap.put("pricePerDay", c.getPricePerDay());
            carMap.put("des", c.getDes());
            carMap.put("createdAt", c.getCreatedAt());
            carMap.put("updatedAt", c.getUpdatedAt());

            if (c.getBrandId() != null) {
                Optional<BrandCar> b = brandRepo.findById(c.getBrandId());
                if (b.isPresent()) {
                    carMap.put("brandId", b.get());
                } else {
                    carMap.put("brandId", c.getBrandId());
                }
            } else {
                carMap.put("brandId", null);
            }

            Map<String, Object> item = new HashMap<>();
            item.put("car", carMap);
            item.put("brand", carMap.get("brandId"));
            carsWithBrand.add(item);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("cars", carsWithBrand);
        resp.put("totalPages", carsPage.getTotalPages());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        Optional<Car> cOpt = repo.findById(id);
        if (cOpt.isEmpty())
            return ResponseEntity.notFound().build();
        Car c = cOpt.get();
        Map<String, Object> resp = new HashMap<>();
        resp.put("car", c);
        if (c.getBrandId() != null) {
            Optional<BrandCar> b = brandRepo.findById(c.getBrandId());
            b.ifPresent(brandCar -> resp.put("brand", brandCar));
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public Car create(@RequestBody Car car) {
        System.out.println("=== Nhận Car từ frontend ===");
        System.out.println("ID: " + car.getId());
        System.out.println("IMG: " + car.getImg()); // ← log này quan trọng nhất
        System.out.println("Category: " + car.getCategory());
        System.out.println("BrandId: " + car.getBrandId());
        System.out.println("PricePerDay: " + car.getPricePerDay());
        System.out.println("Des: " + car.getDes());
        return repo.save(car);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> update(@PathVariable String id, @RequestBody Car car) {
        return repo.findById(id).map(existing -> {
            car.setId(existing.getId());
            return ResponseEntity.ok(repo.save(car));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sorted")
    public ResponseEntity<?> getSortedCars(@RequestParam("sortId") String sortId,
            @RequestParam("showId") String showId) {
        Sort sort = Sort.unsorted();
        if ("1".equals(sortId))
            sort = Sort.by("pricePerDay").ascending();
        else if ("2".equals(sortId))
            sort = Sort.by("pricePerDay").descending();

        if ("all".equals(showId)) {
            List<Car> list = repo.findAll(sort);
            List<Map<String, Object>> listWithBrand = new java.util.ArrayList<>();
            for (Car c : list) {
                Map<String, Object> carMap = new HashMap<>();
                carMap.put("id", c.getId());
                carMap.put("img", c.getImg());
                carMap.put("category", c.getCategory());
                carMap.put("pricePerDay", c.getPricePerDay());
                carMap.put("des", c.getDes());
                carMap.put("createdAt", c.getCreatedAt());
                carMap.put("updatedAt", c.getUpdatedAt());
                if (c.getBrandId() != null) {
                    Optional<BrandCar> b = brandRepo.findById(c.getBrandId());
                    if (b.isPresent()) {
                        carMap.put("brandId", b.get());
                    } else {
                        carMap.put("brandId", c.getBrandId());
                    }
                } else {
                    carMap.put("brandId", null);
                }
                Map<String, Object> item = new HashMap<>();
                item.put("car", carMap);
                item.put("brand", carMap.get("brandId"));
                listWithBrand.add(item);
            }
            return ResponseEntity.ok(listWithBrand);
        }

        int limit = 0;
        if ("8".equals(showId))
            limit = 8;
        else if ("12".equals(showId))
            limit = 12;
        else
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid showId"));

        Page<Car> page = repo.findAll(PageRequest.of(0, limit, sort));
        List<Map<String, Object>> pageWithBrand = new java.util.ArrayList<>();
        for (Car c : page.getContent()) {
            Map<String, Object> carMap = new HashMap<>();
            carMap.put("id", c.getId());
            carMap.put("img", c.getImg());
            carMap.put("category", c.getCategory());
            carMap.put("pricePerDay", c.getPricePerDay());
            carMap.put("des", c.getDes());
            carMap.put("createdAt", c.getCreatedAt());
            carMap.put("updatedAt", c.getUpdatedAt());
            if (c.getBrandId() != null) {
                Optional<BrandCar> b = brandRepo.findById(c.getBrandId());
                if (b.isPresent()) {
                    carMap.put("brandId", b.get());
                } else {
                    carMap.put("brandId", c.getBrandId());
                }
            } else {
                carMap.put("brandId", null);
            }
            Map<String, Object> item = new HashMap<>();
            item.put("car", carMap);
            item.put("brand", carMap.get("brandId"));
            pageWithBrand.add(item);
        }
        return ResponseEntity.ok(pageWithBrand);
    }
}
