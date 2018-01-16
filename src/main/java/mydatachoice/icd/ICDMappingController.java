package mydatachoice.icd;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ICDMappingController {

    private MappingService mappingService;
    private ValuesetService valuesetService;
    private ICD10Service icd10Service;

//    private Model model;
//    private Set<String> originalValueset;
    private Map<String, String> icd10map;

    private Map<String, String> vsByRules;

    @Autowired
    public ICDMappingController(MappingService mappingService, ValuesetService valuesetService, ICD10Service icd10Service){
        this.mappingService = mappingService;
        this.valuesetService = valuesetService;
        this.icd10Service = icd10Service;

//        this.model = ModelFactory.createDefaultModel();
//        model.read("dataset/ICD10CM.ttl");
//        System.out.println("loadModel success");
//
//        this.originalValueset = valuesetService.loadValueset();
        this.icd10map = icd10Service.loadICD10CMasMap();

        this.vsByRules = mappingService.getCompleteValuesetWithDescriptionByRules(icd10map);
    }

//    @RequestMapping(value = "/compare", method = RequestMethod.GET)
//    public List<Set<String>> compareCompleteValueSet(){
//        return mappingService.compareCompleteValueset(model, originalValueset, icd10map);
//    }
//
//    @RequestMapping(value = "/valueset/relationship/complete", method = RequestMethod.GET)
//    public Set<String> getCompleteValuesetByRelation(){
//        return mappingService.getCompleteValuesetByRelation(model, originalValueset);
//    }
//
//    @RequestMapping(value = "/valueset/relationship/subclasses/{code}", method = RequestMethod.GET)
//    public Set<String> getSubclassesByRelationship(@PathVariable String code){
//        return mappingService.getSubclassesByRelation(model, code);
//    }


    @RequestMapping(value = "/valueset/rules/code", method = RequestMethod.GET)
    public Set<String> getValuesetByRules(){
        return new TreeSet<String>(vsByRules.keySet());
    }

    @RequestMapping(value = "/valueset/rules/full", method = RequestMethod.GET)
    public Map<String, String> getValuesetWithDescriptionByRules(){
        return new TreeMap(vsByRules);
    }

    @RequestMapping(value = "/allICD10/code", method = RequestMethod.GET)
    public Set<String> getAllIcd10Codes(){ return new TreeSet<String>(this.icd10map.keySet()); }

    @RequestMapping(value = "/allICD10/full", method = RequestMethod.GET)
    public Map<String, String> getAllIcd10full() { return new TreeMap(this.icd10map); }

//
//    @RequestMapping(value = "/valueset/origin", method = RequestMethod.GET)
//    public Set<String> getValueSet() { return new TreeSet<String>(this.originalValueset); }

    @RequestMapping(value = "/sensCategory/{code:.+}", method = RequestMethod.GET)
    public String getSensitiveCategory(@PathVariable String code) { return mappingService.getSensitiveCategory(code, vsByRules); }
}
