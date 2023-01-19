package swagger;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

	private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

	private final CustomerRepository customerRepository;

	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@GetMapping(path = "/params", produces = MediaType.APPLICATION_JSON_VALUE, params = { "lastName" })
	public Object getCustomersByParams(@Or({
		@Spec(path = "firstName", params = "firstName", spec = Like.class),
		@Spec(path = "lastName", params = "lastName", spec = Like.class),
		@Spec(path = "customId", params = "customId", spec = Equal.class),
	}) Specification<Customer> specification) {

		log.info("Fetching result using parameters from specification: {}", specification.toString());
		return customerRepository.findAll(specification);
	}

	@GetMapping(path = "/pathVars/{firstName}/{lastName}/{customId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getCustomersByPathVars(@Or({
		@Spec(path = "firstName", pathVars = "firstName", spec = Like.class),
		@Spec(path = "lastName", pathVars = "lastName", spec = Like.class),
		@Spec(path = "customId", pathVars = "customId", spec = Equal.class),
	}) Specification<Customer> specification) {

		log.info("Fetching result using path variables from specification: {}", specification.toString());
		return customerRepository.findAll(specification);
	}

	@GetMapping(path = "/headers", produces = MediaType.APPLICATION_JSON_VALUE, headers = { "lastName" })
	public Object getCustomersByHeaders(@Or({
		@Spec(path = "firstName", headers = "firstName", spec = Like.class),
		@Spec(path = "lastName", headers = "lastName", spec = Like.class),
		@Spec(path = "customId", headers = "customId", spec = Equal.class),
	}) Specification<Customer> specification) {

		log.info("Fetching result using headers from specification: {}", specification.toString());
		return customerRepository.findAll(specification);
	}

	@GetMapping(value = "/jsonPath", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Customer> getCustomersByJsonPath(@Or({
		@Spec(path = "firstName", jsonPaths = "biographicData.firstName", spec = Like.class),
		@Spec(path = "lastName", jsonPaths = "biographicData.lastName", spec = Like.class),
		@Spec(path = "customId", jsonPaths = "customId", spec = Equal.class),
	}) Specification<Customer> specification) {

		log.info("Fetching result using json body from specification: {}", specification.toString());
		return customerRepository.findAll(specification);
	}

}
