package swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/result")
public class ResultController {

	private static final Logger log = LoggerFactory.getLogger(ResultController.class);

	private final CustomerRepository customerRepository;

	public ResultController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getResult(@RequestParam(value = "firstName", required = false) String firstName,
	                        @RequestParam("lastName") String lastName) {
		log.info("Fetching result");

		if (Objects.isNull(firstName)) {
			return customerRepository.findAllByLastName(lastName);
		}
		return customerRepository.findAllByFirstNameAndLastName(firstName, lastName);
	}

	@GetMapping(value = "/v2", produces = MediaType.APPLICATION_JSON_VALUE, params = {"lastName"})
	public Object getResultWithSpec(NameSpecification specification) {

		log.info("Fetching result with specification: {}", specification.toString());
		return customerRepository.findAll(specification);
	}

}
