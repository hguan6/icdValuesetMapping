package mydatachoice.icd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ICD10Service{
    private ResourceLoader resourceLoader;
    @Autowired
    public ICD10Service(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    public Map loadICD10CMasMap(){
        Map icd10cm = new HashMap();

        String filepath = "classpath:dataset/icd10cm_codes_2018.txt";
        String line = "";
        String splitby = "\\s+";

        Resource resource = resourceLoader.getResource(filepath);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            while ((line = br.readLine()) != null){
                String[] strings = line.split(splitby);
                String code = null;
                if(strings[0].length() > 3){
                    code = strings[0].substring(0,3) + "." + strings[0].substring(3);
                } else {
                    code = strings[0].substring(0,3);
                }
                StringBuilder description = new StringBuilder();
                for(int i = 1; i < strings.length; i++){
                    description.append(strings[i]);
                    description.append(" ");
                }
                icd10cm.put(code,description);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return icd10cm;
    }
}
