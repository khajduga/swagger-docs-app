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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

					// Obtain the list of joins of the specification
					Map<String, String> joinMap = new HashMap<>();
					for (Annotation anno : methodParameter.getParameterType().getAnnotations()) {
						if (anno.annotationType() == Join.class) {
							joinMap.put(((Join) anno).alias(), ((Join) anno).path());
						} else if (anno.annotationType() == RepeatedJoin.class) {
							for (Join join : ((RepeatedJoin) anno).value()) {
								joinMap.put(join.alias(), join.path());
							}
						}
					}

					// Create doc param from annotations
					for (Annotation anno : methodParameter.getParameterType().getAnnotations()) {
						if (anno.annotationType() == Spec.class) {
							createParameter((Spec) anno, joinMap, requiredParameters).forEach(operation::addParametersItem);
						}

						if (annotationContainsNestedSpecification(anno)) {
							for (Spec spec : ((Or) anno).value()) {
								createParameter(spec, joinMap, requiredParameters).forEach(operation::addParametersItem);
							}
						}
					}
				}
			}
		}

		return operation;
	}

	private List<String> extractRequiredParametersFromHandlerMethod(HandlerMethod handlerMethod) {
		return ofNullable(handlerMethod.getMethodAnnotation(RequestMapping.class))
				.map(RequestMapping::params)
				.map(List::of)
				.orElse(emptyList());
	}

	private boolean annotationContainsNestedSpecification(Annotation annotation) {
		return annotation.annotationType() == And.class || annotation.annotationType() == Or.class;
	}

	private List<Parameter> createParameter(Spec spec, Map<String, String> joinMap, List<String> requiredParameters) {
		List<Parameter> result = new ArrayList<>();

		Schema<String> paramSchema = new Schema<>();
		paramSchema.setType("string");
		paramSchema.setDefault(null);

		for (String paramName : spec.params()) {

			// Get the alias if any join exists, perform while loop for nested loops
			String path = spec.path();
			while (path.contains(".")) {
				String[] splitStr = path.split("\\.");
				path = joinMap.get(splitStr[0]) + ":" + splitStr[1];
			}

			Parameter specificationParam = new Parameter();
			specificationParam.setName(paramName);
			specificationParam.setDescription(
					String.format("Will search for parameter %s using matching method: %s", path, spec.spec().getSimpleName()));
			specificationParam.setRequired(requiredParameters.contains(path)); // if request param annotation contains specified required parameters
			specificationParam.setIn("query");
			specificationParam.setSchema(paramSchema);

			result.add(specificationParam);
		}
		return result;
	}
}