package com.ecommerce.reglog.config;

import com.ecommerce.reglog.entity.*;
import com.ecommerce.reglog.enums.Gender;
import com.ecommerce.reglog.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final ProductVariantRepository variantRepository;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ProductImageRepository imageRepository,
                           ProductVariantRepository variantRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.variantRepository = variantRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0 && productRepository.count() >= 10) {
            logger.info("Database already seeded with categories and products.");
            return;
        }

        logger.info("Seeding database with e-commerce categories and products...");

        // 1. Seed Categories
        Map<String, Category> categories = new LinkedHashMap<>();

        createOrGetCategory(categories, "Men's Fashion", "mens-fashion",
                "Trendy menswear, classic suits, denim, activewear and luxury essentials.",
                "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?q=80&w=800&auto=format&fit=crop", 1);

        createOrGetCategory(categories, "Women's Fashion", "womens-fashion",
                "Elegant dresses, western attire, ethnic wear and premium silhouettes.",
                "https://images.unsplash.com/photo-1483985988355-763728e1935b?q=80&w=800&auto=format&fit=crop", 2);

        createOrGetCategory(categories, "Electronics", "electronics",
                "High-performance laptops, noise-canceling headphones, smart devices & sound systems.",
                "https://images.unsplash.com/photo-1550009158-9ebf69173e03?q=80&w=800&auto=format&fit=crop", 3);

        createOrGetCategory(categories, "Mobile Phones", "mobile-phones",
                "Next-generation smartphones, flagship 5G devices and mobile power accessories.",
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=800&auto=format&fit=crop", 4);

        createOrGetCategory(categories, "Shoes", "shoes",
                "Running sneakers, formal leather shoes, athletic trainers and casual loafers.",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=800&auto=format&fit=crop", 5);

        createOrGetCategory(categories, "Watches", "watches",
                "Swiss chronographs, luxury automatic timepieces and precision smartwatches.",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=800&auto=format&fit=crop", 6);

        createOrGetCategory(categories, "Accessories", "accessories",
                "Designer sunglasses, leather wallets, belts and statement jewelry.",
                "https://images.unsplash.com/photo-1511556532299-8f662fc26c06?q=80&w=800&auto=format&fit=crop", 7);

        createOrGetCategory(categories, "Home & Kitchen", "home-kitchen",
                "Modern home aesthetics, smart kitchen appliances and artisanal cookware.",
                "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?q=80&w=800&auto=format&fit=crop", 8);

        createOrGetCategory(categories, "Beauty Products", "beauty-products",
                "Luxury skincare, dermatologically tested serums, makeup and fragrances.",
                "https://images.unsplash.com/photo-1596462502278-27bfdc403348?q=80&w=800&auto=format&fit=crop", 9);

        createOrGetCategory(categories, "Kids", "kids",
                "Comfortable kids wear, organic cotton clothing, playful footwear and toys.",
                "https://images.unsplash.com/photo-1514090458221-65bb69cf63e6?q=80&w=800&auto=format&fit=crop", 10);

        // 2. Seed Rich Products
        seedProducts(categories);
        logger.info("Category and Product seeding completed successfully.");
    }

    private void createOrGetCategory(Map<String, Category> map, String name, String slug, String desc, String img, int order) {
        Category cat = categoryRepository.findBySlug(slug).orElseGet(() -> {
            Category c = new Category(name, slug, desc, img, order);
            return categoryRepository.save(c);
        });
        map.put(slug, cat);
    }

    private void seedProducts(Map<String, Category> cat) {
        if (productRepository.count() >= 10) return;

        // 1. Men's Fashion
        createProduct(
                "AURA Slim-Fit Italian Merino Wool Blazer",
                "aura-merino-wool-blazer",
                "Handcrafted from super 130s Italian Merino wool, this single-breasted blazer combines modern tailoring with exceptional breathability. Features natural horn buttons, pick-stitched lapels, and double back vents.",
                "Armani Exchange",
                cat.get("mens-fashion"),
                new BigDecimal("14999.00"),
                new BigDecimal("8999.00"),
                35,
                Gender.MEN,
                4.8,
                142,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1507679799987-c73779587ccf?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("M", "Navy Blue", "#1e3a8a", 15),
                        new VariantDef("L", "Navy Blue", "#1e3a8a", 12),
                        new VariantDef("XL", "Charcoal Black", "#1f2937", 8)
                )
        );

        createProduct(
                "Levi's Original 501 Straight Fit Denim Jeans",
                "levis-501-straight-fit-jeans",
                "The timeless blueprint for denim crafted from 100% heavyweight cotton. Features classic button fly, iconic 5-pocket styling and signature copper rivets.",
                "Levi's",
                cat.get("mens-fashion"),
                new BigDecimal("4999.00"),
                new BigDecimal("3499.00"),
                60,
                Gender.MEN,
                4.7,
                389,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1542272604-787c3835535d?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1582552938357-32b906df40cb?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("30", "Indigo Blue", "#1d4ed8", 20),
                        new VariantDef("32", "Indigo Blue", "#1d4ed8", 25),
                        new VariantDef("34", "Black", "#111827", 15)
                )
        );

        // 2. Women's Fashion
        createProduct(
                "Zara Satin Drape Maxi Evening Dress",
                "zara-satin-drape-maxi-dress",
                "A fluid silhouette with cowl neckline, adjustable criss-cross back straps, and subtle side slit. Cut on the bias for an effortless drape that flatters every frame.",
                "Zara",
                cat.get("womens-fashion"),
                new BigDecimal("6990.00"),
                new BigDecimal("4490.00"),
                45,
                Gender.WOMEN,
                4.9,
                210,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1595777457583-95e059d581b8?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("S", "Emerald Green", "#047857", 15),
                        new VariantDef("M", "Emerald Green", "#047857", 15),
                        new VariantDef("M", "Ruby Red", "#b91c1c", 15)
                )
        );

        createProduct(
                "H&M Cashmere Blend Ribbed Knit Sweater",
                "hm-cashmere-blend-sweater",
                "Ultra-soft fine knit crafted with 20% recycled cashmere and 80% responsible wool. Features dropped shoulders and ribbed cuffs.",
                "H&M",
                cat.get("womens-fashion"),
                new BigDecimal("5999.00"),
                new BigDecimal("3999.00"),
                50,
                Gender.WOMEN,
                4.6,
                98,
                false,
                List.of(
                        "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("S", "Cream Beige", "#fef3c7", 20),
                        new VariantDef("M", "Cream Beige", "#fef3c7", 20),
                        new VariantDef("L", "Charcoal Grey", "#4b5563", 10)
                )
        );

        // 3. Electronics
        createProduct(
                "Sony WH-1000XM5 Wireless Noise Cancelling Headphones",
                "sony-wh-1000xm5-wireless-headphones",
                "Industry-leading active noise cancellation with 8 microphones and Auto NC Optimizer. High-Resolution Audio with 30-hour battery life and speak-to-chat smarts.",
                "Sony",
                cat.get("electronics"),
                new BigDecimal("34990.00"),
                new BigDecimal("26990.00"),
                40,
                Gender.UNISEX,
                4.9,
                875,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1583394838336-acd977736f90?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("One Size", "Midnight Black", "#111827", 25),
                        new VariantDef("One Size", "Silver", "#e5e7eb", 15)
                )
        );

        createProduct(
                "Apple MacBook Air 15-inch M3 Chip",
                "apple-macbook-air-15-m3",
                "Incredibly thin with brilliant Liquid Retina display, powered by Apple M3 8-core CPU and 10-core GPU. Up to 18 hours of battery life and silent fanless design.",
                "Apple",
                cat.get("electronics"),
                new BigDecimal("134900.00"),
                new BigDecimal("124900.00"),
                20,
                Gender.UNISEX,
                5.0,
                420,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("16GB/512GB", "Space Grey", "#374151", 10),
                        new VariantDef("16GB/512GB", "Midnight", "#0f172a", 10)
                )
        );

        // 4. Mobile Phones
        createProduct(
                "Samsung Galaxy S24 Ultra 5G AI Smartphone",
                "samsung-galaxy-s24-ultra-5g",
                "Titanium frame with integrated S Pen, Snapdragon 8 Gen 3 processor, Galaxy AI photo editing, and pro-grade 200MP Quad Telephoto optical camera system.",
                "Samsung",
                cat.get("mobile-phones"),
                new BigDecimal("129999.00"),
                new BigDecimal("114999.00"),
                30,
                Gender.UNISEX,
                4.8,
                640,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("256GB", "Titanium Gray", "#6b7280", 15),
                        new VariantDef("512GB", "Titanium Black", "#111827", 15)
                )
        );

        createProduct(
                "Apple iPhone 15 Pro Max 256GB",
                "apple-iphone-15-pro-max",
                "Forged in titanium with aerospace-grade strength, revolutionary A17 Pro chip, customisable Action button, and 5x optical zoom telephoto camera.",
                "Apple",
                cat.get("mobile-phones"),
                new BigDecimal("159900.00"),
                new BigDecimal("148900.00"),
                25,
                Gender.UNISEX,
                4.9,
                1120,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("256GB", "Natural Titanium", "#9ca3af", 15),
                        new VariantDef("512GB", "Blue Titanium", "#1e3a8a", 10)
                )
        );

        // 5. Shoes
        createProduct(
                "Nike Air Jordan 1 Retro High OG",
                "nike-air-jordan-1-retro-high",
                "Iconic hoops heritage in genuine full-grain leather with encapsulated Nike Air cushioning, signature padded collar, and durable rubber cupsole with pivot circle.",
                "Nike",
                cat.get("shoes"),
                new BigDecimal("16995.00"),
                new BigDecimal("13495.00"),
                45,
                Gender.UNISEX,
                4.9,
                730,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("UK 8", "University Red", "#dc2626", 15),
                        new VariantDef("UK 9", "University Red", "#dc2626", 15),
                        new VariantDef("UK 10", "Classic Black", "#000000", 15)
                )
        );

        createProduct(
                "Adidas Ultraboost Light Running Shoes",
                "adidas-ultraboost-light",
                "Lightest BOOST ever with Continental Better Rubber outsole. Engineered Primeknit+ upper ensures snug foot-hugging precision and explosive energy return.",
                "Adidas",
                cat.get("shoes"),
                new BigDecimal("18999.00"),
                new BigDecimal("12999.00"),
                50,
                Gender.MEN,
                4.7,
                310,
                false,
                List.of(
                        "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("UK 8", "Core Black", "#111827", 20),
                        new VariantDef("UK 9", "Cloud White", "#ffffff", 20),
                        new VariantDef("UK 10", "Core Black", "#111827", 10)
                )
        );

        // 6. Watches
        createProduct(
                "Fossil Neutra Minimalist Chronograph Watch",
                "fossil-neutra-chronograph-watch",
                "Mid-century architecture inspired design with genuine amber leather strap, polished stainless steel 44mm casing, stop-watch stopwatch subdials and 5 ATM water resistance.",
                "Fossil",
                cat.get("watches"),
                new BigDecimal("13495.00"),
                new BigDecimal("8995.00"),
                40,
                Gender.MEN,
                4.7,
                245,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1524805444758-089113d48a6d?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("44mm", "Brown Leather", "#78350f", 20),
                        new VariantDef("44mm", "Black Stainless Steel", "#1f2937", 20)
                )
        );

        createProduct(
                "Apple Watch Series 9 GPS + Cellular 45mm",
                "apple-watch-series-9-gps-cellular",
                "Super-bright Always-On Retina display with S9 SiP chip, double tap gesture interaction, advanced ECG heart sensors, cycle tracking and crash detection.",
                "Apple",
                cat.get("watches"),
                new BigDecimal("54900.00"),
                new BigDecimal("47900.00"),
                35,
                Gender.UNISEX,
                4.9,
                512,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("45mm", "Midnight Aluminum", "#0f172a", 20),
                        new VariantDef("45mm", "Starlight Aluminum", "#fef3c7", 15)
                )
        );

        // 7. Accessories
        createProduct(
                "Ray-Ban Aviator Classic Polarized Sunglasses",
                "ray-ban-aviator-classic-polarized",
                "Originally designed for U.S. aviators in 1937. Legendary teardrop shape in lightweight gold steel frame with crystal green G-15 polarized lenses providing 100% UV protection.",
                "Ray-Ban",
                cat.get("accessories"),
                new BigDecimal("11590.00"),
                new BigDecimal("8690.00"),
                55,
                Gender.UNISEX,
                4.8,
                430,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1572635196237-14b3f281503f?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("Standard 58mm", "Gold Frame / Green Lens", "#ca8a04", 30),
                        new VariantDef("Large 62mm", "Black Frame / Grey Lens", "#111827", 25)
                )
        );

        // 8. Home & Kitchen
        createProduct(
                "Nespresso Vertuo Pop Coffee and Espresso Machine",
                "nespresso-vertuo-pop-coffee-machine",
                "Patented Centrifusion technology spins capsule up to 7,000 rotations per minute to gently extract every drop of flavor topped with rich velvety crema.",
                "Nespresso",
                cat.get("home-kitchen"),
                new BigDecimal("16999.00"),
                new BigDecimal("12499.00"),
                28,
                Gender.UNISEX,
                4.7,
                198,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("Standard", "Liquorice Black", "#000000", 15),
                        new VariantDef("Standard", "Pacific Blue", "#0284c7", 13)
                )
        );

        // 9. Beauty Products
        createProduct(
                "Estée Lauder Advanced Night Repair Synchronized Multi-Recovery Serum",
                "estee-lauder-advanced-night-repair-serum",
                "Deep-penetrating fast repair serum with patented Chronolux Power Signal Technology. Delivers 72-hour hydration, 8-hour antioxidant protection and visible firmness.",
                "Estée Lauder",
                cat.get("beauty-products"),
                new BigDecimal("9200.00"),
                new BigDecimal("7360.00"),
                45,
                Gender.WOMEN,
                4.9,
                820,
                true,
                List.of(
                        "https://images.unsplash.com/photo-1608248597359-598d9e761dfa?q=80&w=800&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1596462502278-27bfdc403348?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("50ml", "Amber Glass", "#92400e", 25),
                        new VariantDef("75ml", "Amber Glass", "#92400e", 20)
                )
        );

        // 10. Kids
        createProduct(
                "Polo Ralph Lauren Kids Striped Cotton Mesh Polo Shirt",
                "polo-ralph-lauren-kids-cotton-polo",
                "Crafted from 100% breathable cotton mesh with signature embroidered Pony on left chest, ribbed polo collar and tennis tail hem.",
                "Ralph Lauren",
                cat.get("kids"),
                new BigDecimal("4990.00"),
                new BigDecimal("3490.00"),
                35,
                Gender.KIDS,
                4.8,
                115,
                false,
                List.of(
                        "https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?q=80&w=800&auto=format&fit=crop"
                ),
                List.of(
                        new VariantDef("4-5 Yrs", "Navy / White Stripe", "#1e3a8a", 15),
                        new VariantDef("6-7 Yrs", "Navy / White Stripe", "#1e3a8a", 10),
                        new VariantDef("8-9 Yrs", "Navy / White Stripe", "#1e3a8a", 10)
                )
        );
    }

    private void createProduct(String name, String slug, String description, String brand,
                               Category category, BigDecimal originalPrice, BigDecimal sellingPrice,
                               int stock, Gender gender, double rating, int reviews, boolean isFeatured,
                               List<String> imageUrls, List<VariantDef> variants) {
        Product product = new Product();
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(description);
        product.setBrand(brand);
        product.setCategory(category);
        product.setOriginalPrice(originalPrice);
        product.setSellingPrice(sellingPrice);
        product.setCostPrice(sellingPrice.multiply(BigDecimal.valueOf(0.55)));
        product.setAvailableStock(stock);
        product.setGender(gender);
        product.setRating(rating);
        product.setReviewsCount(reviews);
        product.setIsFeatured(isFeatured);
        product.setIsActive(true);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        product.calculateDiscount();

        Product savedProduct = productRepository.save(product);

        // Save images
        int order = 0;
        for (String url : imageUrls) {
            ProductImage img = new ProductImage(savedProduct, url, order == 0, order++);
            imageRepository.save(img);
        }

        // Save variants
        for (VariantDef v : variants) {
            ProductVariant variant = new ProductVariant(savedProduct, v.size, v.color, v.colorHex, v.stock);
            variant.setSku(brand.toUpperCase().replace(" ", "") + "-" + v.size + "-" + v.color.toUpperCase().replace(" ", ""));
            variantRepository.save(variant);
        }
    }

    private static class VariantDef {
        String size;
        String color;
        String colorHex;
        int stock;

        VariantDef(String size, String color, String colorHex, int stock) {
            this.size = size;
            this.color = color;
            this.colorHex = colorHex;
            this.stock = stock;
        }
    }
}
