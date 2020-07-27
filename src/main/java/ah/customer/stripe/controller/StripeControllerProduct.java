package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.rest.AhResponse;
import com.stripe.Stripe;
import com.stripe.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperProduct.*;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class StripeControllerProduct {

    @Autowired
    public StripeControllerProduct(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/all")
    public ResponseEntity<AhResponse<Product>> getProducts() {
        return getProducts(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/")
    public ResponseEntity<AhResponse<Product>> getProducts(@RequestBody String productListParamsString) {
        return buildProductCollectionResponse(productsGet(productListParamsString));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhResponse<Product>> getProduct(@PathVariable("id") String productCid) {
        return buildProductResponse(productGet(productCid), "Error fetching product.");
    }

    @PostMapping("/")
    public ResponseEntity<AhResponse<Product>> createProduct(@RequestBody String productCreateParamString) {
        return buildProductResponse(productCreate(productCreateParamString), "Error creating product.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhResponse<Product>> updateProduct(@PathVariable("id") String productCid, @RequestBody String productUpdateParamString) {
        return buildProductResponse(productUpdate(productCid, productUpdateParamString), "Error updating product.");
    }

    @PutMapping("/inactive/{id}")
    public ResponseEntity<AhResponse<Product>> inactivateProduct(@PathVariable("id") String productCid) {
        return buildProductResponse(productInactivate(productCid), "Error inactivating product.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AhResponse<Product>> deleteProduct(@PathVariable("id") String productCid) {
        return buildProductResponse(productDelete(productCid), "Error deleting product.");
    }
}
