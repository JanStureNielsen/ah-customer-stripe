package ah.customer.stripe.controller;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;
import static ah.helper.HelperProduct.buildProductCollectionResponse;
import static ah.helper.HelperProduct.buildProductResponse;
import static ah.helper.HelperProduct.productCreate;
import static ah.helper.HelperProduct.productDelete;
import static ah.helper.HelperProduct.productGet;
import static ah.helper.HelperProduct.productInactivate;
import static ah.helper.HelperProduct.productUpdate;
import static ah.helper.HelperProduct.productsGet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.model.Product;

import ah.config.StripeConfig;
import ah.rest.AhResponse;

@RestController
@RequestMapping("/api/v1/products")
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
