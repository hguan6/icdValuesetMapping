package mydatachoice.icd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class ICD10Service{
    private ResourceLoader resourceLoader;
    @Autowired
    public ICD10Service(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    public Map loadICD10CMasMap(){
        Map icd10cm = new HashMap();

        String filepath = "classpath:dataset/icd10cm_order_2018.txt";
        String line = "";
//        String splitby = "\\s+";

        Resource resource = resourceLoader.getResource(filepath);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            while ((line = br.readLine()) != null){
                String code = line.substring(6,13).trim();
                String description = line.substring(77).trim();
                if(code.length() > 3){
                    code = code.substring(0,3) + "." + code.substring(3);
                }
                icd10cm.put(code,description);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return icd10cm;
    }
}
