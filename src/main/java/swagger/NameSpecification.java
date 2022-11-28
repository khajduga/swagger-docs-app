package swagger;


import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Conjunction(value = {
		@Or({
				@Spec(path = "lastName", params = "lastName", spec = Like.class)
		})},
		and = @Spec(path = "firstName", params = "firstName", spec = Equal.class))
public interface NameSpecification extends Specification<Customer> {
}
