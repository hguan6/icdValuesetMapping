package mydatachoice.icd;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class ICDMappingController {

    private MappingService mappingService;
    private ValuesetService valuesetService;
    private  ICD10Service icd10Service;

    private Model model;
    private Set<String> valueset;
    private Set<String> icd10cm;

    @Autowired
    public ICDMappingController(MappingService mappingService, ValuesetService valuesetService, ICD10Service icd10Service){
        this.mappingService = mappingService;
        this.valuesetService = valuesetService;
        this.icd10Service = icd10Service;

        this.model = ModelFactory.createDefaultModel();
        model.read("dataset/ICD10CM.ttl");
        System.out.println("loadModel success");

        this.valueset = valuesetService.loadValueset();
        this.icd10cm = icd10Service.loadICD10CM();
    }

    @RequestMapping(value = "/compare", method = RequestMethod.GET)
    public List<Set<String>> compareCompleteValueSet(){
        return mappingService.compareCompleteValueset(model, valueset, icd10cm);
    }

    @RequestMapping(value = "/relationship/complete", method = RequestMethod.GET)
    public Set<String> getCompleteValueset(){
        return mappingService.getCompleteValuesetByRelation(model, valueset);
    }

    @RequestMapping(value = "/relationship/subclasses/{code}", method = RequestMethod.GET)
    public Set<String> getSubclassesByRelationship(@PathVariable String code){
        return mappingService.getSubclassesByRelation(model, code);
    }


    @RequestMapping(value = "/stringmapping/complete", method = RequestMethod.GET)
    public Set<String> getSubclassesByStringMapping(){
        return mappingService.getCompleteValuesetByStringMapping(valueset, icd10cm);
    }

    @RequestMapping(value = "/allICD10", method = RequestMethod.GET)
    public Set<String> getAllICD10Codes(){
        return icd10Service.loadICD10CM();
    }

}
