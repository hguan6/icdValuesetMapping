package mydatachoice.icd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Service
public class ICD10Service{
    private ResourceLoader resourceLoader;
    @Autowired
    public ICD10Service(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    public Set<String> loadICD10CM(){
        Set<String> icd10cm = new HashSet<>();

        String filepath = "classpath:dataset/icd10cm_codes_2018.txt";
        String line = "";
        String splitby = " ";

        Resource resource = resourceLoader.getResource(filepath);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            while ((line = br.readLine()) != null){
                String[] strings = line.split(splitby);
                String str = strings[0].substring(0,3) + "." + strings[0].substring(3);
                icd10cm.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return icd10cm;
    }
}
