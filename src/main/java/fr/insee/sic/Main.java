package fr.insee.sic;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.vtl.jackson.TrevasModule;
import fr.insee.vtl.model.Dataset;

import javax.script.*;
import java.net.URL;

public class Main {

    public static void main(String[] args) {

        /* Mapper */
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new TrevasModule());

        /* Bindings */
        Bindings bindings = new SimpleBindings();

        try {

            /* Put dataset in bindings */

            URL url = new URL("https://sic.pages.innovation.insee.eu/service-agregation-echange-de-donnees/datasets/dataset.json");

            Dataset ds = mapper.readValue(url, Dataset.class);

            bindings.put("ds_in", ds);

            /* Show dataset */

            Dataset inputDataset = (Dataset) bindings.get("ds_in");

            System.out.println("\n --- Input structure:");
            System.out.println(inputDataset.getDataStructure().values());
            System.out.println("\n --- Input data points:");
            System.out.println(inputDataset.getDataPoints());

        } catch (Exception e){
            e.printStackTrace();
        }

        /* Engine */
        ScriptEngine engine = new ScriptEngineManager()
                .getEngineByName("vtl");
        ScriptContext context = engine.getContext();
        context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        try {

            /* Eval script */

            String script = "ds_out := ds_in [calc DENSITY := POP / AREA];";

            engine.eval(script);

            /* Show result */

            Dataset outputDataset = (Dataset) bindings.get("ds_out");

            System.out.println("\n --- Output structure:");
            System.out.println(outputDataset.getDataStructure().values());
            System.out.println("\n --- Output data points:");
            System.out.println(outputDataset.getDataPoints());

            System.out.println("\n OKK");

        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }

}
