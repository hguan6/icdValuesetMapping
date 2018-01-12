package mydatachoice.icd;

import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.awt.datatransfer.StringSelection;
import java.util.*;


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

//
//    public Set<String> getCompleteValuesetByRules(Set<String> icd10cm){
//        Set<String> completeValueset = new HashSet<>();
//        String pattern = "^(F06|F1|F2|F3|F4|F50|F51|F52|F53|F55|F6|F7|F8|F9|G47|K20|K70|N44.2|N44.8|N50.3|" +
//                "N50.8|N52.1|N52.9|N53.12|N94.1|N94.8|N94.9|P04.3|P04.4|P04.9|P93.8|R23.2|R37|R78.2|T14.91|T50.99|T51|T74|T76|T40)\\S*";
//        for(String code : icd10cm){
//            if(code.matches(pattern)){
//                completeValueset.add(code);
//            }
//        }
//        String exceptionPattern = "^(F13|F28|F51.11|F64.0|G47.2|G47.3|G47.5|T50.996)\\S*";
//        Iterator<String> itr = completeValueset.iterator();
//        while(itr.hasNext()){
//            String code = itr.next();
//            if(code.matches(exceptionPattern)){
//                itr.remove();
//            }
//        }
//        return completeValueset;
//    }

    public Map<String, String> getCompleteValuesetWithDescriptionByRules(Map<String, String> icd10cm){
        Map completeValueset = new HashMap();
        String pattern = "^(F06|F1|F2|F3|F4|F50|F51|F52|F53|F55|F6|F7|F8|F9|G47|K20|K70|N44.2|N44.8|N50.3|" +
                "N50.8|N52.1|N52.9|N53.12|N94.1|N94.8|N94.9|P04.3|P04.4|P04.9|P93.8|R23.2|R37|R78.2|T14.91|T50.99|T51|T74|T76|T40)\\S*";
        Iterator<String> itr = icd10cm.keySet().iterator();
        while(itr.hasNext()){
            String code = itr.next();
            if (code.matches(pattern)){ completeValueset.put(code, icd10cm.get(code)); }
        }

        String exceptionPattern = "^(F13|F28|F51.11|F64.0|F80.82|G47.2|G47.3|G47.5|T50.996)\\S*";
        itr = completeValueset.keySet().iterator();
        while(itr.hasNext()){
            String code = itr.next();
            if(code.matches(exceptionPattern)){ itr.remove(); }
        }
        return completeValueset;
    }
}
