package mydatachoice.icd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ValuesetService {

    private ResourceLoader resourceLoader;


    @Autowired
    public ValuesetService(ResourceLoader resourceLoader, MappingService mappingService){
        this.resourceLoader = resourceLoader;
    }


    public Set<String> loadValueset(){
        Set<String> valueset = new HashSet<>();

        String filepath = "classpath:dataset/generated/valueset.csv";
        String line = "";
        String cvsSplitby = ",";

        Resource resource = resourceLoader.getResource(filepath);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            while ((line = br.readLine()) != null){
                String[] strings = line.split(cvsSplitby);
                valueset.add(strings[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valueset;
    }

    public List<String> getSensitiveCategory(){
        List<String> sensCategory = new ArrayList<>();
        String filepath = "classpath:dataset/valueset.csv";
        String line = "";
        String cvsSplitby = ",";

        Resource resource = resourceLoader.getResource(filepath);

        try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            while ((line = br.readLine()) != null){
                String[] strings = line.split(cvsSplitby);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sensCategory;
    }


}
