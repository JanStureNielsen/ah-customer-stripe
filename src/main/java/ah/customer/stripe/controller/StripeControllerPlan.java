package ah.customer.stripe.controller;

import ah.config.StripeConfig;
import ah.helper.StripeHelper;
import com.stripe.Stripe;
import com.stripe.model.Plan;
import com.stripe.model.PlanCollection;
import com.stripe.net.StripeResponse;
import com.stripe.param.PlanCreateParams;
import com.stripe.param.PlanListParams;
import com.stripe.param.PlanUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ah.helper.AhConstant.STRIPE_REST_LARGE_LIMIT;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class StripeControllerPlan {

    @Autowired
    public StripeControllerPlan(StripeConfig config) {
        Stripe.apiKey = config.stripeSecretKey();
    }

    @GetMapping("/plans/all")
    public ResponseEntity<List<Plan>> getPlans() {
        return getPlans(STRIPE_REST_LARGE_LIMIT);
    }

    @GetMapping("/plans")
    public ResponseEntity<List<Plan>> getPlans(@RequestBody String planListParamsString) {
        try {
            final PlanListParams planListParams = StripeHelper.getGson().fromJson(planListParamsString, PlanListParams.class);
            final PlanCollection planCollection = Plan.list(planListParams);

            final StripeResponse lastResponse = planCollection.getLastResponse();
            if (lastResponse.code() == HttpStatus.OK.value()) {
                return ResponseEntity.ok().body(planCollection.getData());
            }
            log.error(String.format("Error getting plans : Code %d \n%s", lastResponse.code(),
                    StripeHelper.objectToJson(planCollection)));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) {
            log.error("Error Fetching Plan.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/plan/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable("id") String planCid) {
        try {
            final Plan plan = Plan.retrieve(planCid);
            return buildStripeResponsePlan(plan, "Error fetching Plan");
        } catch (Exception e) {
            log.error("Error Fetching Plan.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/plan")
    public ResponseEntity<Plan> createPlan(@RequestBody String planCreateParamString) {
        try {
            final PlanCreateParams planCreateParams = StripeHelper.getGson().fromJson(planCreateParamString, PlanCreateParams.class);
            final Plan planNew = Plan.create(planCreateParams);
            return buildStripeResponsePlan(planNew, "Error Creating Plan");
        } catch (Exception e) {
            log.error("Error Creating Plan.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/plan/{id}")
    public ResponseEntity<Plan> updatePlan(@PathVariable("id") String planCid, @RequestBody String planUpdateParamString) {

        try {
            final PlanUpdateParams planUpdateParams = StripeHelper.getGson().fromJson(planUpdateParamString, PlanUpdateParams.class);
            final Plan existingPlan = Plan.retrieve(planCid);
            final Plan updatedPlan = existingPlan.update(planUpdateParams);
            return buildStripeResponsePlan(updatedPlan, "Error Updating Plan");
        } catch (Exception e) {
            log.error("Error Updating Plan.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/plan/{id}")
    public ResponseEntity<Plan> deletePlan(@PathVariable("id") String planCid) {
        try {
            final Plan plan = Plan.retrieve(planCid);
            final Plan deletedPlan = plan.delete();
            return buildStripeResponsePlan(deletedPlan, "Error Plan.");
        } catch (Exception e) {
            log.error("Error Removing Plan.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Plan> buildStripeResponsePlan(Plan plan, String msg) {
        final StripeResponse lastResponse = plan.getLastResponse();
        if (lastResponse.code() == HttpStatus.OK.value()) {
            final Plan fetchedPlan = StripeHelper.jsonToObject(lastResponse.body(), Plan.class);
            return ResponseEntity.ok().body(fetchedPlan);
        }
        log.error(String.format("%s (alsk) : Code %d \n%s", msg, lastResponse.code(), StripeHelper.objectToJson(plan)));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
