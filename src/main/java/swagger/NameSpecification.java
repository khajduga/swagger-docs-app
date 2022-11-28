package swagger;


import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Or({
	@Spec(path = "firstName", params = "firstName", spec = Like.class),
	@Spec(path = "lastName", params = "lastName", spec = Like.class),
})
public interface NameSpecification extends Specification<Customer> {
}
