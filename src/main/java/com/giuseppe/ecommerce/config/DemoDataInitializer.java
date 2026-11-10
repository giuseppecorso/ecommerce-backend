package com.giuseppe.ecommerce.config;

import com.giuseppe.ecommerce.model.Customer;
import com.giuseppe.ecommerce.model.Order;
import com.giuseppe.ecommerce.model.OrderItem;
import com.giuseppe.ecommerce.model.Product;
import com.giuseppe.ecommerce.model.User;
import com.giuseppe.ecommerce.repository.CustomerRepository;
import com.giuseppe.ecommerce.repository.OrderItemRepository;
import com.giuseppe.ecommerce.repository.OrderRepository;
import com.giuseppe.ecommerce.repository.ProductRepository;
import com.giuseppe.ecommerce.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Configuration
public class DemoDataInitializer {

    // runs on ApplicationReadyEvent, i.e. after every CommandLineRunner,
    // so the demo user created by DataInitializer already exists
    @Bean
    public ApplicationListener<ApplicationReadyEvent> loadDemoData(ProductRepository productRepository,
                                          CustomerRepository customerRepository,
                                          OrderRepository orderRepository,
                                          OrderItemRepository orderItemRepository,
                                          UserRepository userRepository) {
        return event -> {
            if (productRepository.count() == 0) {
                productRepository.saveAll(demoProducts());
            } else {
                addMissingCategories(productRepository);
            }

            if (customerRepository.count() == 0) {
                customerRepository.saveAll(demoCustomers());
            }

            // orders belong to the demo account, so it has to exist already
            Optional<User> demoUser = userRepository.findByUsername("user");
            if (orderRepository.count() == 0 && demoUser.isPresent()) {
                List<Product> products = productRepository.findAll();
                if (products.size() >= 6) {
                    createOrder(orderRepository, orderItemRepository, demoUser.get(), "SHIPPED", 21,
                            products.get(0), 1, products.get(3), 2);
                    createOrder(orderRepository, orderItemRepository, demoUser.get(), "SHIPPED", 9,
                            products.get(5), 1, null, 0);
                    createOrder(orderRepository, orderItemRepository, demoUser.get(), "PAID", 3,
                            products.get(1), 1, products.get(4), 1);
                    createOrder(orderRepository, orderItemRepository, demoUser.get(), "NEW", 0,
                            products.get(2), 3, null, 0);
                }
            }
        };
    }

    // demo products saved before category and image existed get them here
    private void addMissingCategories(ProductRepository productRepository) {
        List<Product> demo = demoProducts();
        for (Product existing : productRepository.findAll()) {
            if (existing.getCategory() != null) {
                continue;
            }
            for (Product d : demo) {
                if (d.getName().equals(existing.getName())) {
                    existing.setCategory(d.getCategory());
                    existing.setImageUrl(d.getImageUrl());
                    productRepository.save(existing);
                }
            }
        }
    }

    private void createOrder(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                             User user, String status, int daysAgo,
                             Product first, int firstQuantity, Product second, int secondQuantity) {
        Order order = new Order();
        order.setUser(user);
        order.setDateOrder(LocalDateTime.now().minusDays(daysAgo));
        order.setStatus(status);
        Order saved = orderRepository.save(order);

        orderItemRepository.save(newItem(saved, first, firstQuantity));
        if (second != null) {
            orderItemRepository.save(newItem(saved, second, secondQuantity));
        }
    }

