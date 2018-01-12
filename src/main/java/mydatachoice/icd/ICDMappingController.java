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

    private Model model;
    private Set<String> valueset;
    private Set<String> icd10set;
    private Map<String, String> icd10map;

    @Autowired
    public ICDMappingController(MappingService mappingService, ValuesetService valuesetService, ICD10Service icd10Service){
        this.mappingService = mappingService;
        this.valuesetService = valuesetService;
        this.icd10Service = icd10Service;

        this.model = ModelFactory.createDefaultModel();
        model.read("dataset/ICD10CM.ttl");
        System.out.println("loadModel success");

        this.valueset = valuesetService.loadValueset();
        this.icd10map = icd10Service.loadICD10CMasMap();
//        this.icd10set = icd10map.keySet();
    }

    @RequestMapping(value = "/compare", method = RequestMethod.GET)
    public List<Set<String>> compareCompleteValueSet(){
        return mappingService.compareCompleteValueset(model, valueset, icd10map);
    }

    @RequestMapping(value = "/relationship/complete", method = RequestMethod.GET)
    public Set<String> getCompleteValuesetByRelation(){
        return mappingService.getCompleteValuesetByRelation(model, valueset);
    }

    @RequestMapping(value = "/relationship/subclasses/{code}", method = RequestMethod.GET)
    public Set<String> getSubclassesByRelationship(@PathVariable String code){
        return mappingService.getSubclassesByRelation(model, code);
    }


    @RequestMapping(value = "/valueset/rules/code", method = RequestMethod.GET)
    public Set<String> getValuesetByRules(){
        return new TreeSet<String>(mappingService.getCompleteValuesetWithDescriptionByRules(icd10map).keySet());
    }

    @RequestMapping(value = "/valueset/rules/full", method = RequestMethod.GET)
    public Map getValuesetWithDescriptionByRules(){
        TreeMap completeValueSetByRules = new TreeMap(mappingService.getCompleteValuesetWithDescriptionByRules(icd10map));
        return completeValueSetByRules;
    }

    @RequestMapping(value = "/allICD10/code", method = RequestMethod.GET)
    public Set<String> getAllIcd10Codes(){ return new TreeSet<String>(this.icd10set); }

    @RequestMapping(value = "/allICD10/full", method = RequestMethod.GET)
    public Map getAllIcd10full() { return new TreeMap(this.icd10map); }



    @RequestMapping(value = "/valueset/origin", method = RequestMethod.GET)
    public Set<String> getValueSet() { return new TreeSet<String>(this.valueset); }
}
