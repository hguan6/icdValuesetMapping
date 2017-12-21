package mydatachoice.icd;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class MappingService {


/*
    This method compares complete value set obtained by parent/children relationship and string mapping
 */
    public List<Set<String>> compareCompleteValueset(Model model, Set<String> valueset, Set<String> icd10cm) {
        Set<String> valuesetByRelation = getCompleteValuesetByRelation(model, valueset);
        Set<String> valuesetByStringMapping = getCompleteValuesetByStringMapping(valueset, icd10cm);

        List<Set<String>> diff = new ArrayList<>();
        Set<String> valuesetByRelationCopy = new HashSet<>();
        valuesetByRelationCopy.addAll(valuesetByRelation);
        if (valuesetByRelationCopy.removeAll(valuesetByStringMapping)) {
            diff.add(valuesetByRelationCopy);
        }
        if (valuesetByStringMapping.removeAll(valuesetByRelation)) {
            diff.add(valuesetByStringMapping);
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
    public Set<String> getCompleteValuesetByStringMapping(Set<String> valueset, Set<String> icd10cm){
        Set<String> completeValueset = new HashSet<>();

        valueset.remove("D69.2");
        valueset.remove("Z79.899");
        for(String code : valueset){
            if(code.substring(0,1) == "T") {
                valueset.remove(code);
            }
        }
        completeValueset.addAll(valueset);

        for(String code : icd10cm){

            if(code.length() >=7 && code.substring(0,7) == "T50.996"){
                completeValueset.add(code);
                continue;
            }
            else if(code.length() >=6 && code.substring(0,6) == "T14.91"){
                completeValueset.add(code);
                continue;
            }
            else if(code.length() >= 5) {
                String str0 = code.substring(0, 5);
                if (code.length() >= 5 && (str0 == "G47.0" || str0 == "G47.1" || str0 == "G47.4" || str0 == "G47.6" ||
                        str0 == "G47.8" || str0 == "G47.9" || str0 == "N94.1" || str0 == "N94.8" || str0 == "N94.9")) {
                    completeValueset.add(code);
                    continue;
                }
            }
            else {
                String str1 = code.substring(0, 3);
                if (str1 == "K70" || str1 == "T51" || str1 == "T74" || str1 == "T76") {
                    completeValueset.add(code);
                }
            }
        }

        return completeValueset;
    }

}