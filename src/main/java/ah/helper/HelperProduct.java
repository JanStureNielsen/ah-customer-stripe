package ah.helper;

import static ah.customer.stripe.StripeParam.PRODUCT_CREATE;
import static ah.customer.stripe.StripeParam.PRODUCT_LIST;
import static ah.customer.stripe.StripeParam.PRODUCT_UPDATE;
import static ah.helper.StripeHelper.inactive;
import static ah.helper.StripeHelper.runReturnOrThrow;
import static ah.rest.AhResponse.buildOk;
import static ah.rest.AhResponse.internalError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductListParams;
import com.stripe.param.ProductUpdateParams;

import ah.rest.AhResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelperProduct {
    private HelperProduct() {
    }

    public static ProductCollection productsGet(String productListParamsString) {
        return runReturnOrThrow(() -> Product.list((ProductListParams) PRODUCT_LIST.fromJson(productListParamsString)),
                "Products fetch failed for '%s'", productListParamsString);
    }

    public static Product productGet(String productCid) {
        return runReturnOrThrow(() -> Product.retrieve(productCid), "Product fetch failed for '%s'", productCid);
    }

    public static Product productCreate(String productCreateParamString) {
        return runReturnOrThrow(() -> Product.create((ProductCreateParams) PRODUCT_CREATE.fromJson(productCreateParamString)),
                "Product create failed for '%s'", productCreateParamString);
    }

    public static Product productUpdate(String productCid, String productUpdateParamString) {
        return runReturnOrThrow(() -> {
                    final Product existingProduct = Product.retrieve(productCid);
                    return existingProduct.update((ProductUpdateParams) PRODUCT_UPDATE.fromJson(productUpdateParamString));
                },
                "Product update failed for '%s'", productUpdateParamString);
    }

    public static Product productInactivate(String productCid) {
        return runReturnOrThrow(() -> {
                    final Product existingProduct = Product.retrieve(productCid);
                    return existingProduct.update(inactive);
                },
                "Product inactivate failed for '%s'", productCid);
    }

    public static Product productDelete(String productCid) {
        return runReturnOrThrow(() -> {
                    final Product existingProduct = Product.retrieve(productCid);
                    return existingProduct.delete();
                },
                "Product delete failed for '%s'", productCid);
    }

    public static ResponseEntity<AhResponse<Product>> buildProductResponse(Product product, String msg) {
        final StripeResponse lastResponse = product.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Product fetchedProduct = StripeHelper.jsonToObject(lastResponse.body(), Product.class);
            return buildOk(fetchedProduct);
        }
        return internalError(msg, lastResponse.code(), product);
    }

    public static ResponseEntity<AhResponse<Product>> buildProductCollectionResponse(ProductCollection productCollection) {
        try {
            final StripeResponse lastResponse = productCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return buildOk(productCollection.getData());
            }
            final String errMsg = String.format("Error getting products : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(productCollection));
            log.error(errMsg);
            return internalError(errMsg);
        } catch (Exception e) {
            log.error("Error Fetching Product.", e);
            return internalError(e);
        }
    }
}