package swagger;

import net.kaczmarzyk.spring.data.jpa.swagger.springdoc.SpecificationArgResolverSpringdocOperationCustomizer;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new SpecificationArgumentResolver());
	}

	@Bean
	public SpecificationArgResolverSpringdocOperationCustomizer specificationArgResolverOperationCustomizer() {
		return new SpecificationArgResolverSpringdocOperationCustomizer();
	}
}
