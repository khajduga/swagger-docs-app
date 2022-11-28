package swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import net.kaczmarzyk.spring.data.jpa.web.annotation.*;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

public class SpecificationArgResolverSpringdocOperationCustomizer implements OperationCustomizer {

	static {
		SpringDocUtils.getConfig().addRequestWrapperToIgnore(Specification.class);
	}

	@Override
	public Operation customize(Operation operation, HandlerMethod handlerMethod) {
		if (isNull(operation) || isNull(handlerMethod)) return operation;

		List<String> requiredParameters = extractRequiredParametersFromHandlerMethod(handlerMethod);

		for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
			for (Class<?> cls : methodParameter.getParameterType().getInterfaces()) {
				if (cls == Specification.class) {

					for (Annotation anno : methodParameter.getParameterType().getAnnotations()) {
						if (anno.annotationType() == Spec.class) {
							createParameter((Spec) anno, requiredParameters).forEach(operation::addParametersItem);
						}

						if (annotationContainsNestedSpecification(anno)) {
							for (Spec spec : ((Or) anno).value()) {
								createParameter(spec, requiredParameters).forEach(operation::addParametersItem);
							}
						}
					}
				}
			}
		}

		return operation;
	}

	private List<Parameter> createParameter(Spec spec, List<String> requiredParameters) {
		Schema<String> defaultParamSchema = new Schema<>();
		defaultParamSchema.setType("string");

		return stream(spec.params())
				.map(parameterName -> {
					Parameter specificationParam = new Parameter();
					specificationParam.setName(parameterName);
					specificationParam.setRequired(requiredParameters.contains(spec.path()));
					specificationParam.setIn("query");
					specificationParam.setSchema(defaultParamSchema);

					return specificationParam;
				})
				.collect(Collectors.toList());
	}

	private List<String> extractRequiredParametersFromHandlerMethod(HandlerMethod handlerMethod) {
		return ofNullable(handlerMethod.getMethodAnnotation(RequestMapping.class))
				.map(RequestMapping::params)
				.map(List::of)
				.orElse(emptyList());
	}

	private boolean annotationContainsNestedSpecification(Annotation annotation) {
		return annotation.annotationType() == Conjunction.class ||
				annotation.annotationType() == Disjunction.class ||
				annotation.annotationType() == And.class ||
				annotation.annotationType() == Or.class;
	}

}