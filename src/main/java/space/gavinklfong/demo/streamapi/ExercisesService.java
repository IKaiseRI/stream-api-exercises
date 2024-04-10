package space.gavinklfong.demo.streamapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.gavinklfong.demo.streamapi.models.Customer;
import space.gavinklfong.demo.streamapi.models.Order;
import space.gavinklfong.demo.streamapi.models.Product;
import space.gavinklfong.demo.streamapi.repos.CustomerRepo;
import space.gavinklfong.demo.streamapi.repos.OrderRepo;
import space.gavinklfong.demo.streamapi.repos.ProductRepo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExercisesService {

    private final CustomerRepo customerRepo;
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;


    public List<Product> exercise1() {
        return productRepo.findAll().stream()
                .filter(product -> product.getCategory().equals("Books"))
                .filter(product -> product.getPrice() > 100)
                .toList();
    }

    public List<Product> exercise1a() {
        return productRepo.findAll().stream()
                .filter(product -> product.getCategory().equals("Books") && product.getPrice() > 100)
                .toList();
    }

    public List<Product> exercise1b() {
        return productRepo.findAll().stream()
                .filter(product -> ((BiPredicate<Product, String>) (p, category) -> p.getCategory().equalsIgnoreCase(category))
                        .test(product, "Book")
                        && product.getPrice() > 100)
                .toList();
    }

    public List<Order> exercise2() {
        return orderRepo.findAll().stream()
                .filter(order -> order.getProducts().stream()
                        .anyMatch(product -> product.getCategory().equalsIgnoreCase("Baby")))
                .toList();
    }

    public List<Product> exercise3() {
        return productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Toys"))
                .peek(product -> product.setPrice(product.getPrice() * 0.9))
                .toList();
    }

    public List<Product> exercise4() {
        return orderRepo.findAll().stream()
                .filter(order -> order.getCustomer().getTier() == 2 &&
                        order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)) &&
                        order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1)))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .toList();
    }

    public List<Product> exercise5() {
        return productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .sorted(Comparator.comparing(Product::getPrice))
                .limit(3)
                .toList();

    }

    public List<Order> exercise6() {
        return orderRepo.findAll().stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .toList();
    }


    public List<Product> exercise7() {
        return orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .flatMap(order -> order.getProducts().stream())
                .toList();
    }

    public Double exercise8() {
        return orderRepo.findAll().stream()
                .filter(order ->
                        order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)) &&
                                order.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)))
                .flatMapToDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice))
                .sum();
    }

    public Double exercise8a() {
        return orderRepo.findAll().stream()
                .filter(order ->
                        order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)) &&
                                order.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)))
                .flatMap(order -> order.getProducts().stream())
                .reduce(0D, (accumulator, productRepo) -> accumulator + productRepo.getPrice(), Double::sum);
    }

    public Double exercise9() {
        return orderRepo.findAll().stream()
                .filter(order ->
                        order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .flatMapToDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice))
                .summaryStatistics()
                .getAverage();
    }

    public DoubleSummaryStatistics exercise10() {
        return productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
    }

    public Map<Long, Integer> exercise11() {
        return orderRepo.findAll().stream()
                .collect(Collectors.toMap(Order::getId, order -> order.getProducts().size()));
    }

    public Map<Customer, List<Order>> exercise12() {
        return orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(Order::getCustomer));
    }

    public Map<Long, List<Long>> exercise12a() {
        return orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCustomer().getId(),
                        Collectors.mapping(Order::getId, Collectors.toList())));
    }

    public Map<Order, Double> exercise13() {
        return orderRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum()));
    }

    public Map<Order, Double> exercise13a() {
        return orderRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        order -> order.getProducts().stream().reduce(0D, (accumulator, productRepo) -> accumulator + productRepo.getPrice(), Double::sum)));
    }

    public Map<String, List<String>> exercise14() {
        return productRepo.findAll().stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.mapping(Product::getName, Collectors.toList())));
    }

    public Map<String, Product> exercise15() {
        return productRepo.findAll().stream()
                .collect(Collectors.toMap(Product::getCategory, Function.identity(), BinaryOperator.maxBy(Comparator.comparing(Product::getPrice))));
    }

    public Map<String, String> exercise15a() {
        return productRepo.findAll().stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(Product::getPrice)),
                                product -> product.map(Product::getName).orElse(null))));
    }
}
