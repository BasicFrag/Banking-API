package br.com.ibm.bankrestapi.openapi;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(info = @Info(description = "API que simula operações bancárias através de alguns endpoints e  seus repectivos verbos HTTP", title = "Dorn's Bank API",version = "1.0"))
public class BankApplication extends Application {
}
