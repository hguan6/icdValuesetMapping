package mydatachoice.icd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ValuesetService {

    private ResourceLoader resourceLoader;
    @Autowired
    public ValuesetService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }


    public Set<String> loadValueset(){
        Set<String> valueset = new HashSet<>();

        String filepath = "classpath:dataset/valueset.csv";
        String line = "";
        String cvsSplitby = ",";

        Resource resource = resourceLoader.getResource(filepath);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            while ((line = br.readLine()) != null){
                valueset.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valueset;
    }

}