    private OrderItem newItem(Order order, Product product, int quantity) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        return item;
    }

    private List<Product> demoProducts() {
        return List.of(
                // Desk & Study
                product("Desk & Study", "Brass Pocket Compass", "A tiny brass compass that never lets you lose your way home.", "24.90", 40),
                product("Desk & Study", "Moon Phase Wall Calendar", "Twelve months of moon phases, screen printed on heavy cotton paper.", "18.50", 60),
                product("Desk & Study", "Walnut Pen Tray", "A hand-oiled walnut tray that keeps your favourite pens in one place.", "22.00", 35),
                product("Desk & Study", "Linen Bound Notebook", "160 dotted pages in a linen cover that gets better with age.", "16.00", 80),
                product("Desk & Study", "Hourglass Desk Timer", "Thirty minutes of falling sand for focused, phone-free work.", "19.90", 45),
                product("Desk & Study", "Typewriter Key Bookmarks", "A set of three brass bookmarks inspired by vintage typewriter keys.", "12.50", 70),

                // Home Curiosities
                product("Home Curiosities", "Glass Cloud Terrarium", "A hand-blown glass dome ready for moss, air plants or tiny treasures.", "34.00", 25),
                product("Home Curiosities", "Constellation Night Lamp", "A warm lamp that projects the northern sky on your ceiling.", "42.00", 20),
                product("Home Curiosities", "Wooden Puzzle Box", "A secret compartment box that opens only if you know the right moves.", "32.00", 30),
                product("Home Curiosities", "Storm Glass Barometer", "A sealed glass drop whose crystals change with the weather.", "27.50", 28),
                product("Home Curiosities", "Mini Zen Garden", "Sand, stones and a tiny rake for five quiet minutes a day.", "21.00", 50),
                product("Home Curiosities", "Beeswax Pillar Candle", "A slow-burning candle with a soft honey scent.", "13.50", 90),

                // Kitchen & Table
                product("Kitchen & Table", "Ceramic Cloud Mug", "A stoneware mug shaped like a small cloud, glazed by hand.", "14.00", 75),
                product("Kitchen & Table", "Olive Wood Honey Dipper", "Turned from a single piece of olive wood, no two are alike.", "8.90", 100),
                product("Kitchen & Table", "Speckled Egg Cups", "A pair of speckled ceramic egg cups for slow weekend breakfasts.", "15.50", 55),
                product("Kitchen & Table", "Copper Tea Infuser", "A fine mesh copper infuser for loose leaf tea.", "11.00", 65),
                product("Kitchen & Table", "Enamel Camp Plate", "A white enamel plate with a blue rim, as good at home as outdoors.", "9.50", 85),
                product("Kitchen & Table", "Cork Trivet Set", "Three hexagonal cork trivets that fit together like a honeycomb.", "17.00", 40),

                // Gifts & Games
                product("Gifts & Games", "Pocket Kaleidoscope", "A brass kaleidoscope small enough for a coat pocket.", "19.00", 48),
                product("Gifts & Games", "Wooden Domino Set", "Twenty-eight beech dominoes in a sliding lid box.", "26.00", 32),
                product("Gifts & Games", "Magnetic Travel Chess", "A fold-flat magnetic chess set for trains, planes and park benches.", "23.50", 38),
                product("Gifts & Games", "Origami Paper Collection", "120 sheets of patterned paper with instructions for ten models.", "10.50", 95),
                product("Gifts & Games", "Brass Spinning Top", "A precision turned top that spins for over three minutes.", "14.90", 60),
                product("Gifts & Games", "Mystery Seed Packets", "Five unlabelled seed packets. Plant them and see what grows.", "7.50", 120),

                // Travel Keepsakes
                product("Travel Keepsakes", "Scratch-Off World Map", "Scratch away every country you visit to reveal the colours below.", "28.00", 42),
                product("Travel Keepsakes", "Leather Luggage Tag", "A vegetable tanned leather tag that darkens with every trip.", "13.00", 70),
                product("Travel Keepsakes", "Folding Pocket Knife", "A beech handle pocket knife for picnics and long walks.", "29.90", 26),
                product("Travel Keepsakes", "Tin Travel Candle", "A cedar scented candle in a tin that closes for the road.", "11.50", 88),
                product("Travel Keepsakes", "Canvas Postcard Wallet", "A waxed canvas wallet that holds twenty postcards and a pencil.", "18.00", 36),
                product("Travel Keepsakes", "Vintage Style Field Flask", "A stainless steel flask with a canvas sleeve and a cork stopper.", "31.00", 0)
        );
    }

    private Product product(String category, String name, String description, String price, int stockQuantity) {
        Product product = new Product(null, name, description, new BigDecimal(price), stockQuantity);
        product.setCategory(category);
        product.setImageUrl(imageFor(name));
        return product;
    }

    // "Brass Pocket Compass" -> /img/products/brass-pocket-compass.svg (served by the frontend)
    private String imageFor(String name) {
        return "/img/products/" + name.toLowerCase().replaceAll("[^a-z0-9]+", "-") + ".svg";
    }

    private List<Customer> demoCustomers() {
        return List.of(
                new Customer(null, "Olivia", "Bennett", "olivia.bennett@example.com"),
                new Customer(null, "James", "Whitaker", "james.whitaker@example.com"),
                new Customer(null, "Amelia", "Clarke", "amelia.clarke@example.com"),
                new Customer(null, "Noah", "Fletcher", "noah.fletcher@example.com"),
                new Customer(null, "Isla", "Morgan", "isla.morgan@example.com"),
                new Customer(null, "Ethan", "Hughes", "ethan.hughes@example.com"),
                new Customer(null, "Sophie", "Lambert", "sophie.lambert@example.com"),
                new Customer(null, "Lucas", "Reid", "lucas.reid@example.com"),
                new Customer(null, "Grace", "Holloway", "grace.holloway@example.com"),
                new Customer(null, "Oscar", "Pryce", "oscar.pryce@example.com"),
                new Customer(null, "Chloe", "Sinclair", "chloe.sinclair@example.com"),
                new Customer(null, "Henry", "Marlowe", "henry.marlowe@example.com")
        );
    }
}
