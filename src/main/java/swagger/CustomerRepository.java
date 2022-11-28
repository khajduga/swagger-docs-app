package swagger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

	List<Customer> findAllByLastName(String lastName);

	List<Customer> findAllByFirstNameAndLastName(String firstName, String lastName);

}
