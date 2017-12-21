package mydatachoice.icd;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IcdApplication {

	public static void main(String[] args) {
		SpringApplication.run(IcdApplication.class, args);
	}

}
