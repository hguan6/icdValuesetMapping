package mydatachoice.icd;


import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class MappingService {

/*
    This method compares complete value set obtained by parent/children relationship and string mapping
 */
    public List<Set<String>> compareCompleteValueset(Model model, Set<String> valueset, Map<String, String> icd10cm) {
        Set<String> valuesetByRelation = getCompleteValuesetByRelation(model, valueset);
        Set<String> valuesetByRules = getCompleteValuesetWithDescriptionByRules(icd10cm).keySet();

        List<Set<String>> diff = new ArrayList<>();
        Set<String> valuesetByRelationCopy = new HashSet<>();
        valuesetByRelationCopy.addAll(valuesetByRelation);
        if (valuesetByRelationCopy.removeAll(valuesetByRules)) {
            diff.add(new TreeSet<String>(valuesetByRelationCopy));
        }
        if (valuesetByRules.removeAll(valuesetByRelation)) {
            diff.add(new TreeSet<String>(valuesetByRules));
        }
        return  diff;
    }

/*
    This method gets complete value set by parent/children relationship
 */
    public Set<String> getCompleteValuesetByRelation(Model model, Set<String> valueset){

        Set<String> completeValueset = new HashSet<>();

        for(String code : valueset){
            String codeURI = "http://purl.bioontology.org/ontology/ICD10CM/" + code;
            completeValueset.add(codeURI);
            addSubclasses(codeURI, completeValueset, model);
        }

        Set<String> result = new HashSet<>();
        for(String codeURI : completeValueset){
            result.add(codeURI.substring(45));
        }
        return result;
    }


    public Set<String> getSubclassesByRelation(Model model, String code){ // This method gets all subclasses for a single code
        Set<String> subclasses = new HashSet<>();
        String codeURI = "http://purl.bioontology.org/ontology/ICD10CM/" + code;
        subclasses.add(codeURI);
        addSubclasses(codeURI, subclasses, model);

        Set<String> result = new HashSet<>();
        for(String subclass : subclasses){
            result.add(subclass.substring(45));
        }
        return result;
    }

    private void addSubclasses(String codeURI, Set<String> subclasses, Model model) {
        Set<String> children = getDirectSubclasses(codeURI, model);

        if(!children.isEmpty()) {
            for(String child : children){
                subclasses.add(child);
                addSubclasses(child, subclasses, model);
            }
        }
    }

    private Set<String> getDirectSubclasses(String codeURI, Model model) {
        Set<String> children = new HashSet<>();
        String queryString =
                        "prefix skos: <http://www.w3.org/2004/02/skos/core#>" +
                        "prefix owl:  <http://www.w3.org/2002/07/owl#>" +
                        "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>" +
                        "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" +
                        "prefix umls: <http://bioportal.bioontology.org/ontologies/umls/>" +
                        "SELECT * WHERE { ?child rdfs:subClassOf <" +
                        codeURI +
                        ">  ." +
                        "}";

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            ResultSet results = qexec.execSelect();
            while(results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                Resource resource = solution.getResource("child");
                String uri = resource.getURI();     // Get uri from resource

                children.add(uri);
            }
        }
        return children;
    }

/*
    This method gets complete value set by string mapping
 */
    public Map<String, String> getCompleteValuesetWithDescriptionByRules(Map<String, String> icd10cm){

        String pattern = "^(F06|F1|F2|F3|F4|F50|F51|F52|F53|F55|F6|F7|F8|F9|G47|K20|K70|N44.2|N44.8|N50.3|" +
                "N50.8|N52.1|N52.9|N53.12|N94.1|N94.8|N94.9|P04.3|P04.4|P04.9|P93.8|R23.2|R37|R78.2|T14.91|T50.99|T51|T74|T76|T40)\\S*";
        Map<String, String> completeValueset = icd10cm.entrySet().stream()
                .filter(map -> map.getKey().matches(pattern))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        String exceptionPattern = "^(F13|F28|F51.11|F64.0|F80.82|G47.2|G47.3|G47.5|T50.996)\\S*";
        completeValueset.keySet().removeIf(key -> key.matches(exceptionPattern));
        return completeValueset;
    }
    /*
        This method gets sensitive category of a code if the code is sensitive
     */
    public String getSensitiveCategory(String code, Map<String, String> vsByRules){
        
        if(vsByRules.containsKey(code)){
            Set<String> sensCategories = new HashSet<>();
            if(code.matches("^((F06|G47|R37|T14|T74|T76)|((F2|F3|F4|F5|F6|F7|F8|F9)[0-9])|(N[0-9][0-9]))(\\.[0-9]+)?$")) {
                sensCategories.add("C2S Mental Health Disorders");
            }
            if(code.matches("^(((F10|K70|T51)(\\.[0-9]+)?))|((P04.3|P04.9|R23.2)[0-9]*)$")) {
                sensCategories.add("C2S Alcohol Use Disorders");
            }
            if(code.matches("^F11(\\.[0-9]+)?$")) {
                sensCategories.add("C2S Opioids");
            }
            if(code.matches("^F12(\\.[0-9]+)?$")){
                sensCategories.add("C2S Cannabis Use Disorders");
            }
            if(code.matches("^((F14|T50|T40)(\\.[0-9]+)?)|((P04.4|P04.9|P93.8|R78.2)[0-9]*)$")){
                sensCategories.add("C2S Cocaine Use Disorder");
            }
            if(code.matches("^F15(\\.[0-9]+)?$")){
                sensCategories.add("C2S Amphetamine Use Disorders");
            }
            if(code.matches("^(F16|T40)(\\.[0-9]+)?$")){
                sensCategories.add("C2S Hallucinogens");
            }
            if(code.matches("^F17(\\.[0-9]+)?$")){
                sensCategories.add("C2S Tobacco Use Disorders");
            }
            if(code.matches("^(F18(\\.[0-9]+)?)|((F17.200|F19.20|F19.21)[0-9]*)$")){
                sensCategories.add("C2S Inhalants");
            }
            if(code.matches("^F19(\\.[0-9]+)?$")){
                sensCategories.add("C2S Other Psychoactive Substance Use Disorder");
                sensCategories.add("C2S Sedative Hypnotic, or anxiolytic related disorders");
            }
            if(code.matches("^K20(\\.[0-9]+)?$")){
                sensCategories.add("Unknown");
            }
            StringBuilder result = new StringBuilder();
            for(String sensCategory : sensCategories){
                result.append(sensCategory);
                result.append(" & ");
            }
            return result.delete(result.length() - 3, result.length()).toString();
        } else{
            return "The code is insensitive or it does not exist.";
        }

    }
}
